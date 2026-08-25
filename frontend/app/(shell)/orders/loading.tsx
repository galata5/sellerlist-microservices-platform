import { Container } from "@/components/ui/container";
import { Skeleton } from "@/components/ui/skeleton";

export default function OrdersLoading() {
  return (
    <Container className="section-space grid gap-4">
      {Array.from({ length: 4 }).map((_, index) => (
        <Skeleton key={index} className="h-36 rounded-[2rem]" />
      ))}
    </Container>
  );
}
