import { Container } from "@/components/ui/container";
import { CreateListingPanel } from "@/features/products/components/create-listing-panel";

export default function SellPage() {
  return (
    <Container className="section-space">
      <CreateListingPanel />
    </Container>
  );
}
