import { request } from "@/lib/api/client";
import type {
  AuthRequest,
  AuthResponse,
  RegisterRequest,
  User
} from "@/lib/api/types";

export async function authenticate(payload: AuthRequest) {
  return request<AuthResponse>("/authenticate", {
    method: "POST",
    body: payload
  });
}

export async function fetchSession() {
  return request<AuthResponse>("/authenticate/session");
}

export async function logoutSession() {
  return request<void>("/authenticate/logout", {
    method: "POST"
  });
}

export async function registerAccount(payload: RegisterRequest) {
  return request<User>("/users/register", {
    method: "POST",
    credentials: "omit",
    body: {
      firstName: payload.firstName,
      lastName: payload.lastName,
      email: payload.email,
      phone: payload.phone,
      username: payload.username,
      password: payload.password
    }
  });
}
