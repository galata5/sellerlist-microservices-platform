# Sellerlist Microservices Platform

Sellerlist is a full-stack e-commerce marketplace built as a Spring Boot microservices monorepo with a Next.js frontend. It demonstrates service boundaries, an API gateway, containerized delivery, and Kubernetes deployment with Kustomize overlays.

## Features

- Public product catalog and category browsing
- Account registration and session-based sign-in
- Cart, checkout, order, payment, shipping, and favourites domains
- Gateway-based routing, CORS handling, and authentication
- Isolated service schemas and Flyway migrations
- Kubernetes health probes, resource limits, network policies, and deployment overlays

## Architecture

```text
Browser
  -> Next.js frontend
  -> API Gateway
  -> User | Product | Order | Payment | Shipping | Favourite services
  -> MySQL schemas and RabbitMQ (order/payment workflows)
```

The browser uses same-origin `/api` requests. The gateway is the public backend entry point; domain services are internal Kubernetes services. Editable architecture and ERD diagrams are in [`docs/diagrams`](docs/diagrams).

## Tech Stack

- Java 17 and Spring Boot 3
- Spring Cloud Gateway, Spring Security, JWT, and Resilience4j
- MySQL, Flyway, RabbitMQ, Testcontainers, Prometheus, and Grafana manifests
- Next.js 14, React, TypeScript, Tailwind CSS, and TanStack Query
- Docker, Kubernetes, Kustomize, and GitHub Actions

## Repository Structure

```text
api-gateway/            Public API routing and authentication
user-service/           Users, credentials, addresses, registration
product-service/        Products and categories
order-service/          Carts and orders
payment-service/        Payment workflow
shipping-service/       Order-item shipping data
favourite-service/      User favourites
platform-security/      Shared internal-service authentication
platform-events/        Shared event contracts
frontend/               Next.js application
deploy/k8s/              Kubernetes base, infrastructure, and overlays
docker/                  Shared container build helpers
scripts/                 Publish and deployment helpers
docs/                    Diagrams, screenshots, and project notes
```

## Getting Started

Prerequisites: Java 17, Docker Desktop, Node.js 20, and a Kubernetes cluster for the deployment workflow.

```powershell
./mvnw test

Set-Location frontend
npm ci
npm run lint
npm run build
```

## Environment Configuration

Copy `.env.example` to `.env` only for local development and replace every placeholder with your own value. Never commit `.env`.

The committed development Kubernetes overlay contains placeholders instead of credentials. Before applying it, provide your own values for MySQL, RabbitMQ, JWT, and internal service secrets. See [`DEPLOYMENT.md`](DEPLOYMENT.md) and [`deploy/k8s/README.md`](deploy/k8s/README.md).

## Docker

Build and publish images with your own Docker Hub namespace:

```powershell
pwsh ./scripts/publish-dockerhub-images.ps1 -DockerHubNamespace <your-dockerhub-namespace> -DevTag v1.0.0 -ProdTag v1.0.0
```

## Kubernetes

Deploy the development overlay after configuring its placeholders:

```powershell
pwsh ./scripts/deploy-k8s-from-dockerhub.ps1 -Environment dev
kubectl get deploy -n ecommerce-dev
```

## Testing

- Backend: `./mvnw test`
- Frontend: `npm ci`, `npm run lint`, and `npm run build` from `frontend/`
- CI: [`.github/workflows/platform-ci.yml`](.github/workflows/platform-ci.yml) runs the same backend and frontend checks.

## CI/CD

GitHub Actions runs Maven tests plus frontend installation, linting, and production build checks on pushes and pull requests. Image publishing and cluster deployment are intentionally manual helper-script workflows until repository-specific credentials and environments are configured.

## Security

- JWT-backed authentication with HTTP-only session cookies
- Gateway-side public routing and authentication controls
- Internal service request signing
- Kubernetes `Secret` and `ConfigMap` separation
- Network policies and non-root workload security contexts in deployment manifests

## Screenshots

Add four to six current UI screenshots under [`docs/screenshots`](docs/screenshots). Suggested coverage: home, product catalog, product detail, cart/checkout, sign-in, and dashboard.

## Technical Highlights

- A monorepo with independently deployable Spring services
- API gateway as the public backend boundary
- Kubernetes base manifests with dev and production Kustomize overlays
- Local development infrastructure manifests for MySQL and RabbitMQ
- Shared security and event-contract modules to avoid duplicated service infrastructure

## Known Limitations

- Payment processing is application-level workflow logic, not a production payment-provider integration.
- The dev overlay uses local infrastructure and must be configured with developer-owned secrets before use.
- Event contracts and RabbitMQ infrastructure exist, but the architecture is not fully event-driven end to end.
- Production operations such as managed-secret integration, backups, and cluster logging require environment-specific setup.

## Future Improvements

- Complete asynchronous domain-event workflows for order, payment, and shipping.
- Add API reference documentation where it improves developer onboarding.
- Add production deployment automation and managed-secret integration.
- Expand automated integration and end-to-end coverage.

## Collaboration and Provenance

This project began from a codebase by `selimhorri` and has since been extended and re-architected into the Sellerlist platform. The current repository includes subsequent frontend, platform-security, platform-events, Docker, Kubernetes, deployment, and documentation work. Preserve applicable upstream attribution and license terms when reusing or distributing inherited components.
