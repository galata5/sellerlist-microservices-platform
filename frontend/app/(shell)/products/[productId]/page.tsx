import { Container } from "@/components/ui/container";
import { ProductDetailHero } from "@/features/products/components/product-detail-hero";

export default function ProductDetailPage({
  params
}: {
  params: { productId: string };
}) {
  return (
    <Container className="section-space">
      <ProductDetailHero productId={params.productId} />
    </Container>
  );
}
