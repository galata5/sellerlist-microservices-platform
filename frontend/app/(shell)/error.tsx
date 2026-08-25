"use client";

import { Button } from "@/components/atoms/button";
import { Text } from "@/components/atoms/text";
import { Container } from "@/components/ui/container";
import { sanitizeApiErrorMessage } from "@/lib/api/client";

export default function ShellError({
  error,
  reset
}: {
  error: Error;
  reset: () => void;
}) {
  return (
    <Container className="section-space">
      <div className="surface-panel flex flex-col gap-6 p-10">
        <span className="eyebrow">Page unavailable</span>
        <h1 className="headline">This section could not be loaded.</h1>
        <Text>{sanitizeApiErrorMessage(error.message, "This section could not be loaded.")}</Text>
        <div>
          <Button onClick={reset}>Try again</Button>
        </div>
      </div>
    </Container>
  );
}
