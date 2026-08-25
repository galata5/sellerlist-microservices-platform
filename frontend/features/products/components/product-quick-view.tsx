"use client";

import { Button } from "@/components/atoms/button";
import { Text } from "@/components/atoms/text";
import { Modal } from "@/components/ui/modal";
import { useCart } from "@/features/cart/cart-provider";
import { ProductArtwork } from "@/features/products/components/product-artwork";
import type { Product } from "@/lib/api/types";
import { formatCurrency } from "@/lib/formatters";

type ProductQuickViewProps = {
  product: Product | null;
  open: boolean;
  onClose: () => void;
};

export function ProductQuickView({
  product,
  open,
  onClose
}: ProductQuickViewProps) {
  const { addItem } = useCart();

  if (!product) {
    return null;
  }

  return (
    <Modal
      open={open}
      onClose={onClose}
      title={product.productTitle}
      description={product.category?.categoryTitle ?? "Catalog product"}
    >
      <div className="grid gap-6 lg:grid-cols-[0.9fr_1.1fr]">
        <div className="rounded-[var(--radius-2xl)] border border-line bg-surface p-6">
          <ProductArtwork
            imageUrl={product.imageUrl}
            productTitle={product.productTitle}
            categoryTitle={product.category?.categoryTitle}
            frameClassName="overflow-hidden rounded-[var(--radius-xl)] border border-line bg-black/10"
            imageClassName="h-64 w-full object-cover"
            fallbackClassName="grid h-64 place-items-center rounded-[var(--radius-xl)] border border-line bg-black/20 text-center"
          />
        </div>
        <div className="space-y-6">
          <div className="flex flex-wrap gap-4">
            <span className="rounded-full border border-line px-4 py-2 text-xs uppercase tracking-[0.2em] text-muted">
              SKU {product.sku ?? "N/A"}
            </span>
            <span className="rounded-full border border-line px-4 py-2 text-xs uppercase tracking-[0.2em] text-muted">
              Qty {product.quantity}
            </span>
          </div>
          <Text>
            A restrained commerce object presented with clear spacing, soft motion, and a direct path into the cart.
          </Text>
          <div className="surface-panel flex items-center justify-between p-4">
            <div>
              <p className="text-xs uppercase tracking-[0.2em] text-muted">Unit price</p>
              <p className="text-2xl font-semibold text-text">
                {formatCurrency(product.priceUnit)}
              </p>
            </div>
            <Button
              onClick={() => {
                void addItem(product).then((added) => {
                  if (added) {
                    onClose();
                  }
                });
              }}
            >
              Add to cart
            </Button>
          </div>
        </div>
      </div>
    </Modal>
  );
}
