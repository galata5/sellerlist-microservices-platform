export function RuntimeEnvScript() {
  const apiUrl = process.env.NEXT_PUBLIC_API_URL ?? "/api";

  return (
    <script
      id="runtime-env"
      dangerouslySetInnerHTML={{
        __html: `window.__APP_ENV__ = ${JSON.stringify({
          NEXT_PUBLIC_API_URL: apiUrl
        })};`
      }}
    />
  );
}
