"use client";

import Link from "next/link";
import { usePathname, useRouter, useSearchParams } from "next/navigation";
import { useEffect, useRef, useState } from "react";

import { Button } from "@/components/atoms/button";
import { Text } from "@/components/atoms/text";
import { useTheme } from "@/components/providers/theme-provider";
import { useAuthSession } from "@/features/auth/auth-provider";
import { useScrollState } from "@/hooks/use-scroll-state";
import { cn } from "@/lib/cn";

const navItems = [
  { label: "Home", href: "/" },
  { label: "product", href: "/products" },
  { label: "Orders", href: "/orders" },
  { label: "Checkout", href: "/checkout" },
  { label: "Dashboard", href: "/dashboard" }
];

export function MarketplaceNavbar() {
  const isScrolled = useScrollState(18);
  const pathname = usePathname();
  const router = useRouter();
  const searchParams = useSearchParams();
  const { isAuthenticated, session, clearSession } = useAuthSession();
  const { theme, resolvedTheme, setTheme } = useTheme();
  const [search, setSearch] = useState("");
  const [themeMenuOpen, setThemeMenuOpen] = useState(false);
  const themeMenuRef = useRef<HTMLDivElement | null>(null);

  useEffect(() => {
    if (pathname.startsWith("/products")) {
      setSearch(searchParams.get("search") ?? "");
      return;
    }

    setSearch("");
  }, [pathname, searchParams]);

  useEffect(() => {
    function handlePointer(event: MouseEvent) {
      if (!themeMenuRef.current?.contains(event.target as Node)) {
        setThemeMenuOpen(false);
      }
    }

    document.addEventListener("mousedown", handlePointer);
    return () => document.removeEventListener("mousedown", handlePointer);
  }, []);

  function submitSearch() {
    const nextQuery = search.trim();
    router.push(nextQuery ? `/products?search=${encodeURIComponent(nextQuery)}` : "/products");
  }

  return (
    <header className="sticky top-0 z-50 border-b border-line bg-surface/90 backdrop-blur-md">
      <div
        className={cn(
          "mx-auto max-w-[1280px] px-4 sm:px-6 lg:px-8",
          isScrolled && "border-line"
        )}
      >
        <div className="flex min-h-[82px] items-center justify-between gap-6">
          <Link
            href="/"
            className="flex shrink-0 items-center gap-3"
          >
            <div className="grid h-11 w-11 place-items-center rounded-2xl bg-[linear-gradient(135deg,#F97316_0%,#FB7185_46%,#2563EB_100%)] text-lg font-bold text-white shadow-[0_12px_30px_rgba(37,99,235,0.22)]">
              e
            </div>
            <div>
              <p className="text-2xl font-bold tracking-[-0.05em] text-text">e-shope</p>
              <Text size="sm" className="hidden sm:block">
                marketplace
              </Text>
            </div>
          </Link>

          <nav className="hidden flex-1 items-center justify-center gap-8 lg:flex">
            {navItems.map((item) => (
              <Link
                key={item.href}
                href={item.href}
                className={cn(
                  "group relative text-sm font-medium transition-colors hover:text-[#2F80ED]",
                  pathname === item.href || pathname.startsWith(`${item.href}/`)
                    ? "text-[#2F80ED]"
                    : "text-text"
                )}
              >
                {item.label}
                <span
                  className={cn(
                    "absolute inset-x-0 -bottom-1 h-0.5 origin-left rounded-full bg-[#2F80ED] transition-transform duration-200 group-hover:scale-x-100",
                    pathname === item.href || pathname.startsWith(`${item.href}/`)
                      ? "scale-x-100"
                      : "scale-x-0"
                  )}
                />
              </Link>
            ))}
          </nav>

          <div className="hidden items-center gap-3 xl:flex">
            <form
              className="flex h-12 w-[320px] items-center gap-3 rounded-2xl border border-line bg-page/70 px-4"
              onSubmit={(event) => {
                event.preventDefault();
                submitSearch();
              }}
            >
              <SearchIcon />
              <input
                value={search}
                onChange={(event) => setSearch(event.target.value)}
                className="h-full flex-1 bg-transparent text-sm text-text outline-none placeholder:text-muted"
                placeholder="Search for product "
              />
            </form>

            <div className="relative" ref={themeMenuRef}>
              <button
                type="button"
                onClick={() => setThemeMenuOpen((current) => !current)}
                className="flex h-12 items-center gap-2 rounded-2xl border border-line bg-surface px-4 text-sm font-medium text-text transition hover:bg-page"
              >
                <ThemeIcon mode={resolvedTheme} />
                <span className="capitalize">{theme}</span>
              </button>

              {themeMenuOpen ? (
                <div className="absolute right-0 top-[calc(100%+10px)] w-40 rounded-2xl border border-line bg-surface p-2 shadow-[0_16px_36px_rgba(15,23,42,0.12)]">
                  {(["light", "dark", "system"] as const).map((option) => (
                    <button
                      key={option}
                      type="button"
                      onClick={() => {
                        setTheme(option);
                        setThemeMenuOpen(false);
                      }}
                      className={cn(
                        "flex w-full items-center justify-between rounded-xl px-3 py-2.5 text-left text-sm text-text transition hover:bg-page",
                        theme === option && "bg-page"
                      )}
                    >
                      <span className="capitalize">{option}</span>
                      {theme === option ? <span className="text-accent">•</span> : null}
                    </button>
                  ))}
                </div>
              ) : null}
            </div>
          </div>

          <div className="flex items-center gap-3">
            {isAuthenticated ? (
              <>
                <span className="hidden text-sm font-medium text-text sm:inline">
                  {session?.username}
                </span>
                <Button variant="outline" onClick={() => void clearSession()}>
                  Log Out
                </Button>
              </>
            ) : (
              <>
                <Link
                  href="/login"
                  className="inline-flex h-11 items-center justify-center rounded-[14px] border border-line px-4 text-sm font-medium text-text transition hover:bg-page"
                >
                  Log In
                </Link>
                <Link
                  href="/register"
                  className="inline-flex h-11 items-center justify-center rounded-[14px] bg-[#2563EB] px-4 text-sm font-medium text-white transition hover:bg-[#1D4ED8]"
                >
                  Sign up
                </Link>
              </>
            )}
          </div>
        </div>

        <div className="scrollbar-none flex gap-5 overflow-x-auto pb-4 lg:hidden">
          {navItems.map((item) => (
            <Link
              key={item.href}
              href={item.href}
              className={cn(
                "whitespace-nowrap text-sm font-medium transition hover:text-[#2F80ED]",
                pathname === item.href || pathname.startsWith(`${item.href}/`)
                  ? "text-[#2F80ED]"
                  : "text-muted"
              )}
            >
              {item.label}
            </Link>
          ))}
        </div>

        <div className="grid gap-3 pb-4 xl:hidden">
          <form
            className="flex h-12 items-center gap-3 rounded-2xl border border-line bg-page/70 px-4"
            onSubmit={(event) => {
              event.preventDefault();
              submitSearch();
            }}
          >
            <SearchIcon />
            <input
              value={search}
              onChange={(event) => setSearch(event.target.value)}
              className="h-full flex-1 bg-transparent text-sm text-text outline-none placeholder:text-muted"
              placeholder="Search for product "
            />
          </form>
          <div className="flex gap-3">
            {(["light", "dark", "system"] as const).map((option) => (
              <button
                key={option}
                type="button"
                onClick={() => setTheme(option)}
                className={cn(
                  "rounded-xl border border-line px-3 py-2 text-sm text-text transition",
                  theme === option ? "bg-page" : "bg-surface"
                )}
              >
                {option}
              </button>
            ))}
          </div>
        </div>
      </div>
    </header>
  );
}

function SearchIcon() {
  return (
    <svg viewBox="0 0 24 24" className="h-4 w-4 text-muted" fill="none" stroke="currentColor" strokeWidth="2">
      <circle cx="11" cy="11" r="6.5" />
      <path d="M16 16L21 21" strokeLinecap="round" />
    </svg>
  );
}

function ThemeIcon({ mode }: { mode: "light" | "dark" }) {
  if (mode === "dark") {
    return (
      <svg viewBox="0 0 24 24" className="h-4 w-4 text-text" fill="none" stroke="currentColor" strokeWidth="2">
        <path d="M21 12.8A9 9 0 1111.2 3a7 7 0 009.8 9.8z" />
      </svg>
    );
  }

  return (
    <svg viewBox="0 0 24 24" className="h-4 w-4 text-text" fill="none" stroke="currentColor" strokeWidth="2">
      <circle cx="12" cy="12" r="4.5" />
      <path d="M12 2v2.5M12 19.5V22M4.93 4.93l1.77 1.77M17.3 17.3l1.77 1.77M2 12h2.5M19.5 12H22M4.93 19.07l1.77-1.77M17.3 6.7l1.77-1.77" strokeLinecap="round" />
    </svg>
  );
}
