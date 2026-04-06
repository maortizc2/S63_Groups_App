// ─────────────────────────────────────────────────────────────
//  Cliente HTTP base — GroupsApp
// ─────────────────────────────────────────────────────────────

const BASE_URL = process.env.NEXT_PUBLIC_API_URL ?? "http://localhost:8080/api"

// ── Helpers de token ─────────────────────────────────────────

export function getToken(): string | null {
  if (typeof window === "undefined") return null
  return localStorage.getItem("token")
}

export function setToken(token: string): void {
  localStorage.setItem("token", token)
}

export function removeToken(): void {
  localStorage.removeItem("token")
  localStorage.removeItem("user")
}

export function saveUser(user: object): void {
  localStorage.setItem("user", JSON.stringify(user))
}

export function getUser<T>(): T | null {
  if (typeof window === "undefined") return null
  const raw = localStorage.getItem("user")
  return raw ? (JSON.parse(raw) as T) : null
}

// ── Cliente base ─────────────────────────────────────────────

type HttpMethod = "GET" | "POST" | "PUT" | "PATCH" | "DELETE"

interface RequestOptions {
  method?: HttpMethod
  body?: unknown
  requiresAuth?: boolean
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
    } else {
      if (typeof window !== "undefined") {
        window.location.href = "/login"
      }
      throw new Error("No autenticado")
    }
  }

  const fullUrl = `${BASE_URL}${path}`

  // LOG temporal de diagnóstico
  console.warn(`[API →] ${method} ${fullUrl}`)

  const response = await fetch(fullUrl, {
    method,
    headers,
    body: body ? JSON.stringify(body) : undefined,
  })

  console.warn(`[API ←] ${method} ${fullUrl} = ${response.status}`)

  if (response.status === 401) {
    removeToken()
    if (typeof window !== "undefined") {
      window.location.href = "/login"
    }
    throw new Error("Sesión expirada")
  }

  const contentType = response.headers.get("content-type") ?? ""
  const hasJson = contentType.includes("application/json")

  if (!hasJson || response.status === 204) {
    if (!response.ok) {
      throw new Error(`Error ${response.status}`)
    }
    return undefined as unknown as T
  }

  let json: { success?: boolean; message?: string; data?: T }
  try {
    json = await response.json()
  } catch {
    throw new Error(`Respuesta inválida del servidor (${response.status})`)
  }

  if (!response.ok) {
    // LOG del mensaje del backend al fallar
    console.error(`[API ERROR] ${method} ${fullUrl} → ${response.status}:`, json.message)
    throw new Error(json.message ?? `Error ${response.status}`)
  }

  return json.data as T
}
