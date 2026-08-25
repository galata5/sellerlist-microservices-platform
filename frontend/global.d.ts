export {};

declare global {
  interface Window {
    __APP_ENV__?: {
      NEXT_PUBLIC_API_URL?: string;
    };
  }
}
