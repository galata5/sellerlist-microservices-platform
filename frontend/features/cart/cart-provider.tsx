"use client";

import {
  createContext,
  type PropsWithChildren,
  useContext,
  useEffect,
  useMemo,
  useState
} from "react";

import { useToast } from "@/components/ui/toast";
import { useAuthSession } from "@/features/auth/auth-provider";
import {
  clearCurrentCart as clearCurrentCartRequest,
  fetchCurrentCart,
  replaceCurrentCart
} from "@/features/cart/api";
import { sanitizeApiErrorMessage } from "@/lib/api/client";
import type {
  CartItem as CartSnapshot,
  CartMutationItem,
  Product
} from "@/lib/api/types";

export type CartItem = {
  product: Product;
  quantity: number;
};

type CartContextValue = {
  items: CartItem[];
  itemCount: number;
  subtotal: number;
  addItem: (product: Product) => Promise<boolean>;
  updateQuantity: (productId: number, quantity: number) => Promise<boolean>;
  removeItem: (productId: number) => Promise<boolean>;
  clearCart: () => Promise<boolean>;
};

const CartContext = createContext<CartContextValue | null>(null);

function mapSnapshotToCartItem(snapshot: CartSnapshot): CartItem {
  return {
    quantity: snapshot.quantity,
    product: {
      productId: snapshot.productId,
      productTitle: snapshot.productTitle,
      sku: snapshot.sku ?? null,
      imageUrl: snapshot.imageUrl ?? null,
      priceUnit: snapshot.priceUnit,
      quantity: snapshot.quantity,
      category: snapshot.categoryTitle
        ? {
            categoryId: snapshot.categoryId ?? 0,
            categoryTitle: snapshot.categoryTitle
          }
        : null
    }
  };
}

function mapCartItemToMutation(item: CartItem): CartMutationItem {
  return {
    productId: item.product.productId,
    quantity: item.quantity
  };
}

export function CartProvider({ children }: PropsWithChildren) {
  const { isAuthenticated, session } = useAuthSession();
  const { notify } = useToast();
  const [items, setItems] = useState<CartItem[]>([]);

  useEffect(() => {
    if (!isAuthenticated || !session?.userId) {
      setItems([]);
      return;
    }

    let active = true;
    void fetchCurrentCart()
      .then((cart) => {
        if (!active) {
          return;
        }
        setItems((cart.items ?? []).map(mapSnapshotToCartItem));
      })
      .catch(() => {
        if (active) {
          notify({
            title: "Cart unavailable",
            description: "We couldn't load your saved cart from the server."
          });
        }
      });

    return () => {
      active = false;
    };
  }, [isAuthenticated, session?.userId, notify]);

  const value = useMemo<CartContextValue>(() => {
    const subtotal = items.reduce(
      (total, item) => total + item.product.priceUnit * item.quantity,
      0
    );
    const itemCount = items.reduce((total, item) => total + item.quantity, 0);

    async function requireAuthenticatedSession() {
      if (isAuthenticated && session?.userId) {
        return true;
      }

      notify({
        title: "Sign in required",
        description: "Please sign in before adding items to your cart."
      });
      return false;
    }

    async function persist(nextItems: CartItem[], previousItems: CartItem[]) {
      try {
        const savedCart = await replaceCurrentCart({
          items: nextItems.map(mapCartItemToMutation)
        });
        setItems((savedCart.items ?? []).map(mapSnapshotToCartItem));
        return true;
      } catch (error) {
        setItems(previousItems);
        notify({
          title: "Cart update failed",
          description:
            error instanceof Error
              ? sanitizeApiErrorMessage(error.message, "The cart could not be updated.")
              : "The cart could not be updated."
        });
        return false;
      }
    }

    return {
      items,
      itemCount,
      subtotal,
      async addItem(product) {
        if (!(await requireAuthenticatedSession())) {
          return false;
        }

        const previousItems = items;
        const existing = items.find((item) => item.product.productId === product.productId);
        const nextItems = existing
          ? items.map((item) =>
              item.product.productId === product.productId
                ? { ...item, quantity: item.quantity + 1 }
                : item
            )
          : [...items, { product, quantity: 1 }];

        setItems(nextItems);
        return persist(nextItems, previousItems);
      },
      async updateQuantity(productId, quantity) {
        if (!(await requireAuthenticatedSession())) {
          return false;
        }

        const previousItems = items;
        const nextItems =
          quantity <= 0
            ? items.filter((item) => item.product.productId !== productId)
            : items.map((item) =>
                item.product.productId === productId
                  ? { ...item, quantity }
                  : item
              );

        setItems(nextItems);
        return persist(nextItems, previousItems);
      },
      async removeItem(productId) {
        if (!(await requireAuthenticatedSession())) {
          return false;
        }

        const previousItems = items;
        const nextItems = items.filter((item) => item.product.productId !== productId);
        setItems(nextItems);
        return persist(nextItems, previousItems);
      },
      async clearCart() {
        if (!isAuthenticated || !session?.userId) {
          setItems([]);
          return true;
        }

        const previousItems = items;
        setItems([]);
        try {
          await clearCurrentCartRequest();
          return true;
        } catch (error) {
        setItems(previousItems);
        notify({
          title: "Cart clear failed",
          description:
            error instanceof Error
              ? sanitizeApiErrorMessage(error.message, "The cart could not be cleared.")
              : "The cart could not be cleared."
        });
        return false;
      }
      }
    };
  }, [isAuthenticated, items, notify, session?.userId]);

  return <CartContext.Provider value={value}>{children}</CartContext.Provider>;
}

export function useCart() {
  const context = useContext(CartContext);

  if (!context) {
    throw new Error("useCart must be used within CartProvider.");
  }

  return context;
}
