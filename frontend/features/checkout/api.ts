import { makeUrl, request } from "@/lib/api/client";
import type { CheckoutInput, Order, Payment } from "@/lib/api/types";

export async function placeOrder(payload: CheckoutInput) {
  const order = await request<Order>("/orders/checkout", {
    method: "POST",
    body: {
      fullName: payload.fullName.trim(),
      phoneNumber: payload.phoneNumber.trim(),
      city: payload.city.trim(),
      streetAddress: payload.streetAddress.trim(),
      postalCode: payload.postalCode.trim(),
      email: payload.email?.trim() || undefined,
      paymentMethod: payload.paymentMethod,
      notes: payload.notes.trim()
    }
  });

  const payment = await waitForPayment(order.orderId);

  return { order, payment };
}

async function waitForPayment(orderId: number) {
  try {
    return await waitForPaymentStream(orderId);
  } catch {
    return pollForPayment(orderId);
  }
}

async function waitForPaymentStream(orderId: number) {
  return new Promise<Payment>((resolve, reject) => {
    const stream = new EventSource(makeUrl(`/payments/stream/order/${orderId}`), {
      withCredentials: true
    });
    const timeoutId = window.setTimeout(() => {
      stream.close();
      reject(new Error("timeout"));
    }, 12000);

    const cleanup = () => {
      window.clearTimeout(timeoutId);
      stream.close();
    };

    stream.addEventListener("payment-status", (event) => {
      const payment = JSON.parse((event as MessageEvent<string>).data) as Payment;
      cleanup();
      resolve(payment);
    });

    stream.onerror = () => {
      cleanup();
      reject(new Error("stream"));
    };
  });
}

async function pollForPayment(orderId: number) {
  const startedAt = Date.now();

  while (Date.now() - startedAt < 20000) {
    try {
      return await request<Payment>(`/payments/order/${orderId}`);
    } catch (error) {
      const status =
        typeof error === "object" && error !== null && "status" in error
          ? Number((error as { status?: number }).status)
          : undefined;
      if (status !== 404) {
        throw error;
      }
    }

    await new Promise((resolve) => window.setTimeout(resolve, 1500));
  }

  throw new Error("Order was accepted, but payment provisioning is taking longer than expected.");
}
