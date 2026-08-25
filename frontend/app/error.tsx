"use client";

import { Button } from "@/components/atoms/button";
import { Text } from "@/components/atoms/text";
import { Container } from "@/components/ui/container";
import { sanitizeApiErrorMessage } from "@/lib/api/client";

export default function GlobalError({
  error,
  reset
}: {
  error: Error;
  reset: () => void;
}) {
  return (
    <Container className="section-space">
      <div className="surface-panel mx-auto flex max-w-2xl flex-col gap-6 p-10">
        <span className="eyebrow">Something went wrong</span>
        <h1 className="headline">The page could not finish loading.</h1>
        <Text>{sanitizeApiErrorMessage(error.message, "The page could not finish loading.")}</Text>
        <div>
          <Button onClick={reset}>Try again</Button>
        </div>
      </div>
    </Container>
  );
}
