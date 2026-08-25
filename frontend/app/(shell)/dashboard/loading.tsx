import { Container } from "@/components/ui/container";
import { Skeleton } from "@/components/ui/skeleton";

export default function DashboardLoading() {
  return (
    <Container className="section-space grid gap-6">
      <Skeleton className="h-32 rounded-[2rem]" />
      <div className="grid gap-6 lg:grid-cols-[1.2fr_0.8fr]">
        <Skeleton className="h-[28rem] rounded-[2rem]" />
        <Skeleton className="h-[28rem] rounded-[2rem]" />
      </div>
    </Container>
  );
}
