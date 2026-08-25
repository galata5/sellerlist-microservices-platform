"use client";

import { useQuery } from "@tanstack/react-query";
import { useRouter } from "next/navigation";

import { Button } from "@/components/atoms/button";
import { Text } from "@/components/atoms/text";
import { EmptyState } from "@/components/molecules/empty-state";
import { SkeletonBlock } from "@/components/molecules/skeleton-block";
import { StatusChip } from "@/components/molecules/status-chip";
import { MarketplaceSectionHero } from "@/components/ui/marketplace-section-hero";
import { getOrderWorkspace } from "@/features/orders/api";
import { formatCurrency, formatDate } from "@/lib/formatters";

export function OrdersOverview() {
  const router = useRouter();
  const workspaceQuery = useQuery({
    queryKey: ["orders-workspace"],
    queryFn: getOrderWorkspace,
    refetchInterval: 20000,
    refetchIntervalInBackground: true
  });

  if (workspaceQuery.isLoading) {
    return (
      <div className="grid gap-4">
        {Array.from({ length: 4 }).map((_, index) => (
          <SkeletonBlock key={index} className="h-36" />
        ))}
      </div>
    );
  }

  if (!workspaceQuery.data) {
    return (
      <EmptyState
        title="Order history is not available right now."
        description="We could not load the order and payment data yet."
      />
    );
  }

  const { orders, payments } = workspaceQuery.data;
  const paymentsByOrderId = new Map(
    payments.items.map((payment) => [payment.order?.orderId, payment])
  );
  const authRequired =
    orders.status === 401 ||
    orders.status === 403 ||
    payments.status === 401 ||
    payments.status === 403;

  if (authRequired) {
    return (
      <EmptyState
        title="Sign in to view your orders."
        description="Your recent orders and payment updates appear here after you sign in."
          actionLabel="Open sign in"
        onAction={() => {
          router.push("/login");
        }}
      />
    );
  }

  if (!orders.sourceAvailable && !payments.sourceAvailable) {
    return (
      <EmptyState
        title="Orders are not available right now."
        description="The page loaded, but the order and payment services did not return data yet."
        actionLabel="Try again"
        onAction={() => void workspaceQuery.refetch()}
      />
    );
  }

  if (orders.items.length === 0) {
    return (
      <div className="space-y-6">
        <div className="surface-panel-strong p-6 sm:p-8">
          <span className="eyebrow">Orders</span>
          <h1 className="headline mt-4">Keep track of your orders.</h1>
          <Text size="sm" className="mt-3 max-w-2xl">
            Your order history will appear here once you complete a purchase.
          </Text>
        </div>
        <EmptyState
          title="You have not placed any orders yet."
          description="Browse products, add what you want to the cart, and come back here after checkout."
          actionLabel="Browse products"
          onAction={() => {
            router.push("/products");
          }}
        />
      </div>
    );
  }

  return (
    <div className="space-y-6">
      <MarketplaceSectionHero
        eyebrow="Orders"
        title="See every purchase in one refined timeline."
        description="Track when orders were placed, what they cost, and whether payment was confirmed without leaving the marketplace flow."
        stats={[
          { label: "Orders", value: String(orders.items.length) },
          { label: "Payments", value: String(payments.items.length) },
          { label: "Access", value: authRequired ? "Sign in" : "Active" }
        ]}
        aside={
          !payments.sourceAvailable ? (
            <div className="rounded-[24px] border border-white/12 bg-white/10 p-5 text-sm text-white/82 backdrop-blur-md">
              Payment updates are temporarily unavailable, but your order feed is still visible.
            </div>
          ) : undefined
        }
      />

      <div className="grid gap-4">
        {orders.items.map((order) => {
          const payment = paymentsByOrderId.get(order.orderId);

          return (
            <article
              key={order.orderId}
              className="surface-panel grid gap-4 p-6 lg:grid-cols-[0.8fr_1.2fr_0.7fr]"
            >
              <div>
                <p className="text-xs uppercase tracking-[0.22em] text-muted">
                  Order reference
                </p>
                <h2 className="mt-2 text-2xl font-semibold text-text">#{order.orderId}</h2>
                <Text size="sm" className="mt-2">
                  {formatDate(order.orderDate)}
                </Text>
              </div>
              <div>
                <p className="text-xs uppercase tracking-[0.22em] text-muted">Order notes</p>
                <Text className="mt-2 max-w-2xl" size="sm">
                  {order.orderDesc || "No notes available."}
                </Text>
              </div>
              <div className="space-y-4">
                <div>
                  <p className="text-xs uppercase tracking-[0.22em] text-muted">Fee</p>
                  <p className="mt-2 text-xl font-semibold text-text">
                    {formatCurrency(order.orderFee)}
                  </p>
                </div>
                <StatusChip
                  label={
                    payment?.paymentStatus ??
                    (payments.sourceAvailable ? "No payment" : "Payment service unavailable")
                  }
                  tone={payment?.paymentStatus === "COMPLETED" ? "positive" : "neutral"}
                />
              </div>
            </article>
          );
        })}
      </div>

      <div className="flex justify-end">
        <Button variant="outline" onClick={() => router.push("/checkout")}>
          Open checkout
        </Button>
      </div>
    </div>
  );
}
