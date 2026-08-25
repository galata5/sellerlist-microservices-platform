import Link from "next/link";

const footerLinks = [
  { label: "Products", href: "/products" },
  { label: "Orders", href: "/orders" },
  { label: "Checkout", href: "/checkout" },
  { label: "Dashboard", href: "/dashboard" },
  { label: "Sell item", href: "/sell" }
];

export function MarketplaceFooter() {
  return (
    <footer className="border-t border-line bg-surface transition-colors">
      <div className="mx-auto grid max-w-[1280px] gap-8 px-4 py-10 sm:grid-cols-[1.2fr_0.8fr] sm:px-6 lg:px-8">
        <div className="space-y-3">
          <p className="text-sm font-semibold uppercase tracking-[0.24em] text-muted">
            e-shope
          </p>
          <p className="text-xl font-semibold tracking-[-0.02em] text-text">
            A simple marketplace for browsing, ordering, and tracking purchases.
          </p>
          <p className="max-w-2xl text-sm text-muted">
            Everything is kept in one place so it is easy to search products, place an order,
            and come back later to check its progress.
          </p>
        </div>
        <div className="grid gap-3 text-sm sm:justify-self-end">
          {footerLinks.map((link) => (
            <Link
              key={link.href}
              href={link.href}
              className="font-medium text-text transition hover:text-accent"
            >
              {link.label}
            </Link>
          ))}
        </div>
      </div>
    </footer>
  );
}
