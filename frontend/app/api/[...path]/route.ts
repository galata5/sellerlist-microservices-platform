import { NextRequest } from "next/server";

const METHODS_WITH_BODY = new Set(["POST", "PUT", "PATCH", "DELETE"]);

function getGatewayBaseUrl() {
  return (process.env.API_GATEWAY_URL ?? "http://api-gateway:8080").replace(/\/$/, "");
}

function buildUpstreamUrl(path: string[], search: string) {
  const normalizedPath = path.length > 0 ? `/${path.join("/")}` : "";
  return `${getGatewayBaseUrl()}/api${normalizedPath}${search}`;
}

function isPublicProxyRequest(request: NextRequest, path: string[]) {
  const normalizedPath = `/${path.join("/")}`;

  if (request.method === "OPTIONS") {
    return true;
  }

  if (normalizedPath === "/authenticate" || normalizedPath.startsWith("/authenticate/")) {
    return true;
  }

  if (request.method === "POST" && normalizedPath === "/users/register") {
    return true;
  }

  if (
    request.method === "GET" &&
    (normalizedPath === "/products" ||
      normalizedPath.startsWith("/products/") ||
      normalizedPath === "/categories" ||
      normalizedPath.startsWith("/categories/"))
  ) {
    return true;
  }

  return false;
}

function copyHeaders(request: NextRequest, path: string[]) {
  const isPublicRequest = isPublicProxyRequest(request, path);
  const headers = isPublicRequest ? new Headers() : new Headers(request.headers);

  if (isPublicRequest) {
    const contentType = request.headers.get("content-type");
    const accept = request.headers.get("accept");

    if (contentType) {
      headers.set("content-type", contentType);
    }

    if (accept) {
      headers.set("accept", accept);
    }
  } else {
    headers.delete("host");
    headers.delete("connection");
    headers.delete("content-length");
    headers.delete("expect");
    headers.delete("transfer-encoding");
    headers.delete("keep-alive");
    headers.delete("proxy-connection");
    headers.delete("upgrade");
  }

  headers.set("x-forwarded-host", request.headers.get("host") ?? "");
  headers.set("x-forwarded-proto", request.nextUrl.protocol.replace(":", ""));
  return headers;
}

async function proxy(request: NextRequest, context: { params: { path: string[] } }) {
  const { path = [] } = context.params;
  const body =
    METHODS_WITH_BODY.has(request.method) && request.body !== null
      ? await request.arrayBuffer()
      : undefined;

  const upstreamResponse = await fetch(buildUpstreamUrl(path, request.nextUrl.search), {
    method: request.method,
    headers: copyHeaders(request, path),
    body,
    redirect: "manual",
    cache: "no-store"
  });

  const responseHeaders = new Headers(upstreamResponse.headers);
  responseHeaders.delete("content-length");

  return new Response(upstreamResponse.body, {
    status: upstreamResponse.status,
    statusText: upstreamResponse.statusText,
    headers: responseHeaders
  });
}

export async function GET(
  request: NextRequest,
  context: { params: { path: string[] } }
) {
  return proxy(request, context);
}

export async function POST(
  request: NextRequest,
  context: { params: { path: string[] } }
) {
  return proxy(request, context);
}

export async function PUT(
  request: NextRequest,
  context: { params: { path: string[] } }
) {
  return proxy(request, context);
}

export async function PATCH(
  request: NextRequest,
  context: { params: { path: string[] } }
) {
  return proxy(request, context);
}

export async function DELETE(
  request: NextRequest,
  context: { params: { path: string[] } }
) {
  return proxy(request, context);
}

export async function OPTIONS(
  request: NextRequest,
  context: { params: { path: string[] } }
) {
  return proxy(request, context);
}
