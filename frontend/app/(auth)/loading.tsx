import { Container } from "@/components/ui/container";
import { SkeletonBlock } from "@/components/molecules/skeleton-block";

export default function AuthLoading() {
  return (
    <Container className="section-space grid gap-6 lg:grid-cols-[0.95fr_1.05fr]">
      <SkeletonBlock className="h-72" />
      <SkeletonBlock className="h-[28rem]" />
    </Container>
  );
}
