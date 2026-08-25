import { AuthForm } from "@/features/auth/auth-form";

export const dynamic = "force-dynamic";
export const revalidate = 0;

export default function RegisterPage() {
  return <AuthForm mode="register" />;
}
