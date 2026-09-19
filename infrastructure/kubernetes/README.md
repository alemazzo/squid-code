# Manifests Kubernetes

Tutti i manifest sono pensati per il **single node k3s** dietro Caddy descritto nel README
principale. Nessuno di questi file crea un ingress: l'esposizione pubblica è fatta da Caddy
(`scripts/deploy-k3s.sh` scrive un blocco in `/etc/caddy/sites/` per ogni servizio).

| File | Contenuto |
|---|---|
| `namespace.yaml` | namespace `apps` |
| `config.yaml` | ConfigMap `squidcode-config` (origini pubbliche usate dalla CORS del backend) |
| `mongodb.yaml` | MongoDB single replica + PVC `local-path` + Service `squidcode-mongo` |

I manifest dei servizi stanno accanto al codice, in `services/<servizio>/infrastructure/kubernetes/`:
`deployment.yaml` (+ `service.yml` per leetcode) con immagine `ghcr.io/alemazzo/squid-code/<servizio>`,
`imagePullPolicy: IfNotPresent`, namespace `apps` e un NodePort fisso.

## NodePort

| Servizio | NodePort | Container port |
|---|---|---|
| frontend | 30090 | 80 |
| backend | 30091 | 8000 |
| leetcode | 30092 | 3000 |

I NodePort sono allocati staticamente perché costruiti in locale con `nerdctl` vengono letti
dal containerd di k3s per nome: nessun registry intermedio, e il blocco Caddy deve puntare a un
numero noto. Se il numero cambia va aggiornato anche in `scripts/deploy-k3s.sh`.

## Cosa non c'è più

- `issuer.yaml`, `role.yaml`, `account.yaml`, `secret.yaml`: service account e ClusterIssuer di
  cert-manager per il cluster DigitalOcean, non servono (il TLS lo fa Caddy con Let's Encrypt).
- `ingress.yaml`: era l'ingress nginx di DO, sostituito da Caddy.
- gli `scaler.yaml` (HPA): `metrics-server` non è installato, quindi gli HPA non avrebbero
  metriche; la CPU del nodo è fissa a 2 vCPU e i servizi girano con 1 replica.
