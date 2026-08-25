import { requestOptional, unwrapCollection } from "@/lib/api/client";
import type {
  CollectionResponse,
  Order,
  Payment,
  Product,
  ServiceCollection,
  User
} from "@/lib/api/types";

type DashboardSnapshot = {
  products: ServiceCollection<Product>;
  orders: ServiceCollection<Order>;
  payments: ServiceCollection<Payment>;
  users: ServiceCollection<User>;
};

export async function getDashboardSnapshot() {
  const [productsResponse, ordersResponse, paymentsResponse, usersResponse] =
    await Promise.all([
      requestOptional<CollectionResponse<Product>>("/products"),
      requestOptional<CollectionResponse<Order>>("/orders"),
      requestOptional<CollectionResponse<Payment>>("/payments"),
      requestOptional<CollectionResponse<User>>("/users")
    ]);

  return {
    products: {
      items: productsResponse.data ? unwrapCollection(productsResponse.data) : [],
      sourceAvailable: Boolean(productsResponse.data),
      message: productsResponse.message,
      status: productsResponse.status
    },
    orders: {
      items: ordersResponse.data ? unwrapCollection(ordersResponse.data) : [],
      sourceAvailable: Boolean(ordersResponse.data),
      message: ordersResponse.message,
      status: ordersResponse.status
    },
    payments: {
      items: paymentsResponse.data ? unwrapCollection(paymentsResponse.data) : [],
      sourceAvailable: Boolean(paymentsResponse.data),
      message: paymentsResponse.message,
      status: paymentsResponse.status
    },
    users: {
      items: usersResponse.data ? unwrapCollection(usersResponse.data) : [],
      sourceAvailable: Boolean(usersResponse.data),
      message: usersResponse.message,
      status: usersResponse.status
    }
  } satisfies DashboardSnapshot;
}
