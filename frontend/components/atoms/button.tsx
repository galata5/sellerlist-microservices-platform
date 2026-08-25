"use client";

import { forwardRef } from "react";
import { motion, type HTMLMotionProps } from "framer-motion";

import { cn } from "@/lib/cn";

type ButtonProps = Omit<HTMLMotionProps<"button">, "ref"> & {
  variant?: "primary" | "outline" | "ghost";
};

const variants: Record<NonNullable<ButtonProps["variant"]>, string> = {
  primary:
    "bg-accent text-white hover:brightness-95 focus-visible:ring-2 focus-visible:ring-accent/20",
  outline:
    "border border-line bg-surface text-text hover:bg-page focus-visible:ring-2 focus-visible:ring-accent/15",
  ghost:
    "bg-transparent text-muted hover:bg-page hover:text-text focus-visible:ring-2 focus-visible:ring-accent/15"
};

export const Button = forwardRef<HTMLButtonElement, ButtonProps>(
  ({ className, variant = "primary", disabled, ...props }, ref) => {
    return (
      <motion.button
        ref={ref}
        className={cn(
          "inline-flex items-center justify-center rounded-[10px] px-4 py-2.5 text-sm font-medium transition-colors duration-200",
          "disabled:pointer-events-none disabled:opacity-50",
          variants[variant],
          className
        )}
        whileHover={disabled ? undefined : { y: -1 }}
        whileTap={disabled ? undefined : { scale: 0.99 }}
        transition={{ duration: 0.16, ease: "easeOut" }}
        {...props}
      />
    );
  }
);

Button.displayName = "Button";
