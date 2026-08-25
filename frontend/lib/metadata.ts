import type { Metadata } from "next";

export const siteConfig = {
  name: "e-shope",
  description:
    "A trusted marketplace for discovering products, placing orders, and selling items through a unified service-backed platform.",
  url: "https://commerce.local",
  keywords: [
    "marketplace",
    "products",
    "orders",
    "seller platform",
    "next.js",
    "spring boot"
  ]
};

export const rootMetadata: Metadata = {
  metadataBase: new URL(siteConfig.url),
  title: {
    default: siteConfig.name,
    template: `%s | ${siteConfig.name}`
  },
  description: siteConfig.description,
  keywords: siteConfig.keywords,
  openGraph: {
    title: siteConfig.name,
    description: siteConfig.description,
    type: "website",
    siteName: siteConfig.name
  },
  twitter: {
    card: "summary_large_image",
    title: siteConfig.name,
    description: siteConfig.description
  }
};
