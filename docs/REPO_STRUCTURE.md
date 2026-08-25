# Repository Structure

This monorepo is intentionally organized around three layers:

## Runtime services

- `api-gateway/`
- `user-service/`
- `product-service/`
- `order-service/`
- `payment-service/`
- `shipping-service/`
- `favourite-service/`
- `frontend/`

These directories contain deployable application code only.

## Shared platform modules

- `platform-security/` for internal request signing and validation
- `platform-events/` for shared event contracts

These modules exist to avoid copy-pasting shared backend infrastructure across services.

## Operations and docs

- `deploy/k8s/` is the single supported runtime definition
- `docker/` contains shared container build helpers
- `docs/` contains design assets and architecture notes

## Cleanliness rules

- generated artifacts must stay out of the workspace when not actively needed:
  - `frontend/node_modules`
  - `frontend/.next`
  - `**/target`
- stale CI/CD definitions should not coexist with the active pipeline
- service folders should contain service code only, not duplicated repo-level tooling

## Practical navigation

- If you are changing auth or routing, start in `api-gateway/` and `platform-security/`
- If you are changing checkout, start in `order-service/`, `payment-service/`, and `frontend/features/checkout/`
- If you are changing runtime behavior, start with `deploy/k8s/`
