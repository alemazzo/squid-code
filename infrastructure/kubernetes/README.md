# Manifests Kubernetes

Tutti i manifest sono pensati per il **single node k3s** dietro Caddy descritto nel README
principale. Nessuno di questi file crea un ingress: l'esposizione pubblica è fatta da Caddy
(`scripts/deploy-k3s.sh` scrive un blocco in `/etc/caddy/sites/` per ogni servizio).

| File | Contenuto |
|---|---|
| `namespace.yaml` | namespace `squidcode` |
| `config.yaml` | ConfigMap `squidcode-config` (origini pubbliche usate dalla CORS del backend) |
| `mongodb.yaml` | MongoDB single replica (Deployment) + Service `squidcode-mongo` |
| `mongodb-pvc.yaml` | PVC `squidcode-mongo-data`, pre-bound al volume statico |
| `mongo-static-pv.yaml` | PV statico `squidcode-mongo-data-static` (`Retain`) che tiene la directory dati |

I manifest dei servizi stanno accanto al codice, in `services/<servizio>/infrastructure/kubernetes/`:
`deployment.yaml` (+ `service.yml` per leetcode) con immagine `ghcr.io/alemazzo/squid-code/<servizio>`,
`imagePullPolicy: IfNotPresent`, namespace `squidcode` e un NodePort fisso.

## Storage di MongoDB: claim pre-bound su un PV statico

Il volume dati di MongoDB **non** è più creato dinamicamente dal provisioner `local-path`: il PVC
`squidcode-mongo-data` è **pre-bound** (`spec.volumeName: squidcode-mongo-data-static`) al
PersistentVolume statico dichiarato in `mongo-static-pv.yaml`, che punta alla directory reale

```
/var/lib/rancher/k3s/storage/pvc-ef23b3a7-36b9-4f10-a904-056ab4812ae9_apps_squidcode-mongo-data
```

Il nome della directory contiene ancora il vecchio namespace `apps`: è il path reale su disco, non
va rinominato (rinominarlo = perdere i dati). Il PV ha `persistentVolumeReclaimPolicy: Retain`,
quindi la cancellazione del PVC non cancella i dati.

Regole da rispettare:

- `spec.volumeName` resta **anche nel manifest**: `mongodb.yaml` in passato non lo dichiarava, e
  `kubectl apply` tentava di azzerare quel campo (`spec is immutable after creation`), facendo
  fallire a ogni run il workflow `infrastructure`. Il manifest deve descrivere la realtà del cluster.
- PVC e PV sono applicati dal workflow **solo se assenti** (`kubectl get ... || kubectl apply`),
  perché lo spec di un claim già legato e quello di un PV sono immutabili. I file si riconoscono dal
  nome: `*pvc*.yaml` e `*-pv.yaml`, che il loop generico salta.
- Se il PVC viene cancellato e ricreato, il PV passa a `Released` e conserva un `claimRef` puntato al
  claim vecchio (UID diverso): il claim nuovo non si lega finché quel `claimRef` non viene rimosso
  (`kubectl patch pv squidcode-mongo-data-static --type=json -p '[{"op":"remove","path":"/spec/claimRef"}]'`),
  poi il pre-binding riporta il claim sulla stessa directory e i dati sono di nuovo lì.
- Ricreando il cluster da zero vale l'ordine del workflow: prima `mongo-static-pv.yaml` (e il
  relativo path su disco), poi `mongodb-pvc.yaml`, poi `mongodb.yaml`.

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
