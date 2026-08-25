"use client";

import { useMutation } from "@tanstack/react-query";
import { useRouter } from "next/navigation";
import { type ReactNode, useMemo, useState } from "react";

import { Button } from "@/components/atoms/button";
import { Text } from "@/components/atoms/text";
import { EmptyState } from "@/components/molecules/empty-state";
import { MarketplaceSectionHero } from "@/components/ui/marketplace-section-hero";
import { useToast } from "@/components/ui/toast";
import { useCart } from "@/features/cart/cart-provider";
import { placeOrder } from "@/features/checkout/api";
import { sanitizeApiErrorMessage } from "@/lib/api/client";
import type { CheckoutInput } from "@/lib/api/types";
import { formatCurrency } from "@/lib/formatters";

type CheckoutStep = "details" | "review";

const initialForm: CheckoutInput = {
  fullName: "",
  phoneNumber: "",
  city: "",
  streetAddress: "",
  postalCode: "",
  email: "",
  paymentMethod: "CASH_ON_DELIVERY",
  notes: ""
};

export function CheckoutExperience() {
  const router = useRouter();
  const { items, subtotal, updateQuantity, removeItem, clearCart } = useCart();
  const { notify } = useToast();
  const [step, setStep] = useState<CheckoutStep>("details");
  const [form, setForm] = useState<CheckoutInput>(initialForm);
  const [validationError, setValidationError] = useState<string | null>(null);

  const placeOrderMutation = useMutation({
    mutationFn: placeOrder,
    onSuccess(result) {
      void clearCart();
      setForm(initialForm);
      setStep("details");
      setValidationError(null);
      notify({
        title: "Checkout submitted",
        description: "Your order was submitted and a payment record was provisioned in the background."
      });
      router.push(`/orders?created=${result.order.orderId}`);
    }
  });

  const reviewRows = useMemo(
    () =>
      items.map((item) => ({
        id: item.product.productId,
        title: item.product.productTitle,
        quantity: item.quantity,
        unitPrice: item.product.priceUnit,
        total: item.product.priceUnit * item.quantity,
        category: item.product.category?.categoryTitle ?? "Product"
      })),
    [items]
  );

  function updateField<K extends keyof CheckoutInput>(key: K, value: CheckoutInput[K]) {
    setForm((current) => ({ ...current, [key]: value }));
  }

  function validateForm() {
    if (!form.fullName.trim()) {
      return "Full name is required.";
    }
    if (!form.phoneNumber.trim()) {
      return "Phone number is required.";
    }
    if (!form.city.trim()) {
      return "City is required.";
    }
    if (!form.streetAddress.trim()) {
      return "Street address is required.";
    }
    if (!form.postalCode.trim()) {
      return "Postal code is required.";
    }
    if (form.email && !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(form.email)) {
      return "Email address is not valid.";
    }
    return null;
  }

  function moveToReview() {
    const error = validateForm();
    setValidationError(error);
    if (!error) {
      setStep("review");
    }
  }

  if (items.length === 0) {
    return (
      <div className="space-y-6">
        <EmptyState
          title="There's nothing staged for checkout yet."
          description="Add products to your cart first, then come back here to place the order."
        />
        <div className="flex flex-wrap gap-3">
          <Button variant="outline" onClick={() => router.push("/products")}>
            Browse products
          </Button>
          <Button onClick={() => router.push("/sell")}>Sell item</Button>
        </div>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      <MarketplaceSectionHero
        eyebrow="Checkout"
        title="Finish the order in a premium, guided flow."
        description="Complete delivery details, review the cart, and confirm the purchase without losing context."
        stats={[
          { label: "Items", value: String(items.reduce((total, item) => total + item.quantity, 0)) },
          { label: "Stage", value: step === "details" ? "Details" : "Review" },
          { label: "Total", value: formatCurrency(subtotal) }
        ]}
      />

      <div className="grid gap-6 lg:grid-cols-[1.15fr_0.85fr]">
      <section className="surface-panel p-6 sm:p-8">
        <div className="mb-6 space-y-2">
          <p className="eyebrow">
            {step === "details" ? "Delivery details" : "Review and confirm"}
          </p>
          <h2 className="text-2xl font-semibold text-text">
            {step === "details"
              ? "Add delivery details before placing the order."
              : "Review the order before you confirm it."}
          </h2>
        </div>

        {step === "details" ? (
          <div className="space-y-6">
            <div className="grid gap-4">
              {items.map((item) => (
                <div
                  key={item.product.productId}
                  className="rounded-[var(--radius-xl)] border border-line bg-surface p-6"
                >
                  <div className="flex flex-col gap-4 lg:flex-row lg:items-center lg:justify-between">
                    <div>
                      <p className="eyebrow mb-2">{item.product.category?.categoryTitle ?? "Product"}</p>
                      <h2 className="text-2xl font-semibold text-text">{item.product.productTitle}</h2>
                      <Text size="sm" className="mt-2">
                        {formatCurrency(item.product.priceUnit)} each
                      </Text>
                    </div>

                    <div className="flex flex-wrap items-center gap-4">
                      <input
                        type="number"
                        min={1}
                        value={item.quantity}
                        onChange={(event) =>
                          updateQuantity(item.product.productId, Number(event.target.value))
                        }
                        className="h-11 w-24 rounded-full border border-line bg-surface px-4 text-sm text-text"
                      />
                      <Button variant="ghost" onClick={() => removeItem(item.product.productId)}>
                        Remove item
                      </Button>
                    </div>
                  </div>
                </div>
              ))}
            </div>

            <div className="grid gap-4 md:grid-cols-2">
              <Field label="Full name" required>
                <input
                  value={form.fullName}
                  onChange={(event) => updateField("fullName", event.target.value)}
                  className="h-12 rounded-[var(--radius-xl)] border border-line bg-surface px-4 text-sm text-text"
                  placeholder="John Doe"
                />
              </Field>
              <Field label="Phone number" required>
                <input
                  value={form.phoneNumber}
                  onChange={(event) => updateField("phoneNumber", event.target.value)}
                  className="h-12 rounded-[var(--radius-xl)] border border-line bg-surface px-4 text-sm text-text"
                  placeholder="+212 600000000"
                />
              </Field>
              <Field label="City" required>
                <input
                  value={form.city}
                  onChange={(event) => updateField("city", event.target.value)}
                  className="h-12 rounded-[var(--radius-xl)] border border-line bg-surface px-4 text-sm text-text"
                  placeholder="Casablanca"
                />
              </Field>
              <Field label="Postal code" required>
                <input
                  value={form.postalCode}
                  onChange={(event) => updateField("postalCode", event.target.value)}
                  className="h-12 rounded-[var(--radius-xl)] border border-line bg-surface px-4 text-sm text-text"
                  placeholder="20000"
                />
              </Field>
            </div>

            <Field label="Street address" required>
              <input
                value={form.streetAddress}
                onChange={(event) => updateField("streetAddress", event.target.value)}
                className="h-12 rounded-[var(--radius-xl)] border border-line bg-surface px-4 text-sm text-text"
                placeholder="Street, building, apartment"
              />
            </Field>

            <div className="grid gap-4 md:grid-cols-2">
              <Field label="Email (optional)">
                <input
                  value={form.email ?? ""}
                  onChange={(event) => updateField("email", event.target.value)}
                  className="h-12 rounded-[var(--radius-xl)] border border-line bg-surface px-4 text-sm text-text"
                  placeholder="john@example.com"
                />
              </Field>
              <Field label="Payment method" required>
                <select
                  value={form.paymentMethod}
                  onChange={(event) => updateField("paymentMethod", event.target.value as CheckoutInput["paymentMethod"])}
                  className="h-12 rounded-[var(--radius-xl)] border border-line bg-surface px-4 text-sm text-text"
                >
                  <option value="CASH_ON_DELIVERY">Cash on delivery</option>
                </select>
              </Field>
            </div>

            <Field label="Order notes">
              <textarea
                className="min-h-32 rounded-[var(--radius-xl)] border border-line bg-surface px-4 py-4 text-sm text-text placeholder:text-muted/70"
                placeholder="Delivery note, preferred timing, or anything the seller should know."
                value={form.notes}
                onChange={(event) => updateField("notes", event.target.value)}
              />
            </Field>
          </div>
        ) : (
          <div className="space-y-6">
            <div className="rounded-[var(--radius-xl)] border border-line bg-surface p-6">
              <p className="eyebrow mb-4">Order review</p>
              <div className="space-y-4">
                {reviewRows.map((item) => (
                  <div key={item.id} className="flex items-start justify-between gap-4 border-b border-line/70 pb-4 last:border-b-0 last:pb-0">
                    <div>
                      <p className="text-sm uppercase tracking-[0.22em] text-muted">{item.category}</p>
                      <h2 className="mt-1 text-lg font-semibold text-text">{item.title}</h2>
                      <Text size="sm">Quantity: {item.quantity}</Text>
                    </div>
                    <div className="text-right">
                      <Text size="sm">{formatCurrency(item.unitPrice)} each</Text>
                      <p className="mt-1 text-lg font-semibold text-text">{formatCurrency(item.total)}</p>
                    </div>
                  </div>
                ))}
              </div>
            </div>

            <div className="grid gap-4 md:grid-cols-2">
              <ReviewCard title="Delivery">
                <p>{form.fullName}</p>
                <p>{form.phoneNumber}</p>
                <p>{form.streetAddress}</p>
                <p>
                  {form.city}, {form.postalCode}
                </p>
                {form.email ? <p>{form.email}</p> : null}
              </ReviewCard>
              <ReviewCard title="Payment">
                <p>Method: Cash on delivery</p>
                <p>Status: Unpaid</p>
              </ReviewCard>
            </div>

            <ReviewCard title="Notes">
              <p>{form.notes.trim() || "No notes provided."}</p>
            </ReviewCard>
          </div>
        )}
      </section>

      <aside className="surface-panel-strong p-6 sm:p-8">
        <div className="space-y-6">
          <div>
            <span className="eyebrow">Order summary</span>
            <p className="mt-4 text-3xl font-semibold text-text">{formatCurrency(subtotal)}</p>
          </div>

          <div className="rounded-[var(--radius-xl)] border border-line bg-surface px-4 py-4 text-sm text-muted">
            <p>Items: {items.reduce((total, item) => total + item.quantity, 0)}</p>
            <p className="mt-2">Payment: Cash on delivery</p>
            <p className="mt-2">Payment status: Unpaid</p>
          </div>

          {validationError ? (
            <div className="rounded-[var(--radius-xl)] border border-accent/40 bg-surface px-4 py-4">
              <Text size="sm">{validationError}</Text>
            </div>
          ) : null}

          {placeOrderMutation.isError ? (
            <div className="rounded-[var(--radius-xl)] border border-accent/40 bg-surface px-4 py-4">
              <Text size="sm">
                {placeOrderMutation.error instanceof Error
                  ? sanitizeApiErrorMessage(placeOrderMutation.error.message)
                  : "Checkout failed."}
              </Text>
            </div>
          ) : null}

          {step === "details" ? (
            <div className="space-y-3">
              <Button className="w-full" onClick={moveToReview}>
                Review order
              </Button>
              <Button className="w-full" variant="outline" onClick={() => router.push("/products")}>
                Continue shopping
              </Button>
            </div>
          ) : (
            <div className="space-y-3">
              <Button
                className="w-full"
                disabled={placeOrderMutation.isPending}
                onClick={() => placeOrderMutation.mutate(form)}
              >
                {placeOrderMutation.isPending ? "Confirming..." : "Confirm order"}
              </Button>
              <Button className="w-full" variant="outline" onClick={() => setStep("details")}>
                Back to edit
              </Button>
              <Button className="w-full" variant="ghost" onClick={() => router.push("/products")}>
                Cancel and return
              </Button>
            </div>
          )}
        </div>
      </aside>
      </div>
    </div>
  );
}

function Field({
  label,
  required,
  children
}: {
  label: string;
  required?: boolean;
  children: ReactNode;
}) {
  return (
    <label className="flex flex-col gap-2">
      <span className="text-xs uppercase tracking-[0.22em] text-muted">
        {label}
        {required ? " *" : ""}
      </span>
      {children}
    </label>
  );
}

function ReviewCard({
  title,
  children
}: {
  title: string;
  children: ReactNode;
}) {
  return (
    <div className="rounded-[var(--radius-xl)] border border-line bg-surface p-6">
      <p className="eyebrow mb-3">{title}</p>
      <div className="space-y-2 text-sm text-text">{children}</div>
    </div>
  );
}
