#!/usr/bin/env bash
#
# Deploy squid-code on the single node k3s VPS (5.175.171.84).
#
#   ./scripts/deploy-k3s.sh                  # build images, apply, expose, verify
#   ./scripts/deploy-k3s.sh --no-build       # only re-apply / re-expose
#   ./scripts/deploy-k3s.sh --only frontend  # restrict to one component
#
# It is the manual/local counterpart of the GitHub Actions workflows: the CI
# builds the same images, pushes them to the registry and applies the same
# manifests over SSH. Both paths use the exact image names below, so an image
# built here is picked up by the cluster without any registry round trip
# (`imagePullPolicy: IfNotPresent`).
#
# Requires (on the VPS): k3s, kubectl, nerdctl/buildkit, Caddy, curl.
set -euo pipefail

# The Caddy site blocks are read by the `caddy` user: make sure everything this
# script creates stays world readable whatever the caller's umask is.
umask 022

REPO_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
export KUBECONFIG="${KUBECONFIG:-/etc/rancher/k3s/k3s.yaml}"
export PATH="/usr/local/bin:${PATH}"

NAMESPACE="${K8S_NAMESPACE:-${NAMESPACE:-squidcode}}"
REGISTRY="${REGISTRY:-ghcr.io/alemazzo/squid-code}"
SUFFIX="${K8S_DOMAIN_SUFFIX:-5-175-171-84.sslip.io}"
DOMAIN_FRONTEND="${DOMAIN_FRONTEND:-squidcode.${SUFFIX}}"
DOMAIN_BACKEND="${DOMAIN_BACKEND:-api.squidcode.${SUFFIX}}"
DOMAIN_LEETCODE="${DOMAIN_LEETCODE:-leetcode.squidcode.${SUFFIX}}"
SITES_DIR="${CADDY_SITES_DIR:-/etc/caddy/sites}"

# Caddy terminates TLS on :443 and proxies to these loopback-only NodePorts,
# which are pinned in the service manifests.
NODEPORT_FRONTEND=30090
NODEPORT_BACKEND=30091
NODEPORT_LEETCODE=30092

BUILD=1
COMPONENTS=()

die()  { printf '\033[31merrore:\033[0m %s\n' "$*" >&2; exit 1; }
ok()   { printf '\033[32m⟶\033[0m %s\n' "$*"; }
info() { printf '  %s\n' "$*"; }

usage() { sed -n '2,15p' "$0" | sed 's/^# \{0,1\}//'; }

while [[ $# -gt 0 ]]; do
  case "$1" in
    --no-build) BUILD=0; shift ;;
    --only)     COMPONENTS+=("${2:-}"); shift 2 ;;
    -h|--help)  usage; exit 0 ;;
    -*)         die "opzione sconosciuta: $1" ;;
    *)          COMPONENTS+=("$1"); shift ;;
  esac
done

if [[ ${#COMPONENTS[@]} -eq 0 ]]; then
  COMPONENTS=(backend frontend leetcode)
fi

for c in "${COMPONENTS[@]}"; do
  case "$c" in backend|frontend|leetcode) ;; *) die "componente sconosciuto: $c" ;; esac
done

in_components() { local want="$1"; for c in "${COMPONENTS[@]}"; do [[ "$c" == "$want" ]] && return 0; done; return 1; }

command -v kubectl >/dev/null || die "kubectl assente"
systemctl is-active --quiet k3s || die "k3s non attivo"

# ---------------------------------------------------------------- build -----

build_component() {
  case "$1" in
    backend)
      nerdctl build -t "${REGISTRY}/backend:latest" \
        "${REPO_ROOT}/services/backend-service/application"
      ;;
    frontend)
      nerdctl build -t "${REGISTRY}/frontend:latest" \
        --build-arg "VITE_API_BASE_URL=https://${DOMAIN_BACKEND}" \
        --build-arg "VITE_LEETCODE_BASE_URL=https://${DOMAIN_LEETCODE}" \
        "${REPO_ROOT}/services/frontend-service/application"
      ;;
    leetcode)
      nerdctl build -t "${REGISTRY}/leetcode:latest" \
        "${REPO_ROOT}/services/leetcode-service/application"
      ;;
  esac
}

if [[ "$BUILD" == 1 ]]; then
  command -v nerdctl >/dev/null || die "nerdctl assente"
  systemctl is-active --quiet buildkit || die "buildkit non attivo"
  for c in "${COMPONENTS[@]}"; do
    ok "build ${REGISTRY}/${c}:latest"
    build_component "$c" >/dev/null
  done
fi

# ------------------------------------------------------------- apply --------

ok "applico namespace, configmap e mongodb"
kubectl apply -f "${REPO_ROOT}/infrastructure/kubernetes/namespace.yaml" >/dev/null
kubectl -n "$NAMESPACE" create configmap squidcode-config \
  --from-literal="frontend-origin=https://${DOMAIN_FRONTEND}" \
  --from-literal="backend-origin=https://${DOMAIN_BACKEND}" \
  --from-literal="leetcode-origin=https://${DOMAIN_LEETCODE}" \
  --dry-run=client -o yaml | kubectl apply -f - >/dev/null
kubectl -n "$NAMESPACE" apply -f "${REPO_ROOT}/infrastructure/kubernetes/mongodb.yaml" >/dev/null
kubectl -n "$NAMESPACE" rollout status deploy/squidcode-mongo --timeout=300s >/dev/null
info "mongodb pronto"

for c in "${COMPONENTS[@]}"; do
  case "$c" in
    backend)  path="services/backend-service/infrastructure/kubernetes";  deploy="squidcode-backend" ;;
    frontend) path="services/frontend-service/infrastructure/kubernetes"; deploy="squidcode-frontend" ;;
    leetcode) path="services/leetcode-service/infrastructure/kubernetes"; deploy="squidcode-leetcode" ;;
  esac
  ok "applico ${deploy}"
  kubectl -n "$NAMESPACE" apply -f "${REPO_ROOT}/${path}/" >/dev/null
  # The manifests reference a moving tag (`:latest`): a freshly built image is
  # already in containerd, but the running pods keep the old one until the
  # deployment is restarted explicitly. The CI path instead pins :<git sha>
  # with `kubectl set image`, which rolls by itself.
  [[ "$BUILD" == 1 ]] && kubectl -n "$NAMESPACE" rollout restart "deploy/${deploy}" >/dev/null
  if ! kubectl -n "$NAMESPACE" rollout status "deploy/${deploy}" --timeout=300s >/dev/null; then
    kubectl -n "$NAMESPACE" get pods -l "app=${deploy}" -o wide >&2 || true
    kubectl -n "$NAMESPACE" logs -l "app=${deploy}" --tail=40 >&2 || true
    die "rollout di ${deploy} fallito"
  fi
  info "${deploy}: $(kubectl -n "$NAMESPACE" get pods -l "app=${deploy}" --no-headers | awk '{print $1" ("$3")"}' | paste -sd' ')"
done

# ------------------------------------------------------------- caddy --------

caddy_block() { # $1 name, $2 domain, $3 nodeport
  printf '%s {\n\tencode zstd gzip\n\treverse_proxy 127.0.0.1:%s {\n\t\theader_up X-Real-IP {remote_host}\n\t}\n\tlog {\n\t\toutput file /var/log/caddy/%s.access.log {\n\t\t\troll_size 10mb\n\t\t\troll_keep 3\n\t\t}\n\t}\n}\n' \
    "$2" "$3" "$1"
}

ok "configuro Caddy"
mkdir -p "$SITES_DIR"
in_components frontend && caddy_block squidcode-frontend "$DOMAIN_FRONTEND" "$NODEPORT_FRONTEND" > "${SITES_DIR}/squidcode-frontend.caddy"
in_components backend  && caddy_block squidcode-backend  "$DOMAIN_BACKEND"  "$NODEPORT_BACKEND"  > "${SITES_DIR}/squidcode-backend.caddy"
in_components leetcode && caddy_block squidcode-leetcode "$DOMAIN_LEETCODE" "$NODEPORT_LEETCODE" > "${SITES_DIR}/squidcode-leetcode.caddy"
chmod 644 "${SITES_DIR}"/squidcode-*.caddy 2>/dev/null || true

if ! caddy validate --config /etc/caddy/Caddyfile --adapter caddyfile >/dev/null 2>&1; then
  caddy validate --config /etc/caddy/Caddyfile --adapter caddyfile 2>&1 | tail -5 >&2
  die "Caddyfile non valido"
fi
systemctl reload caddy
info "domini: https://${DOMAIN_FRONTEND}  https://${DOMAIN_BACKEND}  https://${DOMAIN_LEETCODE}"

# ------------------------------------------------------------ verify --------

check() { # $1 label, $2 curl args..., expected codes in $EXPECT
  local label="$1"; shift
  local code
  for _ in $(seq 1 20); do
    code="$(curl -s -o /dev/null -w '%{http_code}' --max-time 15 "$@" 2>/dev/null || true)"
    [[ "$code" =~ ^($EXPECT)$ ]] && { printf '\033[32m✔\033[0m %-28s HTTP %s\n' "$label" "$code"; return 0; }
    sleep 3
  done
  printf '\033[31m✘\033[0m %-28s HTTP %s\n' "$label" "${code:-nessuna risposta}"
  return 1
}

ok "verifica dall'esterno"
FAILED=0
if in_components frontend; then
  EXPECT='200'; check "frontend" "https://${DOMAIN_FRONTEND}/" || FAILED=1
fi
if in_components leetcode; then
  EXPECT='200'; check "leetcode" "https://${DOMAIN_LEETCODE}/leetcode/problems/two-sum" || FAILED=1
fi
if in_components backend; then
  EXPECT='201'; check "backend" -X POST "https://${DOMAIN_BACKEND}/submissions/" \
    -H 'Content-Type: application/json' \
    -d '{"problemId":"two-sum","code":"print(1)","language":"python"}' || FAILED=1
fi

kubectl -n "$NAMESPACE" get deploy,svc,pod -o wide
[[ "$FAILED" == 0 ]] || die "verifica fallita: guarda i log con kubectl -n ${NAMESPACE} logs -l app=<servizio>"
ok "deploy completato"
