# Kubernetes Deployment Runbook

## 1) Publish Images

```powershell
pwsh ./scripts/publish-dockerhub-images.ps1 -DockerHubNamespace <your-dockerhub-namespace> -DevTag v1.0.0 -ProdTag v1.0.0
```

## 2) Apply The Cluster Overlay

```powershell
pwsh ./scripts/deploy-k8s-from-dockerhub.ps1 -Environment dev
```

Use `-Environment prod` only after replacing the production placeholders for databases, messaging, ingress hostnames, and secrets.

## 3) Required Runtime Configuration

- `JWT_SECRET`
- `SECURE_COOKIES`
- `USER_SERVICE_INTERNAL_AUTH_SECRET`
- `PRODUCT_SERVICE_INTERNAL_AUTH_SECRET`
- `ORDER_SERVICE_INTERNAL_AUTH_SECRET`
- `PAYMENT_SERVICE_INTERNAL_AUTH_SECRET`
- `SHIPPING_SERVICE_INTERNAL_AUTH_SECRET`
- `FAVOURITE_SERVICE_INTERNAL_AUTH_SECRET`
- `SPRING_DATASOURCE_URL` / user / password per service
- RabbitMQ host / user / password for `order-service` and `payment-service`
- ingress host and TLS secret / issuer values

## 4) Verify Runtime

```powershell
kubectl get deploy -n ecommerce-dev
kubectl get pods -n ecommerce-dev
kubectl port-forward -n ecommerce-dev svc/frontend 3000:3000
```

Open:

- `http://127.0.0.1:3000`

If you also want to test the gateway directly:

```powershell
kubectl port-forward -n ecommerce-dev svc/api-gateway 8080:8080
```

Then test:

- `http://127.0.0.1:8080/api/products`

## Local Development Data

The repository intentionally ships without default user accounts, credentials, addresses, or verification tokens. Create accounts through the registration flow after deployment.

If you previously ran an older version of the user-service migrations in a local development cluster, recreate the development MySQL data before deploying this clean migration set. Do not modify or delete production data without an approved migration plan and backup.

## 5) Quick Diagnostics

```powershell
kubectl get ingress -A
kubectl get svc -n ecommerce-dev
kubectl logs -n ecommerce-dev deployment/api-gateway --tail=200
kubectl logs -n ecommerce-dev deployment/user-service --tail=200
kubectl logs -n ecommerce-dev deployment/product-service --tail=200
kubectl logs -n ecommerce-dev deployment/order-service --tail=200
kubectl logs -n ecommerce-dev deployment/payment-service --tail=200
kubectl logs -n ecommerce-dev deployment/shipping-service --tail=200
kubectl logs -n ecommerce-dev deployment/favourite-service --tail=200
```
