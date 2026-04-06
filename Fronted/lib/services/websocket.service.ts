// ─────────────────────────────────────────────────────────────
//  WebSocket Service — GroupsApp
//  Usa @stomp/stompjs + SockJS para conectarse al backend
//  Spring Boot configurado con .withSockJS()
// ─────────────────────────────────────────────────────────────

import { Client, type IMessage, type StompSubscription } from "@stomp/stompjs"
import { getToken } from "@/lib/api"
import type { MessageDTO } from "@/lib/services/messages.service"

declare const SockJS: new (url: string) => unknown

// ── Estado global del cliente ─────────────────────────────────
let stompClient: Client | null = null
let isConnected = false
const pendingCallbacks: Array<() => void> = []   // callbacks que esperan conexión
const subscriptions = new Map<string, StompSubscription>()

// ── Ejecutar cuando el cliente esté conectado ─────────────────
function whenConnected(fn: () => void): void {
  if (isConnected && stompClient?.connected) {
    fn()
  } else {
    pendingCallbacks.push(fn)
  }
}

function flushPending(): void {
  isConnected = true
  while (pendingCallbacks.length > 0) {
    const cb = pendingCallbacks.shift()
    try { cb?.() } catch (e) { console.error("[STOMP] callback error", e) }
  }
}

// ── Inicializar y conectar ────────────────────────────────────
export function initStomp(): void {
  if (typeof window === "undefined") return   // guard SSR
  if (stompClient?.active) return             // ya está activo

  const token   = getToken()
  const baseUrl = (process.env.NEXT_PUBLIC_API_URL ?? "http://localhost:8080/api").replace("/api", "")

  stompClient = new Client({
    webSocketFactory: () => new SockJS(`${baseUrl}/ws`),
    connectHeaders: token ? { Authorization: `Bearer ${token}` } : {},
    reconnectDelay: 5000,

    onConnect: () => {
      flushPending()
    },

    onDisconnect: () => {
      isConnected = false
    },

    onStompError: (frame) => {
      console.error("[STOMP] error:", frame.headers["message"])
      isConnected = false
    },

    debug: process.env.NODE_ENV === "development"
      ? (msg) => console.debug("[STOMP]", msg)
      : () => {},
  })

  stompClient.activate()
}

// ── Desconectar limpiamente ───────────────────────────────────
export function disconnectStomp(): void {
  isConnected = false
  pendingCallbacks.length = 0

  subscriptions.forEach((sub) => {
    try { sub.unsubscribe() } catch { /**/ }
  })
  subscriptions.clear()

  if (stompClient?.active) {
    stompClient.deactivate()
  }
  stompClient = null
}

// ── Suscribirse a canal de grupo ──────────────────────────────
// /topic/channel/{channelId}
export function subscribeToChannel(
  channelId: number,
  onMessage: (msg: MessageDTO) => void
): () => void {
  const key = `channel-${channelId}`
  const destination = `/topic/channel/${channelId}`

  // Cancelar suscripción previa si existe
  if (subscriptions.has(key)) {
    try { subscriptions.get(key)!.unsubscribe() } catch { /**/ }
    subscriptions.delete(key)
  }

  whenConnected(() => {
    if (!stompClient?.connected) return
    // Verificar que no se suscribió mientras esperaba
    if (subscriptions.has(key)) return

    const sub = stompClient.subscribe(destination, (frame: IMessage) => {
      try {
        onMessage(JSON.parse(frame.body) as MessageDTO)
      } catch {
        console.error("[STOMP] parse error:", frame.body)
      }
    })
    subscriptions.set(key, sub)
  })

  // Retorna unsubscribe
  return () => {
    if (subscriptions.has(key)) {
      try { subscriptions.get(key)!.unsubscribe() } catch { /**/ }
      subscriptions.delete(key)
    }
    // Remover de pendientes si aún no se procesó
    const idx = pendingCallbacks.indexOf(() => {})
    if (idx !== -1) pendingCallbacks.splice(idx, 1)
  }
}

// ── Suscribirse a mensajes directos ──────────────────────────
// /user/queue/messages
export function subscribeToDirectMessages(
  onMessage: (msg: MessageDTO) => void
): () => void {
  const key = "direct-messages"

  if (subscriptions.has(key)) {
    try { subscriptions.get(key)!.unsubscribe() } catch { /**/ }
    subscriptions.delete(key)
  }

  whenConnected(() => {
    if (!stompClient?.connected) return
    if (subscriptions.has(key)) return

    const sub = stompClient.subscribe("/user/queue/messages", (frame: IMessage) => {
      try {
        onMessage(JSON.parse(frame.body) as MessageDTO)
      } catch {
        console.error("[STOMP] parse DM error:", frame.body)
      }
    })
    subscriptions.set(key, sub)
  })

  return () => {
    if (subscriptions.has(key)) {
      try { subscriptions.get(key)!.unsubscribe() } catch { /**/ }
      subscriptions.delete(key)
    }
  }
}
