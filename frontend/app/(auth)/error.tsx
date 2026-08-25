"use client";

import { Button } from "@/components/atoms/button";
import { Text } from "@/components/atoms/text";
import { Container } from "@/components/ui/container";
import { sanitizeApiErrorMessage } from "@/lib/api/client";

export default function AuthError({
  error,
  reset
}: {
  error: Error;
  reset: () => void;
}) {
  return (
    <Container className="section-space">
      <div className="surface-panel mx-auto flex max-w-2xl flex-col gap-6 p-10">
        <span className="eyebrow">Authentication interrupted</span>
        <h1 className="headline">The auth flow couldn&apos;t finish loading.</h1>
        <Text>{sanitizeApiErrorMessage(error.message, "The auth flow could not finish loading.")}</Text>
        <div>
          <Button onClick={reset}>Retry</Button>
        </div>
      </div>
    </Container>
  );
}
