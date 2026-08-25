# Docker Hub publishing

Use this script to build the eight application images, push them to Docker Hub, and update the Kubernetes overlays to reference Docker Hub instead of local-only `ecommerce-dev/*` images.

## Requirements

- Docker Desktop running
- `docker login` already completed
- PowerShell

## Command

```powershell
pwsh ./scripts/publish-dockerhub-images.ps1 -DockerHubNamespace <your-dockerhub-namespace>
```

Example:

```powershell
pwsh ./scripts/publish-dockerhub-images.ps1 -DockerHubNamespace <your-dockerhub-namespace> -DevTag v1.0.0 -ProdTag v1.0.0
```

## What it does

1. Builds:
   - `frontend`
   - `api-gateway`
   - `user-service`
   - `product-service`
   - `order-service`
   - `payment-service`
   - `shipping-service`
   - `favourite-service`
2. Pushes them to:
   - `docker.io/<your-dockerhub-namespace>/<service>:<tag>`
3. Updates:
   - `deploy/k8s/overlays/dev/kustomization.yaml`
   - `deploy/k8s/overlays/prod/kustomization.yaml`

The default version tag is:

```text
v1.0.0
```

## Deploy to Kubernetes after push

For `dev`:

```powershell
pwsh ./scripts/deploy-k8s-from-dockerhub.ps1 -Environment dev
```

For `prod`:

```powershell
pwsh ./scripts/deploy-k8s-from-dockerhub.ps1 -Environment prod
```

If your cluster already has the needed operators and CRDs, include add-ons too:

```powershell
pwsh ./scripts/deploy-k8s-from-dockerhub.ps1 -Environment dev -IncludeAddons
```

## Dry run

To build and update the overlays without pushing:

```powershell
pwsh ./scripts/publish-dockerhub-images.ps1 -DockerHubNamespace <your-dockerhub-namespace> -SkipPush
```
