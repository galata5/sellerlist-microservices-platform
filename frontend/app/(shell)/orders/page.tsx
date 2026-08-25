import { Container } from "@/components/ui/container";
import { OrdersOverview } from "@/features/orders/orders-overview";

export default function OrdersPage() {
  return (
    <Container className="section-space">
      <OrdersOverview />
    </Container>
  );
}
