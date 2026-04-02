// ─────────────────────────────────────────────────────────────
//  Cliente HTTP base — GroupsApp
//  Todas las llamadas al backend pasan por aquí.
//  Si el token cambia o la URL base cambia, solo se edita este archivo.
// ─────────────────────────────────────────────────────────────

const BASE_URL = process.env.NEXT_PUBLIC_API_URL ?? "http://localhost:8080/api"

// ── Helpers de token ─────────────────────────────────────────

export function getToken(): string | null {
  if (typeof window === "undefined") return null   // SSR guard
  return localStorage.getItem("token")
}

export function setToken(token: string): void {
  localStorage.setItem("token", token)
}

export function removeToken(): void {
  localStorage.removeItem("token")
}

// ── Cliente base ─────────────────────────────────────────────

type HttpMethod = "GET" | "POST" | "PUT" | "PATCH" | "DELETE"

interface RequestOptions {
  method?: HttpMethod
  body?: unknown
  requiresAuth?: boolean   // true por defecto
}

export async function apiRequest<T>(
  path: string,
  options: RequestOptions = {}
): Promise<T> {
  const { method = "GET", body, requiresAuth = true } = options

  const headers: Record<string, string> = {
    "Content-Type": "application/json",
  }

  if (requiresAuth) {
    const token = getToken()
    if (token) {
      headers["Authorization"] = `Bearer ${token}`
    }
  }

  const response = await fetch(`${BASE_URL}${path}`, {
    method,
    headers,
    body: body ? JSON.stringify(body) : undefined,
  })

  // El backend responde con { success, message, data }
  const json = await response.json()

  if (!response.ok) {
    // Lanza el mensaje de error que viene del backend
    throw new Error(json.message ?? "Error en la petición")
  }

  return json.data as T
}
