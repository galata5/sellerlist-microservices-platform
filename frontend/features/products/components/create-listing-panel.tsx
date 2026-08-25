"use client";

import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { useRouter } from "next/navigation";
import { useMemo, useState } from "react";

import { Button } from "@/components/atoms/button";
import { Text } from "@/components/atoms/text";
import { MarketplaceSectionHero } from "@/components/ui/marketplace-section-hero";
import { sanitizeApiErrorMessage } from "@/lib/api/client";
import { useAuthSession } from "@/features/auth/auth-provider";
import { createProduct, getCategories } from "@/features/products/api";
import { useToast } from "@/components/ui/toast";

type FormState = {
  productTitle: string;
  sku: string;
  priceUnit: string;
  quantity: string;
  imageUrl: string;
  categoryId: string;
  categoryTitle: string;
};

const initialState: FormState = {
  productTitle: "",
  sku: "",
  priceUnit: "",
  quantity: "1",
  imageUrl: "",
  categoryId: "",
  categoryTitle: ""
};

export function CreateListingPanel() {
  const router = useRouter();
  const queryClient = useQueryClient();
  const { notify } = useToast();
  const { session, isAuthenticated } = useAuthSession();
  const [form, setForm] = useState<FormState>(initialState);

  const categoriesQuery = useQuery({
    queryKey: ["seller-categories"],
    queryFn: getCategories
  });

  const selectedCategory = useMemo(
    () =>
      categoriesQuery.data?.items.find(
        (category) => String(category.categoryId) === form.categoryId
      ) ?? null,
    [categoriesQuery.data, form.categoryId]
  );

  const createListingMutation = useMutation({
    mutationFn: async () => {
      const productTitle = form.productTitle.trim();
      const sku = form.sku.trim();
      const categoryTitle =
        selectedCategory?.categoryTitle ?? form.categoryTitle.trim();

      if (!productTitle || !sku || !form.priceUnit || !form.quantity || !categoryTitle) {
        throw new Error("Fill in the product title, SKU, price, quantity, and category.");
      }

      return createProduct({
        productTitle,
        sku,
        imageUrl: form.imageUrl.trim() || null,
        priceUnit: Number(form.priceUnit),
        quantity: Number(form.quantity),
        categoryId: selectedCategory?.categoryId,
        categoryTitle
      });
    },
    onSuccess(product) {
      void Promise.all([
        queryClient.invalidateQueries({ queryKey: ["product-catalog"] }),
        queryClient.invalidateQueries({ queryKey: ["products"] }),
        queryClient.invalidateQueries({ queryKey: ["dashboard-snapshot"] })
      ]);

      notify({
        title: "Listing published",
        description: `${product.productTitle} is now live in the product catalog.`
      });
      setForm(initialState);
      router.push(`/products/${product.productId}`);
    }
  });

  return (
    <div className="space-y-6">
      <MarketplaceSectionHero
        eyebrow="Sell"
        title="Launch a listing with the same polish as the storefront."
        description="Create products, place them in the right category, and publish them into the marketplace without a back-office feel."
        stats={[
          { label: "Categories", value: String(categoriesQuery.data?.items.length ?? 0) },
          { label: "Seller", value: isAuthenticated && session ? session.username : "Guest" },
          { label: "Flow", value: "Draft to live" }
        ]}
      />

      <div className="grid gap-6 lg:grid-cols-[1.05fr_0.95fr]">
      <section className="surface-panel-strong p-6 sm:p-8">
        <div className="space-y-4">
          <span className="eyebrow">Sell item</span>
          <h2 className="text-2xl font-semibold text-text">Create a listing and start selling.</h2>
          <Text>
            Add the product details, choose a category, and publish the item so shoppers can find it.
          </Text>
        </div>

        <div className="mt-8 grid gap-5">
          <label className="grid gap-2">
            <span className="text-sm font-medium text-text">Product title</span>
            <input
              className="h-12 rounded-2xl border border-line bg-surface px-4 text-sm text-text placeholder:text-muted/70"
              value={form.productTitle}
              onChange={(event) =>
                setForm((current) => ({ ...current, productTitle: event.target.value }))
              }
              placeholder="Classic wool coat"
            />
          </label>

          <div className="grid gap-5 sm:grid-cols-2">
            <label className="grid gap-2">
              <span className="text-sm font-medium text-text">SKU</span>
              <input
                className="h-12 rounded-2xl border border-line bg-surface px-4 text-sm text-text placeholder:text-muted/70"
                value={form.sku}
                onChange={(event) =>
                  setForm((current) => ({ ...current, sku: event.target.value }))
                }
                placeholder="ITEM-001"
              />
            </label>

            <label className="grid gap-2">
              <span className="text-sm font-medium text-text">Image URL</span>
              <input
                className="h-12 rounded-2xl border border-line bg-surface px-4 text-sm text-text placeholder:text-muted/70"
                value={form.imageUrl}
                onChange={(event) =>
                  setForm((current) => ({ ...current, imageUrl: event.target.value }))
                }
                placeholder="https://images.example.com/item.jpg"
              />
            </label>
          </div>

          <div className="grid gap-5 sm:grid-cols-2">
            <label className="grid gap-2">
              <span className="text-sm font-medium text-text">Price</span>
              <input
                type="number"
                min="0"
                step="0.01"
                className="h-12 rounded-2xl border border-line bg-surface px-4 text-sm text-text"
                value={form.priceUnit}
                onChange={(event) =>
                  setForm((current) => ({ ...current, priceUnit: event.target.value }))
                }
                placeholder="89.00"
              />
            </label>

            <label className="grid gap-2">
              <span className="text-sm font-medium text-text">Quantity</span>
              <input
                type="number"
                min="1"
                className="h-12 rounded-2xl border border-line bg-surface px-4 text-sm text-text"
                value={form.quantity}
                onChange={(event) =>
                  setForm((current) => ({ ...current, quantity: event.target.value }))
                }
              />
            </label>
          </div>

          <div className="grid gap-5 sm:grid-cols-2">
            <label className="grid gap-2">
              <span className="text-sm font-medium text-text">Existing category</span>
              <select
                className="h-12 rounded-2xl border border-line bg-surface px-4 text-sm text-text"
                value={form.categoryId}
                onChange={(event) =>
                  setForm((current) => ({
                    ...current,
                    categoryId: event.target.value,
                    categoryTitle: ""
                  }))
                }
              >
                <option value="">Create or choose later</option>
                {(categoriesQuery.data?.items ?? []).map((category) => (
                  <option key={category.categoryId} value={category.categoryId}>
                    {category.categoryTitle}
                  </option>
                ))}
              </select>
            </label>

            <label className="grid gap-2">
              <span className="text-sm font-medium text-text">New category title</span>
              <input
                className="h-12 rounded-2xl border border-line bg-surface px-4 text-sm text-text placeholder:text-muted/70"
                value={form.categoryTitle}
                onChange={(event) =>
                  setForm((current) => ({
                    ...current,
                    categoryTitle: event.target.value,
                    categoryId: ""
                  }))
                }
                placeholder="Outerwear"
              />
            </label>
          </div>

          {createListingMutation.isError ? (
            <div className="rounded-[var(--radius-xl)] border border-[#FECACA] bg-[#FEF2F2] px-4 py-4 text-sm text-[#B91C1C]">
              {createListingMutation.error instanceof Error
                ? sanitizeApiErrorMessage(
                    createListingMutation.error.message,
                    "The listing could not be published."
                  )
                : "The listing could not be published."}
            </div>
          ) : null}

          <div className="flex flex-wrap gap-3">
            <Button
              disabled={createListingMutation.isPending}
              onClick={() => createListingMutation.mutate()}
            >
              {createListingMutation.isPending ? "Publishing..." : "Publish listing"}
            </Button>
            <Button variant="outline" onClick={() => router.push("/products")}>
              View catalog
            </Button>
          </div>
        </div>
      </section>

      <aside className="surface-panel p-6 sm:p-8">
        <div className="space-y-6">
          <div>
            <span className="eyebrow">Helpful notes</span>
            <h2 className="mt-3 text-2xl font-semibold text-text">Before you publish</h2>
          </div>

          <div className="rounded-[var(--radius-xl)] border border-line bg-surface p-5">
            <p className="text-sm font-semibold text-text">Session</p>
            <Text size="sm" className="mt-2">
              {isAuthenticated && session
                ? `Signed in as ${session.username}.`
                : "You can fill in the form now. Sign in if your backend later requires authenticated selling."}
            </Text>
          </div>

          <div className="rounded-[var(--radius-xl)] border border-line bg-surface p-5">
            <p className="text-sm font-semibold text-text">Category service</p>
            <Text size="sm" className="mt-2">
              {categoriesQuery.data?.sourceAvailable
                ? `Loaded ${categoriesQuery.data.items.length} categories from the backend.`
                : "Category lookup is unavailable right now. You can still type a new category title and the form will create it for you first."}
            </Text>
          </div>

          <div className="rounded-[var(--radius-xl)] border border-line bg-surface p-5">
            <p className="text-sm font-semibold text-text">Selling tip</p>
            <Text size="sm" className="mt-2">
              Clear titles, accurate prices, and simple categories make listings easier to trust and easier to buy.
            </Text>
          </div>
        </div>
      </aside>
      </div>
    </div>
  );
}
