"use client";

import { useMutation, useQueryClient } from "@tanstack/react-query";
import { useRouter } from "next/navigation";

import { useToast } from "@/components/ui/toast";
import { useAuthSession } from "@/features/auth/auth-provider";
import { authenticate, registerAccount } from "@/features/auth/api";
import type { AuthRequest, AuthResponse, RegisterRequest, User } from "@/lib/api/types";

export function useLoginMutation() {
  const router = useRouter();
  const queryClient = useQueryClient();
  const { notify } = useToast();
  const { setSession } = useAuthSession();

  return useMutation<AuthResponse, Error, AuthRequest>({
    mutationFn: authenticate,
    async onSuccess(data, variables) {
      setSession({
        userId: data.userId,
        username: data.username || variables.username
      });
      await queryClient.invalidateQueries();
      notify({
        title: "Session opened",
        description: "You're connected to the commerce workspace."
      });
      router.push("/dashboard");
    }
  });
}

export function useRegisterMutation() {
  const router = useRouter();
  const { notify } = useToast();

  return useMutation<User, Error, RegisterRequest>({
    mutationFn: registerAccount,
    async onSuccess(_, variables) {
      notify({
        title: "Welcome to e-shope",
        description: "Your account is ready. Sign in to start shopping and managing orders."
      });
      router.push(`/login?registered=1&username=${encodeURIComponent(variables.username)}`);
    }
  });
}
