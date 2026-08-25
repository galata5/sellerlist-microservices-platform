"use client";

import { useQuery } from "@tanstack/react-query";
import { useRouter } from "next/navigation";

import { Button } from "@/components/atoms/button";
import { Text } from "@/components/atoms/text";
import { EmptyState } from "@/components/molecules/empty-state";
import { SkeletonBlock } from "@/components/molecules/skeleton-block";
import { StatusChip } from "@/components/molecules/status-chip";
import { MarketplaceSectionHero } from "@/components/ui/marketplace-section-hero";
import { getDashboardSnapshot } from "@/features/dashboard/api";
import { formatCompactNumber, formatCurrency, formatDate } from "@/lib/formatters";

const statLabels = [
  { key: "products", label: "Products" },
  { key: "orders", label: "Orders" },
  { key: "payments", label: "Payments" },
  { key: "users", label: "Users" }
] as const;

export function DashboardOverview() {
  const router = useRouter();
  const { data, isLoading, refetch } = useQuery({
    queryKey: ["dashboard-snapshot"],
    queryFn: getDashboardSnapshot,
    refetchInterval: 20000,
    refetchIntervalInBackground: true
  });

  if (isLoading) {
    return (
      <div className="grid gap-6">
        <SkeletonBlock className="h-32" />
        <div className="grid gap-6 lg:grid-cols-[1.2fr_0.8fr]">
          <SkeletonBlock className="h-[28rem]" />
          <SkeletonBlock className="h-[28rem]" />
        </div>
      </div>
    );
  }

  if (!data) {
    return (
      <EmptyState
        title="Dashboard data is not available right now."
        description="One or more backend services did not respond yet."
        actionLabel="Try again"
        onAction={() => void refetch()}
      />
    );
  }

  const availableSections = statLabels.filter(({ key }) => data[key].sourceAvailable);
  const completedPayments = data.payments.items.filter(
    (payment) => payment.paymentStatus === "COMPLETED"
  ).length;
  const conversionRate =
    data.payments.items.length === 0 ? 0 : completedPayments / data.payments.items.length;
  const featuredProduct = data.products.items[0];
  const latestOrder = data.orders.items[0];
  const unavailableSections = statLabels.filter(({ key }) => !data[key].sourceAvailable);
  const protectedSectionsMissing =
    data.orders.status === 401 ||
    data.orders.status === 403 ||
    data.payments.status === 401 ||
    data.payments.status === 403 ||
    data.users.status === 401 ||
    data.users.status === 403;

  if (availableSections.length === 0) {
    return (
      <EmptyState
        title={
          protectedSectionsMissing
            ? "Sign in to open the dashboard."
            : "Dashboard data is not available yet."
        }
        description={
          protectedSectionsMissing
            ? "Products are public, but orders, payments, and account data require an active session."
            : "The page loaded, but the backend services did not return dashboard data yet."
        }
        actionLabel={protectedSectionsMissing ? "Open sign in" : "Try again"}
        onAction={() =>
          protectedSectionsMissing ? router.push("/login") : void refetch()
        }
      />
    );
  }

  return (
    <div className="space-y-8">
      <MarketplaceSectionHero
        eyebrow="Dashboard"
        title="A clean command view for your marketplace activity."
        description="Monitor catalog health, order momentum, and payment coverage from one consistent control surface."
        stats={[
          { label: "Products", value: formatCompactNumber(data.products.items.length) },
          { label: "Orders", value: formatCompactNumber(data.orders.items.length) },
          { label: "Payments", value: formatCompactNumber(data.payments.items.length) }
        ]}
        aside={
          unavailableSections.length > 0 ? (
            <div className="rounded-[24px] border border-white/12 bg-white/10 p-5 text-sm text-white/82 backdrop-blur-md">
              <p className="text-[0.68rem] font-semibold uppercase tracking-[0.18em] text-white/58">
                Service status
              </p>
              <p className="mt-3">
                {protectedSectionsMissing
                  ? "Sign in to load protected information such as orders, payments, and users."
                  : `Some services are still unavailable: ${unavailableSections
                      .map((section) => section.label.toLowerCase())
                      .join(", ")}.`}
              </p>
            </div>
          ) : undefined
        }
      />

      <section className="surface-panel-strong p-6 sm:p-8">
        <div className="grid gap-4 md:grid-cols-2 xl:grid-cols-4">
          {statLabels.map((stat) => (
            <div
              key={stat.key}
              className="rounded-[var(--radius-xl)] border border-line bg-surface p-6"
            >
              <p className="text-xs uppercase tracking-[0.24em] text-muted">
                {stat.label}
              </p>
              <p className="mt-4 text-3xl font-semibold text-text">
                {formatCompactNumber(data[stat.key].items.length)}
              </p>
              <Text size="sm" className="mt-2">
                {data[stat.key].sourceAvailable ? "Available" : "Unavailable"}
              </Text>
            </div>
          ))}
        </div>
      </section>

      <section className="grid gap-6 lg:grid-cols-[1.2fr_0.8fr]">
        <article className="surface-panel p-6 sm:p-8">
          <div className="flex items-end justify-between gap-4">
            <div>
              <p className="eyebrow">Recent activity</p>
              <h2 className="mt-2 text-2xl font-semibold text-text">Latest order</h2>
            </div>
            <StatusChip
              label={`${Math.round(conversionRate * 100)}% payment completion`}
              tone={conversionRate > 0.5 ? "positive" : "neutral"}
            />
          </div>

          {latestOrder ? (
            <div className="mt-8 grid gap-4">
              <div className="rounded-[var(--radius-xl)] border border-line bg-surface p-6">
                <p className="text-xs uppercase tracking-[0.22em] text-muted">
                  Order reference
                </p>
                <p className="mt-2 text-2xl font-semibold text-text">
                  #{latestOrder.orderId}
                </p>
                <Text size="sm" className="mt-2">
                  {latestOrder.orderDesc || "No order notes were added."}
                </Text>
              </div>
              <div className="grid gap-4 sm:grid-cols-2">
                <div className="rounded-[var(--radius-xl)] border border-line bg-surface p-6">
                  <p className="text-xs uppercase tracking-[0.22em] text-muted">
                    Order fee
                  </p>
                  <p className="mt-2 text-xl font-semibold text-text">
                    {formatCurrency(latestOrder.orderFee)}
                  </p>
                </div>
                <div className="rounded-[var(--radius-xl)] border border-line bg-surface p-6">
                  <p className="text-xs uppercase tracking-[0.22em] text-muted">
                    Order date
                  </p>
                  <p className="mt-2 text-xl font-semibold text-text">
                    {formatDate(latestOrder.orderDate)}
                  </p>
                </div>
              </div>
            </div>
          ) : (
            <div className="mt-8 rounded-[var(--radius-xl)] border border-line bg-surface p-6">
              <Text size="sm">
                {data.orders.sourceAvailable
                  ? "No orders have been recorded yet."
                  : "The order stream is temporarily unavailable."}
              </Text>
            </div>
          )}
        </article>

        <article className="surface-panel p-6 sm:p-8">
          <p className="eyebrow">Featured product</p>
          <h2 className="mt-2 text-2xl font-semibold text-text">From the catalog</h2>
          {featuredProduct ? (
            <div className="mt-8 space-y-5">
              <div className="grid h-56 place-items-center rounded-[18px] bg-[#F8FAFC] text-center">
                <div>
                  <p className="mb-4 text-xs font-medium uppercase tracking-[0.18em] text-[#6B7280]">
                    {featuredProduct.category?.categoryTitle ?? "Product"}
                  </p>
                  <p className="text-2xl font-semibold text-text">
                    {featuredProduct.productTitle}
                  </p>
                </div>
              </div>
              <div className="rounded-[var(--radius-xl)] border border-line bg-surface p-6">
                <p className="text-xs uppercase tracking-[0.22em] text-muted">
                  Current unit price
                </p>
                <p className="mt-2 text-2xl font-semibold text-text">
                  {formatCurrency(featuredProduct.priceUnit)}
                </p>
              </div>
                <Button variant="outline" onClick={() => router.push(`/products/${featuredProduct.productId}`)}>
                  View product
                </Button>
            </div>
          ) : (
            <div className="mt-8 rounded-[var(--radius-xl)] border border-line bg-surface p-6">
              <div className="space-y-4">
                <Text size="sm">
                  {data.products.sourceAvailable
                    ? "No products are available yet. Use Sell item to publish the first listing."
                    : "The product feed is temporarily unavailable, but you can still open the listing flow."}
                </Text>
                <Button variant="outline" onClick={() => router.push("/sell")}>
                  Sell item
                </Button>
              </div>
            </div>
          )}
        </article>
      </section>
    </div>
  );
}
