import { Container } from "@/components/ui/container";
import { DashboardOverview } from "@/features/dashboard/dashboard-overview";

export default function DashboardPage() {
  return (
    <Container className="section-space">
      <DashboardOverview />
    </Container>
  );
}
