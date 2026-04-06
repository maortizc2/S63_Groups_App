// ─────────────────────────────────────────────────────────────
//  Hook: useCurrentUser
//  Lee el usuario desde localStorage SOLO en el cliente,
//  evitando hydration mismatch entre SSR y cliente.
// ─────────────────────────────────────────────────────────────
"use client"

import { useState, useEffect } from "react"
import { getUser } from "@/lib/api"

export interface CurrentUser {
  userId: number
  username: string
  email: string
}

export function useCurrentUser(): CurrentUser | null {
  const [user, setUser] = useState<CurrentUser | null>(null)

  useEffect(() => {
    // Solo se ejecuta en el cliente, después del montaje
    setUser(getUser<CurrentUser>())
  }, [])

  return user
}
