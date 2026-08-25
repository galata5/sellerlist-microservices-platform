"use client";

import {
  createContext,
  type PropsWithChildren,
  useContext,
  useEffect,
  useState
} from "react";
import { useQueryClient } from "@tanstack/react-query";

type AuthSession = {
  userId?: number;
  username: string;
};

type AuthContextValue = {
  session: AuthSession | null;
  isAuthenticated: boolean;
  setSession: (session: AuthSession) => void;
  clearSession: () => Promise<void>;
};

const AuthContext = createContext<AuthContextValue | null>(null);

async function readServerSession() {
  const { fetchSession } = await import("@/features/auth/api");

  try {
    const session = await fetchSession();
    return session.authenticated
      ? { userId: session.userId, username: session.username }
      : null;
  } catch {
    return null;
  }
}

export function AuthProvider({ children }: PropsWithChildren) {
  const [session, setSessionState] = useState<AuthSession | null>(null);
  const queryClient = useQueryClient();

  useEffect(() => {
    let active = true;

    void readServerSession().then((nextSession) => {
      if (active) {
        setSessionState(nextSession);
      }
    });

    return () => {
      active = false;
    };
  }, []);

  const value: AuthContextValue = {
    session,
    isAuthenticated: Boolean(session),
    setSession(nextSession) {
      setSessionState(nextSession);
    },
    async clearSession() {
      try {
        const { logoutSession } = await import("@/features/auth/api");
        await logoutSession();
      } finally {
        setSessionState(null);
        await queryClient.invalidateQueries();
      }
    }
  };

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuthSession() {
  const context = useContext(AuthContext);

  if (!context) {
    throw new Error("useAuthSession must be used within AuthProvider.");
  }

  return context;
}
