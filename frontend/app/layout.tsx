import type { Metadata } from "next";

import { AppProviders } from "@/components/providers/app-providers";
import { RuntimeEnvScript } from "@/components/providers/runtime-env-script";
import { rootMetadata } from "@/lib/metadata";

import "./globals.css";

export const metadata: Metadata = rootMetadata;

export default function RootLayout({
  children
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <html lang="en" suppressHydrationWarning>
      <body className="bg-page text-text antialiased">
        <RuntimeEnvScript />
        <AppProviders>{children}</AppProviders>
      </body>
    </html>
  );
}
