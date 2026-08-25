import { motion } from "framer-motion";

import { Button } from "@/components/atoms/button";
import { Text } from "@/components/atoms/text";

type EmptyStateProps = {
  title: string;
  description: string;
  actionLabel?: string;
  onAction?: () => void;
};

export function EmptyState({
  title,
  description,
  actionLabel,
  onAction
}: EmptyStateProps) {
  return (
    <motion.div
      className="surface-panel flex flex-col items-start gap-4 p-6 sm:p-8"
      initial={{ opacity: 0, y: 10 }}
      animate={{ opacity: 1, y: 0 }}
      transition={{ duration: 0.25, ease: "easeOut" }}
    >
      <div>
        <p className="text-xl font-semibold text-text">{title}</p>
        <Text className="mt-2 max-w-2xl" size="sm">
          {description}
        </Text>
      </div>
      {actionLabel && onAction ? (
        <Button variant="outline" onClick={onAction}>
          {actionLabel}
        </Button>
      ) : null}
    </motion.div>
  );
}
