import { Container } from "@/components/ui/container";
import { ProductGrid } from "@/features/products/components/product-grid";

export default function ProductsPage() {
  return (
    <Container className="section-space">
      <ProductGrid />
    </Container>
  );
}
