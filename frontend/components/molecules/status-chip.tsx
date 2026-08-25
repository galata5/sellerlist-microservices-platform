import { cn } from "@/lib/cn";

type StatusChipProps = {
  label: string;
  tone?: "neutral" | "positive";
};

const toneClass: Record<NonNullable<StatusChipProps["tone"]>, string> = {
  neutral: "border-line text-muted",
  positive: "border-accent/50 text-accent"
};

export function StatusChip({ label, tone = "neutral" }: StatusChipProps) {
  return (
    <span
      className={cn(
        "rounded-full border px-4 py-2 text-[0.65rem] uppercase tracking-[0.26em]",
        toneClass[tone]
      )}
    >
      {label}
    </span>
  );
}
