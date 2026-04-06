// ─────────────────────────────────────────────────────────────
//  Channels Service — GroupsApp
// ─────────────────────────────────────────────────────────────

import { apiRequest } from "@/lib/api"

export interface ChannelDTO {
  id: number
  name: string
  description: string
  groupId: number
}

export interface CreateChannelRequest {
  name: string
  description: string
}

export async function getChannelsByGroup(groupId: number): Promise<ChannelDTO[]> {
  return apiRequest<ChannelDTO[]>(`/groups/${groupId}/channels`)
}

export async function createChannel(groupId: number, data: CreateChannelRequest): Promise<ChannelDTO> {
  return apiRequest<ChannelDTO>(`/groups/${groupId}/channels`, { method: "POST", body: data })
}

export async function deleteChannel(groupId: number, channelId: number): Promise<void> {
  return apiRequest<void>(`/groups/${groupId}/channels/${channelId}`, { method: "DELETE" })
}
