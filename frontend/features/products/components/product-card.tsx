"use client";

import { motion, type MotionProps } from "framer-motion";
import Link from "next/link";

import { Button } from "@/components/atoms/button";
import { ProductArtwork } from "@/features/products/components/product-artwork";
import { cn } from "@/lib/cn";
import type { Product } from "@/lib/api/types";
import { formatCurrency } from "@/lib/formatters";

type ProductCardProps = {
  product: Product;
  onQuickView: (product: Product) => void;
  onAddToCart: (product: Product) => void;
  layout?: "grid" | "list";
};

export function ProductCard({
  product,
  onQuickView,
  onAddToCart,
  layout = "grid"
}: ProductCardProps) {
  return (
    <motion.article
      className={cn(
        "overflow-hidden rounded-[24px] border border-line bg-surface shadow-[0_16px_36px_rgba(15,23,42,0.08)]",
        layout === "list" && "min-h-[330px] md:grid md:grid-cols-[0.92fr_1.08fr]"
      )}
      whileHover={{ y: -4 }}
      transition={{ duration: 0.2, ease: "easeOut" }}
    >
      <div className="relative overflow-hidden border-b border-line/70 p-0 md:border-b-0 md:border-r">
        <span className="absolute right-4 top-4 z-[1] rounded-full bg-[#2563EB] px-3 py-1 text-xs font-semibold text-white shadow-[0_8px_20px_rgba(37,99,235,0.3)]">
          {product.category?.categoryTitle ?? "Product"}
        </span>

        <ProductArtwork
          imageUrl={product.imageUrl}
          productTitle={product.productTitle}
          categoryTitle={product.category?.categoryTitle}
          frameClassName="overflow-hidden bg-[#F8FAFC]"
          imageClassName="h-60 w-full object-cover md:h-full"
          fallbackClassName="grid h-60 place-items-center bg-[#F8FAFC] px-6 text-center md:h-full"
        />
      </div>

      <div className="flex flex-1 flex-col gap-6 p-6">
        <div className="space-y-4">
          <div className="flex items-center gap-3">
            <div className="grid h-11 w-11 place-items-center rounded-full bg-[linear-gradient(135deg,#F97316_0%,#2563EB_100%)] text-sm font-bold text-white">
              {product.productTitle.charAt(0).toUpperCase()}
            </div>
            <div>
              <p className="text-sm font-medium text-text">e-shope seller</p>
              <p className="text-sm text-muted">{product.quantity} in stock</p>
            </div>
          </div>

          <div className="space-y-2">
            <h3 className="text-[1.55rem] font-semibold leading-[1.16] tracking-[-0.03em] text-text">
              {product.productTitle}
            </h3>
            <p className="line-clamp-2 text-sm text-muted">
              {product.sku
                ? `SKU ${product.sku} • Secure marketplace listing with simple checkout and order tracking.`
                : "Secure marketplace listing with simple checkout and order tracking."}
            </p>
          </div>

          <div className="flex items-center gap-3">
            <span className="text-2xl font-semibold text-[#F59E0B]">★</span>
            <span className="text-lg font-semibold text-text">4.9</span>
            <span className="text-sm text-muted">(market verified)</span>
          </div>
        </div>

        <div className="mt-auto flex items-end justify-between gap-4 border-t border-line/70 pt-5">
          <div>
            <p className="text-xs uppercase tracking-[0.18em] text-muted">Starting at</p>
            <p className="mt-1 text-2xl font-semibold text-text">
              {formatCurrency(product.priceUnit)}
            </p>
          </div>

          <div className="flex flex-wrap gap-3">
            <Link href={`/products/${product.productId}`}>
              <Button variant="outline">View details</Button>
            </Link>
            <Button variant="ghost" onClick={() => onQuickView(product)}>
              Quick view
            </Button>
            <Button onClick={() => onAddToCart(product)}>Add to cart</Button>
          </div>
        </div>
      </div>
    </motion.article>
  );
}
