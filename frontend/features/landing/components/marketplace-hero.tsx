"use client";

import { useState } from "react";
import { useRouter } from "next/navigation";
import { motion } from "framer-motion";

import { Reveal } from "@/components/reveal";

const quickFilters = [
  { label: "Products", href: "/products" },
  { label: "Orders", href: "/orders" },
  { label: "Checkout", href: "/checkout" },
  { label: "Dashboard", href: "/dashboard" }
];

export function MarketplaceHero() {
  const router = useRouter();
  const [searchTerm, setSearchTerm] = useState("");

  return (
    <section className="px-4 pt-4 sm:px-6 lg:px-8">
      <div className="mx-auto max-w-[1240px]">
        <div
          className="relative overflow-hidden rounded-[28px] bg-cover bg-center shadow-[0_24px_60px_rgba(17,24,39,0.12)]"
          style={{
            backgroundImage:
              "url('https://images.unsplash.com/photo-1483985988355-763728e1935b?auto=format&fit=crop&w=1600&q=80')"
          }}
        >
          <div className="absolute inset-0 bg-[rgba(15,23,42,0.34)]" />
          <div className="relative flex min-h-[480px] items-center px-6 py-14 sm:px-10">
            <div className="w-full max-w-[680px]">
              <motion.div
                initial={{ opacity: 0, y: 24 }}
                animate={{ opacity: 1, y: 0 }}
                transition={{ duration: 0.6, ease: "easeOut" }}
                className="space-y-7"
              >
                <div className="space-y-4">
                  <p className="text-xs font-medium uppercase tracking-[0.22em] text-white/68">
                    e-shope marketplace
                  </p>
                  <h1 className="text-balance text-[clamp(2.5rem,5vw,4.2rem)] font-semibold leading-[1] tracking-[-0.04em] text-white">
                    Shop, track orders, and keep everything in one place.
                  </h1>
                  <p className="max-w-[36rem] text-base leading-7 text-white/82">
                    A straightforward storefront for browsing products, checking out, and following your orders without the usual clutter.
                  </p>
                </div>

                <form
                  className="flex w-full max-w-[560px] items-center gap-3 rounded-[18px] bg-white px-4 py-3 shadow-[0_14px_34px_rgba(17,24,39,0.18)]"
                  onSubmit={(event) => {
                    event.preventDefault();
                    const nextQuery = searchTerm.trim();
                    router.push(nextQuery ? `/products?search=${encodeURIComponent(nextQuery)}` : "/products");
                  }}
                >
                  <div className="flex h-11 w-11 shrink-0 items-center justify-center rounded-full bg-[#F5F7FA] text-[#6B7280]">
                    <SearchIcon />
                  </div>
                  <input
                    type="text"
                    placeholder="Search products by name, SKU, or category"
                    value={searchTerm}
                    onChange={(event) => setSearchTerm(event.target.value)}
                    className="h-12 flex-1 bg-transparent text-base text-[#111827] outline-none placeholder:text-[#9CA3AF]"
                  />
                  <motion.button
                    type="submit"
                    className="flex h-12 w-12 shrink-0 items-center justify-center rounded-full bg-[#2F80ED] text-white shadow-[0_12px_24px_rgba(47,128,237,0.35)]"
                    whileHover={{ scale: 1.04, boxShadow: "0 16px 28px rgba(47,128,237,0.4)" }}
                    whileTap={{ scale: 0.96 }}
                    transition={{ duration: 0.2, ease: "easeInOut" }}
                    aria-label="Search listings"
                  >
                    <ArrowIcon />
                  </motion.button>
                </form>

                <div className="flex flex-wrap items-center gap-3">
                  {quickFilters.map((filter, index) => (
                    <Reveal key={filter.href} delay={0.08 + index * 0.05}>
                      <motion.button
                        type="button"
                        className="rounded-full bg-white px-4 py-2.5 text-sm font-medium text-[#111827] shadow-[0_8px_20px_rgba(17,24,39,0.1)] transition"
                        whileHover={{ y: -2, backgroundColor: "#EAF3FF" }}
                        whileTap={{ scale: 0.98 }}
                        onClick={() => router.push(filter.href)}
                      >
                        {filter.label}
                      </motion.button>
                    </Reveal>
                  ))}
                </div>
              </motion.div>
            </div>
          </div>
        </div>
      </div>
    </section>
  );
}

function SearchIcon() {
  return (
    <svg viewBox="0 0 24 24" className="h-5 w-5" fill="none" stroke="currentColor" strokeWidth="1.8">
      <circle cx="11" cy="11" r="6.5" />
      <path d="M16 16L21 21" strokeLinecap="round" />
    </svg>
  );
}

function ArrowIcon() {
  return (
    <svg viewBox="0 0 24 24" className="h-5 w-5" fill="none" stroke="currentColor" strokeWidth="2">
      <path d="M5 12H19" strokeLinecap="round" />
      <path d="M12 5L19 12L12 19" strokeLinecap="round" strokeLinejoin="round" />
    </svg>
  );
}
