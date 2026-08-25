# Kubernetes Migration

This directory is the production Kubernetes deployment layer for the marketplace platform.

## Architecture Diagram

```text
Internet
  -> Ingress Controller (NGINX)
    -> /      -> frontend
    -> /api   -> api-gateway

api-gateway
  -> user-service
  -> product-service
  -> order-service
  -> payment-service
  -> shipping-service
  -> favourite-service

order-service
  -> managed MySQL
  -> managed RabbitMQ
  -> product-service

payment-service
  -> managed MySQL
  -> managed RabbitMQ

user-service / product-service / shipping-service / favourite-service
  -> managed MySQL

Prometheus Operator
  -> ServiceMonitor / PodMonitor targets

cert-manager
  -> ClusterIssuer
  -> TLS certificates for Ingress
```

## Folder Structure

```text
deploy/k8s/
  base/
    frontend/
    api-gateway/
    user-service/
    product-service/
    order-service/
    payment-service/
    shipping-service/
    favourite-service/
    kustomization.yaml

  infra/
    ingress/
    cert-manager/
    network-policies/
    monitoring/
    keda/
    external-secrets/

  overlays/
    dev/
    prod/
    dev-addons/
    prod-addons/
```

## What Was Removed from the Production Model

- Docker Compose is no longer the production deployment contract.
- Caddy is no longer required in production.
- `.env` is no longer the production runtime contract.
- public `/actuator/*` routing is removed from the production edge.
- file logging is removed in favor of console logging.
- Flyway is no longer intended to run automatically in every pod.
- MySQL, RabbitMQ, Prometheus, Grafana, Alertmanager, and Caddy are not part of the initial app deployment layer.

## Runtime Model

- non-sensitive configuration comes from `ConfigMap`
- sensitive configuration comes from `Secret`
- `ExternalSecret` can replace static Kubernetes `Secret` objects when the operator is installed
- frontend uses same-origin `/api` by default
- internal service communication uses Kubernetes DNS service names
- health checks use Kubernetes HTTP probes
- metrics are scraped through `ServiceMonitor` / `PodMonitor`
- critical services use `PodDisruptionBudget`
- workloads use resource requests and limits
- topology spread constraints reduce single-node concentration

## Ingress Contract

- `/` routes to `frontend`
- `/api` routes to `api-gateway`
- TLS is issued by `cert-manager`
- `Ingress` replaces the previous production role of Caddy

## Migration Plan

### Phase 1: Base Services

1. Build and push container images for all eight app services.
   Services:
   - `frontend`
   - `api-gateway`
   - `user-service`
   - `product-service`
   - `order-service`
   - `payment-service`
   - `shipping-service`
   - `favourite-service`
   Use:
   `pwsh ./scripts/publish-dockerhub-images.ps1 -DockerHubNamespace <your-dockerhub-namespace> -DevTag v1.0.0 -ProdTag v1.0.0`
2. Provision managed MySQL and managed RabbitMQ endpoints.
3. Install the ingress controller in the cluster.
4. Apply `deploy/k8s/overlays/dev` first.
5. Run migration `Job`s per service before scaling application deployments.
6. Validate:
   - ingress routing
   - pod readiness
   - service-to-service communication
   - database connectivity
   - message broker connectivity

### Phase 2: Scaling and Observability

1. Enable `HorizontalPodAutoscaler` for:
   - frontend
   - api-gateway
   - user-service
   - product-service
   - order-service
   - payment-service
2. Deploy Prometheus Operator or kube-prometheus-stack separately.
3. Install `cert-manager` and `KEDA` operators if needed.
4. Install External Secrets Operator if secrets should come from a vault or cloud secret manager.
5. Apply `deploy/k8s/overlays/dev-addons` or `deploy/k8s/overlays/prod-addons`.
6. Enable `KEDA` for RabbitMQ-driven `payment-service` scaling.
7. Apply `ServiceMonitor` and `PodMonitor`.
8. Connect stdout logs to the cluster logging stack.

### Phase 3: Production Hardening

1. Apply stricter `NetworkPolicy` rules per namespace and external dependency.
2. Replace placeholder `Secret`s with an external secret manager.
3. Tune HPA thresholds and resource limits from real production metrics.
4. Add rollout controls, SLO alerting, and backup/restore procedures.
5. Promote `deploy/k8s/overlays/prod` after dev validation.

## Risk Analysis

### High Risk

- keeping Flyway enabled in pods during rollout can create race conditions
- treating managed MySQL and RabbitMQ as if they were local Compose services
- leaving secrets as plain Kubernetes `Secret` objects instead of externalized secret management
- applying add-on overlays before their CRDs/operators are installed
- overloading `api-gateway` with edge responsibilities that belong in ingress

### Medium Risk

- inaccurate resource limits before production traffic is observed
- insufficient network policy refinement for external service egress
- message-driven scaling without queue-depth calibration

### Low Risk

- Kubernetes DNS migration itself, because the app is already service-oriented
- replacing Caddy with ingress, because routing is straightforward: `/` and `/api`

## What Not to Migrate

Do not copy these Compose patterns into production Kubernetes:

- `depends_on`
- bridge networking
- host port publishing
- Compose restart policies
- `mem_limit` / `cpus`
- public `/actuator/*`
- bundled stateful infra in the app release

Do not deploy these inside the first application release:

- MySQL
- RabbitMQ
- Prometheus
- Grafana
- Alertmanager
- Caddy

## Deployment Commands

```powershell
kubectl apply -k deploy/k8s/overlays/dev
kubectl apply -k deploy/k8s/overlays/prod
```

Or use the helper script after publishing:

```powershell
pwsh ./scripts/deploy-k8s-from-dockerhub.ps1 -Environment dev
pwsh ./scripts/deploy-k8s-from-dockerhub.ps1 -Environment prod
```

Optional operator-dependent add-ons:

```powershell
kubectl apply -k deploy/k8s/overlays/dev-addons
kubectl apply -k deploy/k8s/overlays/prod-addons
```

For a small local cluster, `overlays/dev` uses:

- single replica per Deployment
- reduced CPU and memory requests
- Docker Hub image names once published with `scripts/publish-dockerhub-images.ps1`
- no HPA requirement

Optional External Secrets examples:

```powershell
kubectl apply -k deploy/k8s/infra/external-secrets
```
