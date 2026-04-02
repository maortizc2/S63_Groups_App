// ─────────────────────────────────────────────────────────────
//  Auth Service — GroupsApp
//  Encapsula todas las llamadas al backend relacionadas con
//  autenticación: register, login, logout.
// ─────────────────────────────────────────────────────────────

import { apiRequest, setToken, removeToken } from "@/lib/api"

// ── Tipos que espera el backend ───────────────────────────────

export interface RegisterRequest {
  username: string
  email: string
  password: string
}

export interface LoginRequest {
  email: string
  password: string
}

// ── Tipo que devuelve el backend en data ──────────────────────

export interface AuthResponse {
  token: string
  id: number
  username: string
  email: string
}

// ── Funciones del servicio ────────────────────────────────────

export async function register(data: RegisterRequest): Promise<AuthResponse> {
  const response = await apiRequest<AuthResponse>("/auth/register", {
    method: "POST",
    body: data,
    requiresAuth: false,   // No necesita token — el usuario aún no está logueado
  })
  setToken(response.token) // Guarda el token automáticamente al registrarse
  return response
}

export async function login(data: LoginRequest): Promise<AuthResponse> {
  const response = await apiRequest<AuthResponse>("/auth/login", {
    method: "POST",
    body: data,
    requiresAuth: false,   // No necesita token — el usuario aún no está logueado
  })
  setToken(response.token) // Guarda el token automáticamente al loguearse
  return response
}

export async function logout(): Promise<void> {
  await apiRequest<void>("/auth/logout", {
    method: "POST",
    requiresAuth: true,
  })
  removeToken() // Elimina el token local al cerrar sesión
}
