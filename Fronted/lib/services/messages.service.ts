// ─────────────────────────────────────────────────────────────
//  Messages Service — GroupsApp
// ─────────────────────────────────────────────────────────────

import { apiRequest, getToken } from "@/lib/api"

const BASE_URL = process.env.NEXT_PUBLIC_API_URL ?? "http://localhost:8080/api"

export interface MessageDTO {
  id: number
  content: string
  senderId: number
  senderUsername: string
  senderAvatarUrl?: string
  channelId?: number
  receiverId?: number
  type: "TEXT" | "FILE" | "IMAGE"
  createdAt: string
  // Archivo adjunto
  fileId?: number
  fileName?: string
  fileUrl?: string
  // Estado de entrega
  deliveryStatus?: string
}

export interface SendMessageRequest {
  content: string
  channelId?: number
  receiverId?: number
  type?: "TEXT" | "FILE" | "IMAGE"
  fileId?: number
}

export async function getChannelHistory(channelId: number): Promise<MessageDTO[]> {
  return apiRequest<MessageDTO[]>(`/messages/channel/${channelId}`)
}

export async function getDirectHistory(userId: number): Promise<MessageDTO[]> {
  return apiRequest<MessageDTO[]>(`/messages/direct/${userId}`)
}

export async function getUnreadCount(channelId: number): Promise<number> {
  return apiRequest<number>(`/messages/channel/${channelId}/unread`)
}

export async function sendMessage(data: SendMessageRequest): Promise<MessageDTO> {
  return apiRequest<MessageDTO>("/messages", {
    method: "POST",
    body: { type: "TEXT", ...data },
  })
}

/** Sube un archivo y retorna la metadata con el fileId */
export async function uploadFile(file: File): Promise<{ id: number; originalName: string; mimeType: string; size: number }> {
  const token = getToken()
  const form = new FormData()
  form.append("file", file)

  const response = await fetch(`${BASE_URL}/files/upload`, {
    method: "POST",
    headers: token ? { Authorization: `Bearer ${token}` } : {},
    body: form,
  })

  if (!response.ok) {
    throw new Error(`Error subiendo archivo: ${response.status}`)
  }

  const json = await response.json()
  return json.data
}

/** Genera la URL completa de descarga de un archivo */
export function getFileDownloadUrl(fileId: number): string {
  return `${BASE_URL}/files/${fileId}`
}
