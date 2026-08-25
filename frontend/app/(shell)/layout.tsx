import { MarketplaceFooter } from "@/features/landing/components/marketplace-footer";
import { MarketplaceNavbar } from "@/features/landing/components/marketplace-navbar";

export default function ShellLayout({
  children
}: {
  children: React.ReactNode;
}) {
  return (
    <div className="min-h-screen bg-page text-text transition-colors">
      <MarketplaceNavbar />
      <main>{children}</main>
      <MarketplaceFooter />
    </div>
  );
}
