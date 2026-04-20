"use client"

import { useEffect } from "react"
import { initStomp, subscribeToDirectMessages } from "@/lib/services/websocket.service"
import { getToken } from "@/lib/api"

/**
 * Componente que mantiene la conexión STOMP activa en toda la aplicación.
 * Se monta una sola vez en el layout y persiste entre navegaciones.
 * Esto garantiza que los mensajes directos lleguen sin importar en qué
 * página esté el usuario.
 */
export function StompProvider({ children }: { children: React.ReactNode }) {
  useEffect(() => {
    // Solo conectar si hay sesión activa
    if (!getToken()) return

    initStomp()

    // Suscripción global a DMs — notifica aunque no estés en /messages
    const unsub = subscribeToDirectMessages((msg) => {
      // Emitir evento del navegador para que cualquier componente lo escuche
      window.dispatchEvent(new CustomEvent("dm:received", { detail: msg }))
    })

    return () => { unsub() }
  }, [])

  return <>{children}</>
}
