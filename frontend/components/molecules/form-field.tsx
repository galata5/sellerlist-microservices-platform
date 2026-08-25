import { Input } from "@/components/atoms/input";
import { cn } from "@/lib/cn";

type FormFieldProps = React.InputHTMLAttributes<HTMLInputElement> & {
  label: string;
  hint?: string;
};

export function FormField({ label, hint, className, ...props }: FormFieldProps) {
  return (
    <label className="flex flex-col gap-2 text-sm text-muted">
      <span className="text-xs uppercase tracking-[0.22em] text-muted">
        {label}
      </span>
      <Input className={cn("text-text", className)} {...props} />
      {hint ? <span className="text-xs text-muted/80">{hint}</span> : null}
    </label>
  );
}
