"use client";

import Link from "next/link";

import { Button } from "@/components/atoms/button";
import { Text } from "@/components/atoms/text";
import { EmptyState } from "@/components/molecules/empty-state";
import { SkeletonBlock } from "@/components/molecules/skeleton-block";
import { useToast } from "@/components/ui/toast";
import { useCart } from "@/features/cart/cart-provider";
import { ProductArtwork } from "@/features/products/components/product-artwork";
import { useProductQuery, useProductsQuery } from "@/features/products/hooks";
import { formatCurrency } from "@/lib/formatters";

type ProductDetailHeroProps = {
  productId: string;
};

export function ProductDetailHero({ productId }: ProductDetailHeroProps) {
  const { data: product, isLoading, isError, refetch } = useProductQuery(productId);
  const { data: products } = useProductsQuery();
  const { addItem } = useCart();
  const { notify } = useToast();

  if (isLoading) {
    return (
      <div className="grid gap-6 lg:grid-cols-[1fr_0.9fr]">
        <SkeletonBlock className="h-[34rem]" />
        <SkeletonBlock className="h-[34rem]" />
      </div>
    );
  }

  if (isError || !product) {
    return (
      <EmptyState
        title="This product couldn't be resolved from the catalog."
        description="The requested product id did not return a valid payload."
        actionLabel="Retry request"
        onAction={() => void refetch()}
      />
    );
  }

  const related = (products ?? [])
    .filter(
      (candidate) =>
        candidate.productId !== product.productId &&
        candidate.category?.categoryTitle === product.category?.categoryTitle
    )
    .slice(0, 3);

  return (
    <div className="space-y-8">
      <section className="grid gap-6 lg:grid-cols-[1fr_0.9fr]">
        <div className="surface-panel-strong p-6 sm:p-8">
          <ProductArtwork
            imageUrl={product.imageUrl}
            productTitle={product.productTitle}
            categoryTitle={product.category?.categoryTitle}
            frameClassName="overflow-hidden rounded-[18px] bg-[#F8FAFC]"
            imageClassName="h-[34rem] w-full object-cover"
            fallbackClassName="grid h-[34rem] place-items-center rounded-[18px] bg-[#F8FAFC] px-8 text-center"
          />
        </div>

        <div className="surface-panel flex flex-col gap-8 p-6 sm:p-8">
          <div className="space-y-4">
            <span className="eyebrow">Product details</span>
            <h1 className="headline">{product.productTitle}</h1>
            <Text>
              Check the price, stock, and category before adding the item to your cart.
            </Text>
          </div>

          <div className="grid gap-4 sm:grid-cols-2">
            <div className="rounded-[var(--radius-xl)] border border-line bg-surface p-4">
              <p className="text-xs uppercase tracking-[0.22em] text-muted">Price</p>
              <p className="mt-2 text-2xl font-semibold text-text">
                {formatCurrency(product.priceUnit)}
              </p>
            </div>
            <div className="rounded-[var(--radius-xl)] border border-line bg-surface p-4">
              <p className="text-xs uppercase tracking-[0.22em] text-muted">Stock</p>
              <p className="mt-2 text-2xl font-semibold text-text">{product.quantity}</p>
            </div>
            <div className="rounded-[var(--radius-xl)] border border-line bg-surface p-4">
              <p className="text-xs uppercase tracking-[0.22em] text-muted">SKU</p>
              <p className="mt-2 text-lg font-medium text-text">{product.sku ?? "Unavailable"}</p>
            </div>
            <div className="rounded-[var(--radius-xl)] border border-line bg-surface p-4">
              <p className="text-xs uppercase tracking-[0.22em] text-muted">Category</p>
              <p className="mt-2 text-lg font-medium text-text">
                {product.category?.categoryTitle ?? "General"}
              </p>
            </div>
          </div>

          <div className="flex flex-wrap gap-4">
            <Button
              onClick={() => {
                void addItem(product).then((added) => {
                  if (added) {
                    notify({
                      title: "Added to cart",
                      description: `${product.productTitle} was added to your cart.`
                    });
                  }
                });
              }}
            >
              Add to cart
            </Button>
            <Link href="/checkout">
              <Button variant="outline">Go to checkout</Button>
            </Link>
          </div>
        </div>
      </section>

      {related.length > 0 ? (
        <section className="surface-panel p-6 sm:p-8">
          <div className="mb-6 flex items-end justify-between gap-4">
            <div>
              <p className="eyebrow">Related selection</p>
              <h2 className="text-2xl font-semibold text-text">
                Similar products
              </h2>
            </div>
            <Link href="/products">
              <Button variant="ghost">Back to products</Button>
            </Link>
          </div>
          <div className="grid gap-4 md:grid-cols-3">
            {related.map((item) => (
              <Link
                key={item.productId}
                href={`/products/${item.productId}`}
                className="rounded-[var(--radius-xl)] border border-line bg-surface p-6 transition hover:border-accent/40"
              >
                <p className="text-xs uppercase tracking-[0.22em] text-muted">
                  {item.category?.categoryTitle ?? "Product"}
                </p>
                <h3 className="mt-4 text-xl font-semibold text-text">{item.productTitle}</h3>
                <Text size="sm" className="mt-6">
                  {formatCurrency(item.priceUnit)}
                </Text>
              </Link>
            ))}
          </div>
        </section>
      ) : null}
    </div>
  );
}
