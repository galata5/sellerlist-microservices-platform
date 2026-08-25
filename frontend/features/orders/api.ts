import { request, requestOptional, unwrapCollection } from "@/lib/api/client";
import type {
  CollectionResponse,
  Order,
  Payment,
  ServiceCollection
} from "@/lib/api/types";

export async function getOrders() {
  const response = await request<CollectionResponse<Order>>("/orders");

  return unwrapCollection(response);
}

export async function getPayments() {
  const response = await request<CollectionResponse<Payment>>("/payments");

  return unwrapCollection(response);
}

export async function getOrderWorkspace(): Promise<{
  orders: ServiceCollection<Order>;
  payments: ServiceCollection<Payment>;
}> {
  const [ordersResponse, paymentsResponse] = await Promise.all([
    requestOptional<CollectionResponse<Order>>("/orders"),
    requestOptional<CollectionResponse<Payment>>("/payments")
  ]);

  return {
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
    }
  };
}
