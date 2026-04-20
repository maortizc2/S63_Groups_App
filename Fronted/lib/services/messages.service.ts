// ─────────────────────────────────────────────────────────────
//  Messages Service — GroupsApp
// ─────────────────────────────────────────────────────────────

import { apiRequest } from "@/lib/api"

export interface MessageDTO {
  id: number
  content: string
  senderId: number
  senderUsername: string
  channelId?: number
  receiverId?: number
  type: "TEXT" | "FILE" | "IMAGE"
  createdAt: string
}

export interface SendMessageRequest {
  content: string
  channelId?: number
  receiverId?: number
  type?: "TEXT" | "FILE" | "IMAGE"
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
