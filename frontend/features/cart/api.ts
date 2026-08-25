import { request } from "@/lib/api/client";
import type { Cart, CartMutationItem } from "@/lib/api/types";

type ReplaceCartPayload = {
  items: CartMutationItem[];
};

export async function fetchCurrentCart() {
  return request<Cart>("/carts/me");
}

export async function replaceCurrentCart(payload: ReplaceCartPayload) {
  return request<Cart>("/carts/me", {
    method: "PUT",
    body: payload
  });
}

export async function clearCurrentCart() {
  return request<void>("/carts/me", {
    method: "DELETE"
  });
}
