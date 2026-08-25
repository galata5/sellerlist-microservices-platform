import { Container } from "@/components/ui/container";
import { SkeletonBlock } from "@/components/molecules/skeleton-block";

export default function GlobalLoading() {
  return (
    <Container className="section-space grid gap-8">
      <SkeletonBlock className="h-16 w-56 rounded-full" />
      <SkeletonBlock className="h-[40vh]" />
      <div className="grid gap-6 lg:grid-cols-3">
        <SkeletonBlock className="h-48" />
        <SkeletonBlock className="h-48" />
        <SkeletonBlock className="h-48" />
      </div>
    </Container>
  );
}
