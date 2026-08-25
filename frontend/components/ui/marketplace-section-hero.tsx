import type { ReactNode } from "react";

import { Text } from "@/components/atoms/text";

type MarketplaceSectionHeroProps = {
  eyebrow: string;
  title: string;
  description: string;
  stats?: Array<{
    label: string;
    value: string;
  }>;
  aside?: ReactNode;
};

export function MarketplaceSectionHero({
  eyebrow,
  title,
  description,
  stats = [],
  aside
}: MarketplaceSectionHeroProps) {
  return (
    <section className="marketplace-hero-card overflow-hidden p-6 sm:p-8">
      <div className="relative grid gap-8 lg:grid-cols-[1.2fr_0.8fr] lg:items-end">
        <div className="relative z-[1] space-y-5">
          <span className="eyebrow text-white/72">{eyebrow}</span>
          <div className="space-y-4">
            <h1 className="headline max-w-3xl text-white">{title}</h1>
            <Text size="sm" className="max-w-2xl text-white/78">
              {description}
            </Text>
          </div>

          {stats.length > 0 ? (
            <div className="grid gap-3 sm:grid-cols-3">
              {stats.map((stat) => (
                <div
                  key={stat.label}
                  className="rounded-[20px] border border-white/12 bg-white/8 px-4 py-4 backdrop-blur-sm"
                >
                  <p className="text-[0.68rem] font-semibold uppercase tracking-[0.18em] text-white/55">
                    {stat.label}
                  </p>
                  <p className="mt-2 text-xl font-semibold tracking-[-0.03em] text-white">
                    {stat.value}
                  </p>
                </div>
              ))}
            </div>
          ) : null}
        </div>

        <div className="relative z-[1]">
          {aside ?? (
            <div className="rounded-[24px] border border-white/12 bg-white/10 p-5 backdrop-blur-md">
              <p className="text-[0.68rem] font-semibold uppercase tracking-[0.18em] text-white/58">
                Marketplace flow
              </p>
              <div className="mt-4 grid gap-3 text-sm text-white/82">
                <div className="rounded-[18px] bg-black/14 px-4 py-3">Browse products</div>
                <div className="rounded-[18px] bg-black/14 px-4 py-3">Add to cart and checkout</div>
                <div className="rounded-[18px] bg-black/14 px-4 py-3">Track orders and payments</div>
              </div>
            </div>
          )}
        </div>
      </div>
    </section>
  );
}
