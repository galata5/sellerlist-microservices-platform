"use client";

import dynamic from "next/dynamic";
import { useRouter, useSearchParams } from "next/navigation";
import { useEffect, useMemo, useState } from "react";

import { Button } from "@/components/atoms/button";
import { Text } from "@/components/atoms/text";
import { EmptyState } from "@/components/molecules/empty-state";
import { SkeletonBlock } from "@/components/molecules/skeleton-block";
import { useToast } from "@/components/ui/toast";
import { useCart } from "@/features/cart/cart-provider";
import { getCategories, getProductCatalog } from "@/features/products/api";
import { ProductCard } from "@/features/products/components/product-card";
import type { Product } from "@/lib/api/types";
import { useQuery } from "@tanstack/react-query";
import { formatCurrency } from "@/lib/formatters";
import { cn } from "@/lib/cn";

const ProductQuickView = dynamic(
  () =>
    import("@/features/products/components/product-quick-view").then((module) => ({
      default: module.ProductQuickView
    })),
  { ssr: false }
);

const shopFocusFilters = [
  { label: "Clothes", terms: ["cloth", "clothes", "fashion", "wear", "shirt", "jacket"] },
  { label: "Laptop", terms: ["laptop", "computer", "pc", "notebook", "electronics"] },
  { label: "Phone", terms: ["phone", "mobile", "smartphone"] },
  { label: "Beauty", terms: ["beauty", "skin", "cosmetic", "care"] }
] as const;

export function ProductGrid() {
  const router = useRouter();
  const searchParams = useSearchParams();
  const { data, isLoading, refetch } = useQuery({
    queryKey: ["product-catalog"],
    queryFn: getProductCatalog,
    refetchInterval: 20000,
    refetchIntervalInBackground: true
  });
  const categoriesQuery = useQuery({
    queryKey: ["product-categories"],
    queryFn: getCategories,
    refetchInterval: 20000,
    refetchIntervalInBackground: true
  });
  const { addItem } = useCart();
  const { notify } = useToast();
  const [query, setQuery] = useState("");
  const [selectedProduct, setSelectedProduct] = useState<Product | null>(null);
  const [selectedCategories, setSelectedCategories] = useState<string[]>([]);
  const [selectedShopFocus, setSelectedShopFocus] = useState<string | null>(null);
  const [maxPrice, setMaxPrice] = useState(1000);
  const [sortBy, setSortBy] = useState("recommended");
  const [viewMode, setViewMode] = useState<"grid" | "list">("grid");

  useEffect(() => {
    setQuery(searchParams.get("search") ?? "");
  }, [searchParams]);

  useEffect(() => {
    if (!data?.items.length) {
      return;
    }

    const highestPrice = Math.max(...data.items.map((product) => Math.ceil(product.priceUnit)));
    setMaxPrice(highestPrice);
  }, [data?.items]);

  const products = useMemo(() => {
    if (!data?.items) {
      return [];
    }

    const normalizedQuery = query.trim().toLowerCase();

    const filtered = data.items.filter((product) => {
      const matchesQuery =
        !normalizedQuery ||
        [product.productTitle, product.sku, product.category?.categoryTitle]
          .filter(Boolean)
          .join(" ")
          .toLowerCase()
          .includes(normalizedQuery);
      const shopTargetText = [product.productTitle, product.sku, product.category?.categoryTitle]
        .filter(Boolean)
        .join(" ")
        .toLowerCase();
      const activeShopFocus = shopFocusFilters.find((item) => item.label === selectedShopFocus);
      const matchesShopFocus =
        !activeShopFocus ||
        activeShopFocus.terms.some((term) => shopTargetText.includes(term));

      const matchesCategory =
        selectedCategories.length === 0 ||
        selectedCategories.includes(product.category?.categoryTitle ?? "");
      const matchesPrice = product.priceUnit <= maxPrice;

      return matchesQuery && matchesShopFocus && matchesCategory && matchesPrice;
    });

    switch (sortBy) {
      case "price-low":
        return [...filtered].sort((left, right) => left.priceUnit - right.priceUnit);
      case "price-high":
        return [...filtered].sort((left, right) => right.priceUnit - left.priceUnit);
      default:
        return filtered;
    }
  }, [data, maxPrice, query, selectedCategories, selectedShopFocus, sortBy]);
  const hasCatalogFailure = data ? !data.sourceAvailable : false;
  const categoryItems = categoriesQuery.data?.items ?? [];
  const highestPrice = Math.max(...(data?.items.map((product) => Math.ceil(product.priceUnit)) ?? [1000]));

  if (isLoading) {
    return (
      <div className="grid gap-6 md:grid-cols-2 xl:grid-cols-3">
        {Array.from({ length: 6 }).map((_, index) => (
          <SkeletonBlock key={index} className="h-[30rem]" />
        ))}
      </div>
    );
  }

  return (
    <div className="space-y-8">
      <div className="space-y-3">
        <p className="eyebrow">product</p>
        <h1 className="text-[clamp(2.4rem,5vw,3.4rem)] font-semibold tracking-[-0.05em] text-text">
          Browse Products
        </h1>
        <Text className="max-w-3xl">
          Find the right product from a curated marketplace selection with filters for what you want to shop, price range, and sorting.
        </Text>
      </div>

      {products.length === 0 ? (
        !hasCatalogFailure ? (
          <EmptyState
            title={
              query
                ? "Nothing in the catalog matches that filter."
                : "No products are available yet."
            }
            description={
              query
                ? "Try a broader search term or clear the field to see all products."
                : "Start selling to publish the first item, or come back once products have been added."
            }
            actionLabel={query ? "Clear filter" : "Sell item"}
            onAction={
              query
                ? () => setQuery("")
                : () => {
                    router.push("/sell");
                  }
            }
          />
        ) : (
            <div className="surface-panel p-6">
              <div className="space-y-4">
                <p className="text-lg font-semibold text-text">
                  The catalog is unavailable right now.
                </p>
                <Text size="sm">
                  The product service did not return data yet. Try again in a moment or open the selling flow while it recovers.
                </Text>
              <div className="flex flex-wrap gap-3">
                <Button variant="outline" onClick={() => void refetch()}>
                  Retry request
                </Button>
                <Button onClick={() => router.push("/sell")}>Sell item</Button>
              </div>
            </div>
          </div>
        )
      ) : (
        <div className="grid gap-8 xl:grid-cols-[320px_1fr]">
          <aside className="surface-panel h-fit p-7">
            <div className="flex items-center justify-between">
              <h2 className="text-2xl font-semibold text-text">Filters</h2>
              <button
                type="button"
                className="text-sm font-medium text-accent"
                onClick={() => {
                  setQuery("");
                  setSelectedShopFocus(null);
                  setSelectedCategories([]);
                  setSortBy("recommended");
                  setMaxPrice(highestPrice);
                }}
              >
                Reset
              </button>
            </div>

            <div className="mt-8 space-y-8">
              <div className="space-y-3">
                <p className="text-xl font-medium text-text">What do you want to shop?</p>
                <div className="flex flex-wrap gap-3">
                  {shopFocusFilters.map((item) => (
                    <button
                      key={item.label}
                      type="button"
                      onClick={() =>
                        setSelectedShopFocus((current) =>
                          current === item.label ? null : item.label
                        )
                      }
                      className={cn(
                        "rounded-full border px-4 py-2 text-sm font-medium transition",
                        selectedShopFocus === item.label
                          ? "border-[#2563EB] bg-[#EAF2FF] text-[#2563EB]"
                          : "border-line bg-surface text-text hover:bg-page"
                      )}
                    >
                      {item.label}
                    </button>
                  ))}
                </div>
              </div>

              <div className="space-y-3">
                <p className="text-xl font-medium text-text">Search</p>
                <input
                  className="h-12 w-full rounded-2xl border border-line bg-page px-4 text-sm text-text placeholder:text-muted"
                  placeholder="Search for product "
                  value={query}
                  onChange={(event) => setQuery(event.target.value)}
                />
              </div>

              <div className="space-y-4 border-t border-line pt-6">
                <div className="flex items-center justify-between">
                  <p className="text-xl font-medium text-text">Category</p>
                </div>
                <div className="space-y-3">
                  {categoryItems.map((category) => {
                    const checked = selectedCategories.includes(category.categoryTitle);
                    return (
                      <label key={category.categoryId} className="flex items-center gap-3 text-base text-text">
                        <input
                          type="checkbox"
                          checked={checked}
                          onChange={(event) => {
                            setSelectedCategories((current) =>
                              event.target.checked
                                ? [...current, category.categoryTitle]
                                : current.filter((item) => item !== category.categoryTitle)
                            );
                          }}
                          className="h-5 w-5 rounded border border-line"
                        />
                        <span>{category.categoryTitle}</span>
                      </label>
                    );
                  })}
                </div>
              </div>

              <div className="space-y-4 border-t border-line pt-6">
                <p className="text-xl font-medium text-text">Price Range</p>
                <input
                  type="range"
                  min="0"
                  max={Math.max(...(data?.items.map((product) => Math.ceil(product.priceUnit)) ?? [1000]))}
                  value={maxPrice}
                  onChange={(event) => setMaxPrice(Number(event.target.value))}
                  className="w-full accent-[#2563EB]"
                />
                <div className="flex items-center justify-between text-base text-text">
                  <span>$0</span>
                  <span>{formatCurrency(maxPrice)}</span>
                </div>
              </div>
            </div>
          </aside>

          <section className="space-y-6">
            <div className="flex flex-col gap-4 lg:flex-row lg:items-center lg:justify-between">
              <p className="text-2xl text-muted">
                Showing <span className="font-semibold text-text">{products.length}</span> results
              </p>

              <div className="flex flex-wrap items-center gap-3">
                <select
                  value={sortBy}
                  onChange={(event) => setSortBy(event.target.value)}
                  className="h-12 rounded-2xl border border-line bg-surface px-4 text-sm text-text"
                >
                  <option value="recommended">Recommended</option>
                  <option value="price-high">High price</option>
                  <option value="price-low">Low price</option>
                </select>

                <div className="flex overflow-hidden rounded-2xl border border-line bg-surface">
                  <button
                    type="button"
                    onClick={() => setViewMode("grid")}
                    className={`px-4 py-3 text-sm ${viewMode === "grid" ? "bg-page text-text" : "text-muted"}`}
                  >
                    Grid
                  </button>
                  <button
                    type="button"
                    onClick={() => setViewMode("list")}
                    className={`px-4 py-3 text-sm ${viewMode === "list" ? "bg-page text-text" : "text-muted"}`}
                  >
                    List
                  </button>
                </div>
              </div>
            </div>

            <div className={viewMode === "grid" ? "grid gap-6 md:grid-cols-2 xl:grid-cols-3" : "grid gap-6"}>
              {products.map((product) => (
                <ProductCard
                  key={product.productId}
                  product={product}
                  onQuickView={setSelectedProduct}
                  layout={viewMode}
                  onAddToCart={(selected) => {
                    void addItem(selected).then((added) => {
                      if (added) {
                        notify({
                          title: "Added to cart",
                          description: `${selected.productTitle} was added to your cart.`
                        });
                      }
                    });
                  }}
                />
              ))}
            </div>
          </section>
        </div>
      )}

      <ProductQuickView
        product={selectedProduct}
        open={Boolean(selectedProduct)}
        onClose={() => setSelectedProduct(null)}
      />
    </div>
  );
}
