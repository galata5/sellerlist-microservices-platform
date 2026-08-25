import Link from "next/link";

import { Button } from "@/components/atoms/button";
import { Text } from "@/components/atoms/text";
import { Container } from "@/components/ui/container";

export default function NotFound() {
  return (
    <Container className="section-space">
      <div className="surface-panel mx-auto flex max-w-2xl flex-col gap-6 p-10">
        <span className="eyebrow">404</span>
        <h1 className="headline">This page could not be found.</h1>
        <Text>
          The link may be out of date, or the page may have moved.
        </Text>
        <div className="flex gap-4">
          <Link href="/">
            <Button>Go home</Button>
          </Link>
          <Link href="/products">
            <Button variant="outline">Browse products</Button>
          </Link>
        </div>
      </div>
    </Container>
  );
}
