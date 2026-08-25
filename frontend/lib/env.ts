type PublicRuntimeEnv = {
  NEXT_PUBLIC_API_URL: string;
};

function resolveApiUrl() {
  const browserValue =
    typeof window !== "undefined" ? window.__APP_ENV__?.NEXT_PUBLIC_API_URL : undefined;
  const serverValue = process.env.NEXT_PUBLIC_API_URL;
  const value = browserValue ?? serverValue ?? "/api";

  return value.replace(/\/$/, "");
}

export function getPublicRuntimeEnv(): PublicRuntimeEnv {
  return {
    NEXT_PUBLIC_API_URL: resolveApiUrl()
  };
}
