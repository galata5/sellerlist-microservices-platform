import { forwardRef } from "react";

import { cn } from "@/lib/cn";

type InputProps = React.InputHTMLAttributes<HTMLInputElement>;

export const Input = forwardRef<HTMLInputElement, InputProps>(
  ({ className, ...props }, ref) => {
    return (
      <input
        ref={ref}
        className={cn(
          "h-12 w-full rounded-2xl border border-line bg-surface px-4 text-sm text-text placeholder:text-muted/70",
          "transition focus:border-accent/60 focus:outline-none focus:ring-2 focus:ring-accent/20",
          className
        )}
        {...props}
      />
    );
  }
);

Input.displayName = "Input";
