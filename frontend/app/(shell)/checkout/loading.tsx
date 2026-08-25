import { Container } from "@/components/ui/container";
import { Skeleton } from "@/components/ui/skeleton";

export default function CheckoutLoading() {
  return (
    <Container className="section-space grid gap-6 lg:grid-cols-[1.1fr_0.9fr]">
      <Skeleton className="h-[32rem] rounded-[2rem]" />
      <Skeleton className="h-[32rem] rounded-[2rem]" />
    </Container>
  );
}
