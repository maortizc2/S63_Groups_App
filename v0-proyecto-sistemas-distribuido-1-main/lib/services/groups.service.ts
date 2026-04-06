// ─────────────────────────────────────────────────────────────
//  Groups Service — GroupsApp
// ─────────────────────────────────────────────────────────────

import { apiRequest } from "@/lib/api"

export interface GroupDTO {
  id: number
  name: string
  description: string
  type: "PUBLIC" | "PRIVATE"
  memberCount: number
  ownerId: number
  ownerUsername: string
  createdAt: string
}

export interface CreateGroupRequest {
  name: string
  description: string
  type: "PUBLIC" | "PRIVATE"
}

export interface MemberDTO {
  userId: number
  username: string
  email: string
  avatarUrl: string | null
  role: "OWNER" | "ADMIN" | "MEMBER"
  status: "ONLINE" | "OFFLINE"
}

export interface UserSearchDTO {
  userId: number
  username: string
  email: string
  avatarUrl: string | null
  status: "ONLINE" | "OFFLINE"
}

// ── Grupos ────────────────────────────────────────────────────

export async function getMyGroups(): Promise<GroupDTO[]> {
  return apiRequest<GroupDTO[]>("/groups")
}

export async function getGroupById(id: number): Promise<GroupDTO> {
  return apiRequest<GroupDTO>(`/groups/${id}`)
}

export async function searchGroups(name: string): Promise<GroupDTO[]> {
  return apiRequest<GroupDTO[]>(`/groups/search?name=${encodeURIComponent(name)}`)
}

export async function createGroup(data: CreateGroupRequest): Promise<GroupDTO> {
  return apiRequest<GroupDTO>("/groups", { method: "POST", body: data })
}

export async function joinGroup(id: number): Promise<void> {
  return apiRequest<void>(`/groups/${id}/join`, { method: "POST" })
}

export async function leaveGroup(id: number): Promise<void> {
  return apiRequest<void>(`/groups/${id}/leave`, { method: "POST" })
}

// ── Miembros ──────────────────────────────────────────────────

export async function getGroupMembers(groupId: number): Promise<MemberDTO[]> {
  return apiRequest<MemberDTO[]>(`/groups/${groupId}/members`)
}

export async function addGroupMember(groupId: number, username: string): Promise<MemberDTO> {
  return apiRequest<MemberDTO>(`/groups/${groupId}/members`, {
    method: "POST",
    body: { username },
  })
}

// ── Búsqueda de usuarios (para DMs y para añadir al grupo) ───

export async function searchUsers(q: string): Promise<UserSearchDTO[]> {
  return apiRequest<UserSearchDTO[]>(`/groups/users/search?q=${encodeURIComponent(q)}`)
}
