import { MarketplaceHero } from "@/features/landing/components/marketplace-hero";
import { MarketplaceFooter } from "@/features/landing/components/marketplace-footer";
import { MarketplaceNavbar } from "@/features/landing/components/marketplace-navbar";
import { FeatureStrip } from "@/features/landing/components/feature-strip";
import { RecentlyListed } from "@/features/landing/components/recently-listed";

export default function LandingPage() {
  return (
    <div className="min-h-screen bg-page text-text transition-colors">
      <MarketplaceNavbar />
      <main className="pb-16">
        <MarketplaceHero />
        <RecentlyListed />
        <FeatureStrip />
      </main>
      <MarketplaceFooter />
    </div>
  );
}
