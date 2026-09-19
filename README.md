# squid-code

Coding practice platform: solve a random LeetCode problem, submit a solution and get it
evaluated in a sandboxed runner.

## Servizi

| Servizio | Tecnologia | Porta | Immagine |
|---|---|---|---|
| `services/frontend-service` | Svelte 5 + Vite, servito da nginx | 80 | `ghcr.io/alemazzo/squid-code/frontend` |
| `services/backend-service` | Kotlin / Micronaut (MongoDB, Google OAuth, JWT) | 8000 | `ghcr.io/alemazzo/squid-code/backend` |
| `services/leetcode-service` | Express, proxy verso l'API LeetCode | 3000 | `ghcr.io/alemazzo/squid-code/leetcode` |
| `services/runners/*` | sandbox di esecuzione (java, python) | — | `ghcr.io/alemazzo/squid-code/runner-*` |

Il frontend parla con gli altri due servizi via HTTP: gli URL sono **iniettati al build**
(`VITE_API_BASE_URL`, `VITE_LEETCODE_BASE_URL`, vedi `src/config.ts`) perché il bundle è
statico. Il backend legge MongoDB, l'origine CORS e il client id di Google dall'ambiente
(`MONGODB_URI`, `CORS_ALLOWED_ORIGIN`, `GOOGLE_CLIENT_ID`), con default per lo sviluppo locale.

## Dove gira

Tutto gira su un **single node k3s** (VPS `5.175.171.84`, 2 vCPU / 7.7 GB) nel namespace
`squidcode`, dietro **Caddy** che è l'unico ingress pubblico e termina il TLS:

```
internet -> Caddy (:80/:443) -> 127.0.0.1:<NodePort> -> pod
```

I NodePort sono bindati solo su loopback: **non sono raggiungibili da internet**, l'unico
punto di entrata è Caddy. Non assegnare :80/:443 a un pod.

| URL | Servizio | NodePort |
|---|---|---|
| https://squidcode.5-175-171-84.sslip.io | frontend | 30090 |
| https://api.squidcode.5-175-171-84.sslip.io | backend | 30091 |
| https://leetcode.squidcode.5-175-171-84.sslip.io | leetcode-service | 30092 |

`5-175-171-84.sslip.io` è un wildcard DNS pubblico che risolve su questo VPS: per usare un
dominio proprio basta puntarci un record `A` e aggiornare `DOMAIN_*` (script) e `SITE_*`/
`frontend-origin` (workflow e ConfigMap).

## Dati di MongoDB

MongoDB scrive in una directory che **sopravvive al namespace**: il PVC `squidcode-mongo-data` è
pre-bound a un PV statico con `persistentVolumeReclaimPolicy: Retain`
(`infrastructure/kubernetes/mongo-static-pv.yaml`), che punta a
`/var/lib/rancher/k3s/storage/pvc-ef23b3a7-36b9-4f10-a904-056ab4812ae9_apps_squidcode-mongo-data`.
Il path porta ancora il vecchio namespace `apps`: è il percorso reale su disco, non va rinominato.
Dettagli, ordine di apply e procedura di recupero: `infrastructure/kubernetes/README.md`.

## Deploy

Automatico: ogni push su `main` che tocca un servizio ricostruisce l'immagine, la pubblica su
GHCR e la applica al cluster via SSH (`.github/workflows/*.yml`).
Manuale, dalla macchina di deploy (o dal VPS stesso):

```bash
./scripts/deploy-k3s.sh                    # build + apply + Caddy + smoke test
./scripts/deploy-k3s.sh --no-build         # solo apply/re-expose
./scripts/deploy-k3s.sh --only frontend    # un solo componente
```

Lo script costruisce con `nerdctl`/buildkit (che scrivono direttamente nel containerd di k3s)
con lo **stesso nome immagine** usato dai manifest: `imagePullPolicy: IfNotPresent` fa
scegliere l'immagine locale, senza passare da un registry.

## Secrets e variabili richieste su GitHub

| Nome | Uso |
|---|---|
| `VPS_HOST` | host del VPS (5.175.171.84) |
| `VPS_USER` | utente ssh con accesso a `kubectl` (root) |
| `VPS_SSH_KEY` | chiave privata di deploy |
| `GITHUB_TOKEN` | fornito da Actions: push su GHCR |

Opzionale, per far funzionare il login Google: crea nel cluster il secret
`squidcode-backend-secrets` con la chiave `google-client-id`.

```bash
kubectl -n squidcode create secret generic squidcode-backend-secrets \
  --from-literal=google-client-id=<client-id>
```

## Sviluppo locale

```bash
# backend  (serve MongoDB su localhost:27017)
cd services/backend-service/application && ./gradlew run

# leetcode proxy
cd services/leetcode-service/application && npm install && npm start

# frontend (http://localhost:8080, punta a localhost:8000/3000 di default)
cd services/frontend-service/application && npm install && npm run dev
```

## Limiti noti del cluster

- **niente autoscaling**: `metrics-server` è disattivato, quindi gli HPA non hanno metriche.
  Le repliche sono fisse a 1 per servizio.
- **niente alta disponibilità**: nodo singolo, se il VPS cade i servizi sono giù.
- **2 vCPU**: il collo di bottiglia sono i build, non la RAM. Le immagini si costruiscono una
  volta e si riusano.
- **Let's Encrypt**: 5 certificati identici a settimana per dominio; `sslip.io` è condiviso da
  tutti, quindi un'eventuale rate limit va aspettato.
