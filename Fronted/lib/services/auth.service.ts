// ─────────────────────────────────────────────────────────────
//  Auth Service — GroupsApp
// ─────────────────────────────────────────────────────────────

import { apiRequest, setToken, removeToken, saveUser } from "@/lib/api"

export interface RegisterRequest {
  username: string
  email: string
  password: string
}

export interface LoginRequest {
  email: string
  password: string
}

export interface AuthResponse {
  token: string
  type: string
  userId: number
  username: string
  email: string
}

export async function register(data: RegisterRequest): Promise<AuthResponse> {
  const response = await apiRequest<AuthResponse>("/auth/register", {
    method: "POST",
    body: data,
    requiresAuth: false,
  })
  setToken(response.token)
  saveUser({ userId: response.userId, username: response.username, email: response.email })
  return response
}

export async function login(data: LoginRequest): Promise<AuthResponse> {
  const response = await apiRequest<AuthResponse>("/auth/login", {
    method: "POST",
    body: data,
    requiresAuth: false,
  })
  setToken(response.token)
  saveUser({ userId: response.userId, username: response.username, email: response.email })
  return response
}

export async function logout(): Promise<void> {
  try {
    await apiRequest<void>("/auth/logout", {
      method: "POST",
      requiresAuth: true,
    })
  } finally {
    removeToken()
  }
}
