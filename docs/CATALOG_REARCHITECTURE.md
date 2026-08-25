# Catalog Re-architecture

## Design Decisions

The product service now treats a product as a marketplace listing rather than a generic CRUD record.

- Prices are stored as `BigDecimal` with two decimal places.
- Categories are resolved by identifier instead of being embedded as writable object graphs.
- Category visibility prevents new listings in archived catalog areas.
- Products are withdrawn by status instead of being hard-deleted, preserving order history.
- Write APIs use dedicated request records; response records are read-only representations.

## Database Evolution

`V9__evolve_catalog_for_listing_workflow.sql` is deliberately forward-only. Existing Kubernetes environments may already have Flyway checksums for migrations `V1` through `V8`; rewriting those files would block deployment. New installations run all migrations, and existing installations receive the non-destructive schema evolution at `V9`.

## API Compatibility

Public browse endpoints remain available at `/api/products` and `/api/categories`. Product and category writes now use the following request fields:

```json
{
  "productTitle": "Marketplace desk lamp",
  "sku": "SELLERLIST-001",
  "priceUnit": 49.99,
  "quantity": 8,
  "description": "Warm light with an adjustable arm.",
  "category": { "categoryId": 4 }
}
```
