import { Container } from "@/components/ui/container";
import { SkeletonBlock } from "@/components/molecules/skeleton-block";

export default function ShellLoading() {
  return (
    <Container className="section-space grid gap-6">
      <SkeletonBlock className="h-24" />
      <div className="grid gap-6 lg:grid-cols-[1.3fr_0.7fr]">
        <SkeletonBlock className="h-[28rem]" />
        <div className="grid gap-6">
          <SkeletonBlock className="h-40" />
          <SkeletonBlock className="h-40" />
        </div>
      </div>
    </Container>
  );
}
