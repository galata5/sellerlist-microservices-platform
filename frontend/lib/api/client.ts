import { getPublicRuntimeEnv } from "@/lib/env";

export class ApiError extends Error {
  status: number;
  payload: unknown;

  constructor(message: string, status: number, payload: unknown) {
    super(message);
    this.name = "ApiError";
    this.status = status;
    this.payload = payload;
  }
}

export function sanitizeApiErrorMessage(input: unknown, fallback = "Unexpected API failure.") {
  if (typeof input !== "string") {
    return fallback;
  }

  const trimmed = input.trim();
  if (!trimmed) {
    return fallback;
  }

  const looksLikeHtml =
    trimmed.startsWith("<!DOCTYPE") ||
    trimmed.startsWith("<html") ||
    trimmed.includes("<body") ||
    trimmed.includes("</html>");

  if (looksLikeHtml) {
    return "The API returned an HTML error page instead of JSON. Check the frontend-to-gateway routing.";
  }

  return trimmed.replace(/\s+/g, " ");
}

type RequestOptions = Omit<RequestInit, "body"> & {
  body?: unknown;
};

export function makeUrl(path: string) {
  const baseUrl = getPublicRuntimeEnv().NEXT_PUBLIC_API_URL;
  const normalizedPath = path.startsWith("/") ? path : `/${path}`;
  const safePath =
    baseUrl.endsWith("/api") && normalizedPath.startsWith("/api/")
      ? normalizedPath.slice(4)
      : normalizedPath;
  return `${baseUrl}${safePath}`;
}

export async function request<T>(path: string, options: RequestOptions = {}): Promise<T> {
  const { body, headers, credentials = "include", ...rest } = options;

  const response = await fetch(makeUrl(path), {
    ...rest,
    headers: {
      "Content-Type": "application/json",
      ...(headers ?? {})
    },
    body: body === undefined ? undefined : JSON.stringify(body),
    cache: "no-store",
    credentials
  });

  const contentType = response.headers.get("content-type") ?? "";
  const payload = contentType.includes("application/json")
    ? await response.json()
    : await response.text();

  if (!response.ok) {
    const extractedMessage =
      typeof payload === "string"
        ? sanitizeApiErrorMessage(payload)
        : sanitizeApiErrorMessage(
            (payload as { message?: string; msg?: string; error?: string })?.message ??
              (payload as { message?: string; msg?: string; error?: string })?.msg ??
              (payload as { message?: string; msg?: string; error?: string })?.error
          );
    throw new ApiError(extractedMessage, response.status, payload);
  }

  return payload as T;
}

export function unwrapCollection<T>(input: { collection?: T[] } | T[]) {
  return Array.isArray(input) ? input : input.collection ?? [];
}

export async function requestOptional<T>(
  path: string,
  options: RequestOptions = {}
): Promise<{ data: T | null; message?: string; status?: number }> {
  try {
    return {
      data: await request<T>(path, options)
    };
  } catch (error) {
    return {
      data: null,
      message:
        error instanceof Error
          ? sanitizeApiErrorMessage(error.message, "The service did not respond.")
          : "The service did not respond.",
      status: error instanceof ApiError ? error.status : undefined
    };
  }
}
