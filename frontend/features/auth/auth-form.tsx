"use client";

import Link from "next/link";
import { useEffect, useState, type FormEvent } from "react";
import { useSearchParams } from "next/navigation";

import { Button } from "@/components/atoms/button";
import { Text } from "@/components/atoms/text";
import { FormField } from "@/components/molecules/form-field";
import { useLoginMutation, useRegisterMutation } from "@/features/auth/hooks";
import { ApiError, sanitizeApiErrorMessage } from "@/lib/api/client";

type AuthFormProps = {
  mode: "login" | "register";
};

export function AuthForm({ mode }: AuthFormProps) {
  const searchParams = useSearchParams();
  const loginMutation = useLoginMutation();
  const registerMutation = useRegisterMutation();
  const [clientValidationError, setClientValidationError] = useState<string | null>(null);
  const [formState, setFormState] = useState({
    firstName: "",
    lastName: "",
    email: "",
    phone: "",
    username: "",
    password: ""
  });

  const isRegister = mode === "register";
  const registrationSucceeded = !isRegister && searchParams.get("registered") === "1";
  const activeMutation = isRegister ? registerMutation : loginMutation;
  const suggestedUsername =
    formState.username.trim().replace(/[^a-zA-Z0-9_]/g, "") || "shopper";
  const updateField = (field: keyof typeof formState, value: string) => {
    setClientValidationError(null);
    setFormState((current) => ({ ...current, [field]: value }));
  };
  const friendlyErrorMessage = (() => {
    if (clientValidationError) {
      return clientValidationError;
    }

    if (!activeMutation.isError || !(activeMutation.error instanceof Error)) {
      return null;
    }

    if (activeMutation.error instanceof ApiError) {
      if (isRegister) {
        if (activeMutation.error.status === 409) {
          const conflictMessage = activeMutation.error.message.toLowerCase();

          if (conflictMessage.includes("username")) {
            return `This username already exists. Try another one, for example ${suggestedUsername}123.`;
          }

          if (conflictMessage.includes("email")) {
            return "This email address already exists. Try another email or sign in to your existing account.";
          }

          if (conflictMessage.includes("phone")) {
            return "This phone number already exists. Try another phone number or sign in to your existing account.";
          }

          return "This account already exists. Try different details or sign in instead.";
        }

        if (activeMutation.error.status === 400) {
          const normalizedMessage = activeMutation.error.message.toLowerCase();

          if (normalizedMessage.includes("password")) {
            return "Password must be 12-72 characters and include an uppercase letter, a lowercase letter, a number, and a special character.";
          }

          if (normalizedMessage.includes("username")) {
            return "Username must be 4-32 characters and can only use letters, numbers, dots, underscores, or hyphens.";
          }

          if (normalizedMessage.includes("phone")) {
            return "Phone number must be 8-20 characters and use only numbers or + ( ) - spaces.";
          }

          if (normalizedMessage.includes("email")) {
            return "Enter a valid email address.";
          }

          return sanitizeApiErrorMessage(
            activeMutation.error.message,
            "We couldn't create the account with these details. Check the form and try again."
          );
        }

        if (activeMutation.error.status === 401 || activeMutation.error.status === 403) {
          return "The register request was blocked before the account was created. Refresh the page and try again.";
        }

        return sanitizeApiErrorMessage(
          activeMutation.error.message,
          "We couldn't create your account right now. Please try again."
        );
      }

      if (activeMutation.error.status === 401) {
        return "No account matches those credentials. Check the username and password or create a new account.";
      }
    }

    if (isRegister && activeMutation.error.message === "Failed to fetch") {
      return "The connection to the app was interrupted. Restart the port-forward, refresh the page, and try again.";
    }

    return sanitizeApiErrorMessage(activeMutation.error.message, "The request could not be completed.");
  })();

  useEffect(() => {
    if (isRegister) {
      return;
    }

    const prefixedUsername = searchParams.get("username") ?? "";
    if (!prefixedUsername) {
      return;
    }

    setFormState((current) =>
      current.username === prefixedUsername ? current : { ...current, username: prefixedUsername }
    );
  }, [isRegister, searchParams]);

  const submit = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    setClientValidationError(null);

    if (isRegister) {
      const trimmedEmail = formState.email.trim();
      const trimmedPhone = formState.phone.trim();
      const trimmedUsername = formState.username.trim();
      const password = formState.password;

      if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(trimmedEmail)) {
        setClientValidationError("Enter a valid email address.");
        return;
      }

      if (!/^[0-9+()\-\s]{8,20}$/.test(trimmedPhone)) {
        setClientValidationError(
          "Phone number must be 8-20 characters and use only numbers or + ( ) - spaces."
        );
        return;
      }

      if (!/^[A-Za-z0-9._-]{4,32}$/.test(trimmedUsername)) {
        setClientValidationError(
          "Username must be 4-32 characters and can only use letters, numbers, dots, underscores, or hyphens."
        );
        return;
      }

      if (!/^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[^A-Za-z\d]).{12,72}$/.test(password)) {
        setClientValidationError(
          "Password must be 12-72 characters and include an uppercase letter, a lowercase letter, a number, and a special character."
        );
        return;
      }
    }

    if (isRegister) {
      await registerMutation.mutateAsync(formState);
      return;
    }

    await loginMutation.mutateAsync({
      username: formState.username,
      password: formState.password
    });
  };

  return (
    <div className="page-shell">
      <div className="relative overflow-hidden rounded-[36px] px-4 py-8 sm:px-8 sm:py-10">
        <div className="absolute inset-0 rounded-[36px] bg-[radial-gradient(circle_at_18%_82%,rgba(59,130,246,0.34),transparent_26%),radial-gradient(circle_at_82%_18%,rgba(244,114,182,0.26),transparent_22%),radial-gradient(circle_at_65%_55%,rgba(251,191,36,0.14),transparent_20%),linear-gradient(135deg,rgba(255,255,255,0.92),rgba(244,244,255,0.88))]" />
        <div className="absolute inset-0 rounded-[36px] bg-[linear-gradient(180deg,rgba(255,255,255,0.44),rgba(255,255,255,0.12))] backdrop-blur-[3px]" />

        <div className="relative mx-auto max-w-[560px]">
          <section className="glass-panel rounded-[30px] p-6 sm:p-8">
            <form className="grid gap-6" onSubmit={submit}>
              <div className="space-y-3 text-center">
                <div className="mx-auto flex h-16 w-16 items-center justify-center rounded-[22px] bg-[linear-gradient(135deg,#F97316_0%,#FB7185_42%,#2563EB_100%)] text-2xl font-bold text-white shadow-[0_18px_36px_rgba(37,99,235,0.22)]">
                  e
                </div>
                <div>
                  <h1 className="text-[clamp(2.2rem,6vw,3rem)] font-semibold tracking-[-0.05em] text-text">
                    {isRegister ? "Join e-shope" : "Welcome Back"}
                  </h1>
                  <Text className="mt-3 text-center">
                    {isRegister
                      ? "Create your account to shop, track orders, and manage your marketplace profile."
                      : "Sign in to your account to continue shopping and managing your orders."}
                  </Text>
                </div>
              </div>

          {isRegister ? (
            <div className="grid gap-4 sm:grid-cols-2">
              <FormField
                label="First name"
                value={formState.firstName}
                onChange={(event) => updateField("firstName", event.target.value)}
                required
              />
              <FormField
                label="Last name"
                value={formState.lastName}
                onChange={(event) => updateField("lastName", event.target.value)}
                required
              />
              <FormField
                label="Email"
                type="email"
                value={formState.email}
                onChange={(event) => updateField("email", event.target.value)}
                required
              />
              <FormField
                label="Phone"
                value={formState.phone}
                onChange={(event) => updateField("phone", event.target.value)}
                required
              />
            </div>
          ) : null}

          <FormField
            label="Username"
            value={formState.username}
            onChange={(event) => updateField("username", event.target.value)}
            className="border-white/45 bg-white/55"
            placeholder="Enter your username"
            required
          />

          <FormField
            label="Password"
            type="password"
            value={formState.password}
            onChange={(event) => updateField("password", event.target.value)}
            className="border-white/45 bg-white/55"
            placeholder="Enter your password"
            required
          />

          {registrationSucceeded ? (
            <div className="rounded-2xl border border-emerald-300/60 bg-emerald-50/85 px-4 py-4 dark:border-emerald-500/40 dark:bg-emerald-500/12">
              <Text size="sm" className="text-emerald-900 dark:text-emerald-100">
                Your account was created successfully. Sign in with the username and password you just chose.
              </Text>
            </div>
          ) : null}

          {friendlyErrorMessage ? (
            <div className="rounded-2xl border border-[#F9A8D4] bg-white/60 px-4 py-4">
              <Text size="sm">
                {friendlyErrorMessage}
              </Text>
            </div>
          ) : null}

          <Button
            className="mt-2 h-12 rounded-[18px] bg-[#191970] text-base font-semibold hover:bg-[#111a67]"
            disabled={activeMutation.isPending}
            type="submit"
          >
            {activeMutation.isPending
              ? "Processing"
              : isRegister
                ? "Create account"
                : "Sign In"}
          </Button>

          <div className="space-y-3 text-center">
            {!isRegister ? (
              <>
                <p className="text-xs font-medium uppercase tracking-[0.18em] text-muted">
                  or continue with
                </p>
                <div className="grid gap-3">
                  {["Continue with Google", "Continue with Apple", "Continue with Meta"].map((label) => (
                    <button
                      key={label}
                      type="button"
                      className="rounded-[18px] border border-white/50 bg-white/58 px-4 py-3 text-sm font-medium text-text transition hover:bg-white/72"
                    >
                      {label}
                    </button>
                  ))}
                </div>
                <button type="button" className="text-sm text-muted transition hover:text-text">
                  Forgot your password?
                </button>
              </>
            ) : null}

          <Text size="sm" className="text-center">
            {isRegister ? "Already have an account?" : "Need an account?"}{" "}
            <Link
              href={isRegister ? "/login" : "/register"}
              className="text-text transition hover:text-accent"
            >
              {isRegister ? "Sign in" : "Create one"}
            </Link>
          </Text>
          </div>
        </form>
      </section>
        </div>
      </div>
    </div>
  );
}
