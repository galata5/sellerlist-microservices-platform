"use client";

import { useQuery } from "@tanstack/react-query";

import { getProduct, getProducts } from "@/features/products/api";

export function useProductsQuery() {
  return useQuery({
    queryKey: ["products"],
    queryFn: getProducts
  });
}

export function useProductQuery(productId: string) {
  return useQuery({
    queryKey: ["product", productId],
    queryFn: () => getProduct(productId),
    enabled: Boolean(productId)
  });
}
