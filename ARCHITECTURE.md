# Architecture and Boundaries

## Runtime Topology

```text
Browser
  |
  v
HTTPS Edge (Caddy, TLS termination)
  |
  +--> Frontend (Next.js)
  |
  +--> API Gateway (routing + authentication)
            |
            +--> user-service
            +--> product-service
            +--> order-service
            +--> payment-service
            +--> shipping-service
            +--> favourite-service
            |
            v
          MySQL (isolated schemas + users)

Prometheus <----- actuator/prometheus
Grafana -----> Prometheus
```

## Security Model

- Public traffic enters through the HTTPS edge only.
- The gateway issues a session cookie that is `httpOnly`, `Secure`, and `SameSite=Strict`.
- The gateway injects a signed internal service token on every proxied `/api/**` request.
- Internal services reject `/api/**` traffic that does not carry a valid internal token.
- The gateway fails startup if required security variables are missing or if secure cookies are disabled.

## Service Ownership

| Service | Primary responsibility | Main internal base path |
|---|---|---|
| `user-service` | Users, credentials, addresses, verification tokens, controlled registration | `/user-service` |
| `product-service` | Products and categories | `/product-service` |
| `order-service` | Carts and orders | `/order-service` |
| `payment-service` | Order payments | `/payment-service` |
| `shipping-service` | Order items | `/shipping-service` |
| `favourite-service` | User favourites | `/favourite-service` |
| `api-gateway` | Edge routing, authentication, CORS, throttling | `/` |

## Public Contract (via API Gateway)

The supported browser contract is:

- `GET /api/products`
- `GET /api/products/{id}`
- `GET /api/categories`
- `POST /api/authenticate`
- `POST /api/users/register`
- authenticated `GET /api/orders`
- authenticated `POST /api/orders`
- authenticated `GET /api/payments`
- authenticated `POST /api/payments`
- authenticated `GET /api/users`

## Resilience Posture

- Read paths no longer expand related objects by calling other services synchronously.
- Internal HTTP clients use signed internal tokens and short timeouts.
- Compose boot uses readiness dependencies instead of naive start order only.
- Observability is intentionally limited to Prometheus + Grafana; unsupported partial stacks are removed.

## Target Event-Driven Expansion

The current stack is hardened for secure direct traffic. The next scale step is asynchronous domain events for write-side workflows:

```text
order-service -- order.created --> event bus --> payment-service
                                           \--> shipping-service
```

Recommended implementation path:

1. Add RabbitMQ or Kafka as the event backbone.
2. Publish `order.created`, `payment.completed`, and `catalog.item.updated`.
3. Let payment and shipping react asynchronously instead of joining across services at request time.
4. Keep the gateway transport-focused; do not move orchestration into the edge.

## Supported Deployment

- Kubernetes under `deploy/k8s/` is the only supported runtime stack.
- The stack is HTTPS-first, MySQL-backed, and intended for self-hosted deployment.
- There are no separate dev/prod profile files anymore.
