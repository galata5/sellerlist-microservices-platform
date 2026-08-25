import { cn } from "@/lib/cn";

type TextProps = React.HTMLAttributes<HTMLParagraphElement> & {
  tone?: "primary" | "muted";
  size?: "sm" | "base" | "lg";
};

const sizeClasses: Record<NonNullable<TextProps["size"]>, string> = {
  sm: "text-sm leading-6",
  base: "text-base leading-7",
  lg: "text-lg leading-7"
};

const toneClasses: Record<NonNullable<TextProps["tone"]>, string> = {
  primary: "text-text",
  muted: "text-muted"
};

export function Text({
  className,
  tone = "muted",
  size = "base",
  ...props
}: TextProps) {
  return (
    <p className={cn(sizeClasses[size], toneClasses[tone], className)} {...props} />
  );
}
