"use client";

import Link from "next/link";
import { useRef } from "react";
import { motion } from "framer-motion";
import { useQuery } from "@tanstack/react-query";

import { Button } from "@/components/atoms/button";
import { Reveal } from "@/components/reveal";
import { getProductCatalog } from "@/features/products/api";

type LandingItem = {
  id: string;
  title: string;
  price: string;
  image: string;
  href?: string;
};

const fallbackItems: LandingItem[] = [
  {
    id: "fallback-1",
    title: "Textured denim jacket",
    price: "$82",
    image:
      "https://images.unsplash.com/photo-1541099649105-f69ad21f3246?auto=format&fit=crop&w=900&q=80"
  },
  {
    id: "fallback-2",
    title: "Leather city tote",
    price: "$64",
    image:
      "https://images.unsplash.com/photo-1548036328-c9fa89d128fa?auto=format&fit=crop&w=900&q=80"
  },
  {
    id: "fallback-3",
    title: "Clean white sneakers",
    price: "$91",
    image:
      "https://images.unsplash.com/photo-1542291026-7eec264c27ff?auto=format&fit=crop&w=900&q=80"
  },
  {
    id: "fallback-4",
    title: "Botanical skin set",
    price: "$38",
    image:
      "https://images.unsplash.com/photo-1526045478516-99145907023c?auto=format&fit=crop&w=900&q=80"
  },
  {
    id: "fallback-5",
    title: "Kids weekend knit",
    price: "$29",
    image:
      "https://images.unsplash.com/photo-1515886657613-9f3515b0c78f?auto=format&fit=crop&w=900&q=80"
  },
  {
    id: "fallback-6",
    title: "Studio ceramic vase",
    price: "$47",
    image:
      "https://images.unsplash.com/photo-1616627453124-d4f7027d7d73?auto=format&fit=crop&w=900&q=80"
  }
];

export function RecentlyListed() {
  const scrollerRef = useRef<HTMLDivElement>(null);
  const { data } = useQuery({
    queryKey: ["landing-products"],
    queryFn: getProductCatalog,
    refetchInterval: 20000,
    refetchIntervalInBackground: true
  });

  const catalogItems: LandingItem[] =
    data?.items.slice(0, 8).map((product) => ({
      id: String(product.productId),
      href: `/products/${product.productId}`,
      title: product.productTitle,
      price: new Intl.NumberFormat("en-US", {
        style: "currency",
        currency: "USD"
      }).format(product.priceUnit),
      image:
        product.imageUrl?.trim() ||
        "https://images.unsplash.com/photo-1523381210434-271e8be1f52b?auto=format&fit=crop&w=900&q=80"
    })) ?? [];

  const recentItems = catalogItems.length > 0 ? catalogItems : fallbackItems;
  const extraItems = recentItems.slice(4, 8);

  const scroll = (direction: "left" | "right") => {
    const node = scrollerRef.current;
    if (!node) {
      return;
    }

    node.scrollBy({
      left: direction === "left" ? -360 : 360,
      behavior: "smooth"
    });
  };

  return (
    <section className="px-4 py-12 sm:px-6 lg:px-8">
      <div className="mx-auto max-w-[1240px]">
        <div className="mb-6 flex items-center justify-between gap-4">
          <div>
            <p className="text-sm font-medium uppercase tracking-[0.2em] text-[#6B7280]">
              Recently added
            </p>
            <h2 className="mt-2 text-2xl font-semibold tracking-[-0.03em] text-[#111827] sm:text-3xl">
              Listed recently
            </h2>
          </div>

          <div className="hidden items-center gap-3 sm:flex">
            <CarouselButton direction="left" onClick={() => scroll("left")} />
            <CarouselButton direction="right" onClick={() => scroll("right")} />
          </div>
        </div>

        <div
          ref={scrollerRef}
          className="scrollbar-none flex gap-5 overflow-x-auto pb-4"
        >
          {recentItems.map((item, index) => (
            <Reveal
              key={item.id}
              delay={index * 0.06}
              className="min-w-[260px] max-w-[260px] flex-none sm:min-w-[300px] sm:max-w-[300px]"
            >
              <motion.article
                className="overflow-hidden rounded-2xl bg-white shadow-[0_14px_34px_rgba(17,24,39,0.08)]"
                whileHover={{ y: -3 }}
                transition={{ duration: 0.24, ease: "easeOut" }}
              >
                {item.href ? (
                  <Link href={item.href} className="block">
                    <ProductCardContent item={item} />
                  </Link>
                ) : (
                  <ProductCardContent item={item} />
                )}
              </motion.article>
            </Reveal>
          ))}
        </div>

        <div className="mt-6 flex items-center justify-center gap-3 sm:hidden">
          <CarouselButton direction="left" onClick={() => scroll("left")} />
          <CarouselButton direction="right" onClick={() => scroll("right")} />
        </div>

        <div className="mt-8 grid gap-4 md:grid-cols-2 xl:grid-cols-4">
          {extraItems.map((item) => (
            <Link
              key={`more-${item.id}`}
              href={item.href ?? "/products"}
              className="rounded-2xl border border-line bg-white p-5 shadow-[0_10px_24px_rgba(17,24,39,0.05)] transition hover:-translate-y-0.5"
            >
              <p className="text-xs font-medium uppercase tracking-[0.18em] text-[#6B7280]">
                More to browse
              </p>
              <h3 className="mt-3 text-base font-semibold text-[#111827]">{item.title}</h3>
              <p className="mt-2 text-sm text-[#6B7280]">{item.price}</p>
            </Link>
          ))}
        </div>

        <div className="mt-8 flex justify-center">
          <Link href="/products">
            <Button variant="outline">See all products</Button>
          </Link>
        </div>
      </div>
    </section>
  );
}

function ProductCardContent({ item }: { item: LandingItem }) {
  return (
    <>
      <div className="h-[320px] w-full overflow-hidden bg-[#E5E7EB]">
        <img
          src={item.image}
          alt={item.title}
          loading="lazy"
          className="h-full w-full object-cover"
        />
      </div>
      <div className="space-y-2 px-5 py-4">
        <h3 className="text-base font-semibold text-[#111827]">{item.title}</h3>
        <p className="text-sm text-[#6B7280]">{item.price}</p>
      </div>
    </>
  );
}

function CarouselButton({
  direction,
  onClick
}: {
  direction: "left" | "right";
  onClick: () => void;
}) {
  const isLeft = direction === "left";

  return (
    <motion.button
      type="button"
      onClick={onClick}
      className="flex h-12 w-12 items-center justify-center rounded-full bg-white text-[#111827] shadow-[0_12px_30px_rgba(17,24,39,0.1)] transition hover:bg-[#EAF3FF] hover:text-[#2F80ED]"
      whileHover={{ y: -2 }}
      whileTap={{ scale: 0.96 }}
      aria-label={`Scroll ${direction}`}
    >
      <svg viewBox="0 0 24 24" className="h-5 w-5" fill="none" stroke="currentColor" strokeWidth="2">
        {isLeft ? (
          <>
            <path d="M19 12H5" strokeLinecap="round" />
            <path d="M12 19L5 12L12 5" strokeLinecap="round" strokeLinejoin="round" />
          </>
        ) : (
          <>
            <path d="M5 12H19" strokeLinecap="round" />
            <path d="M12 5L19 12L12 19" strokeLinecap="round" strokeLinejoin="round" />
          </>
        )}
      </svg>
    </motion.button>
  );
}
