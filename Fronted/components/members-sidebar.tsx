"use client"

import { useEffect, useState } from "react"
import { Crown, Shield, UserPlus, X, Search, Loader2 } from "lucide-react"
import { cn } from "@/lib/utils"
import { getGroupMembers, addGroupMember, searchUsers, type MemberDTO, type UserSearchDTO } from "@/lib/services/groups.service"

interface MembersSidebarProps {
  groupId: number
}

function AddMemberModal({ groupId, onClose, onAdded }: {
  groupId: number
  onClose: () => void
  onAdded: (member: MemberDTO) => void
}) {
  const [query, setQuery] = useState("")
  const [results, setResults] = useState<UserSearchDTO[]>([])
  const [searching, setSearching] = useState(false)
  const [adding, setAdding] = useState<number | null>(null)
  const [error, setError] = useState<string | null>(null)

  useEffect(() => {
    if (!query.trim() || query.length < 2) { setResults([]); return }
    setSearching(true)
    const t = setTimeout(() => {
      searchUsers(query).then(setResults).catch(() => setResults([])).finally(() => setSearching(false))
    }, 400)
    return () => clearTimeout(t)
  }, [query])

  const handleAdd = async (username: string, userId: number) => {
    setAdding(userId); setError(null)
    try {
      const member = await addGroupMember(groupId, username)
      onAdded(member); onClose()
    } catch (err: unknown) {
      setError(err instanceof Error ? err.message : "Error al anadir miembro")
    } finally { setAdding(null) }
  }

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/60">
      <div className="w-full max-w-sm rounded-xl bg-card border border-border p-6 shadow-2xl">
        <div className="mb-4 flex items-center justify-between">
          <h2 className="text-lg font-semibold text-card-foreground">Anadir miembro</h2>
          <button onClick={onClose} className="text-muted-foreground hover:text-foreground"><X className="h-5 w-5" /></button>
        </div>
        <div className="relative mb-3">
          <Search className="absolute left-3 top-1/2 -translate-y-1/2 h-4 w-4 text-muted-foreground" />
          <input autoFocus value={query} onChange={(e) => setQuery(e.target.value)}
            placeholder="Buscar por nombre o email..."
            className="w-full rounded-lg border border-border bg-background py-2 pl-9 pr-3 text-sm text-foreground focus:outline-none focus:ring-2 focus:ring-primary" />
          {searching && <Loader2 className="absolute right-3 top-1/2 -translate-y-1/2 h-4 w-4 animate-spin text-muted-foreground" />}
        </div>
        <div className="max-h-60 overflow-y-auto space-y-1">
          {results.length === 0 && query.length >= 2 && !searching && (
            <p className="py-4 text-center text-sm text-muted-foreground">No se encontraron usuarios</p>
          )}
          {results.map((u) => (
            <div key={u.userId} className="flex items-center gap-3 rounded-lg p-2 hover:bg-muted">
              <div className="flex h-9 w-9 shrink-0 items-center justify-center rounded-full bg-primary/20 text-sm font-semibold text-primary">
                {u.username.substring(0, 2).toUpperCase()}
              </div>
              <div className="flex-1 min-w-0">
                <p className="truncate text-sm font-medium text-foreground">{u.username}</p>
                <p className="truncate text-xs text-muted-foreground">{u.email}</p>
              </div>
              <button onClick={() => handleAdd(u.username, u.userId)} disabled={adding === u.userId}
                className="shrink-0 rounded-lg bg-primary px-3 py-1 text-xs font-medium text-primary-foreground hover:bg-primary/90 disabled:opacity-50">
                {adding === u.userId ? "..." : "Anadir"}
              </button>
            </div>
          ))}
        </div>
        {error && <p className="mt-3 rounded-lg bg-destructive/10 px-3 py-2 text-sm text-destructive">{error}</p>}
      </div>
    </div>
  )
}

export function MembersSidebar({ groupId }: MembersSidebarProps) {
  const [members, setMembers] = useState<MemberDTO[]>([])
  const [loading, setLoading] = useState(true)
  const [showAddModal, setShowAddModal] = useState(false)

  useEffect(() => {
    if (!groupId) return
    setLoading(true)
    getGroupMembers(groupId).then(setMembers).catch(console.error).finally(() => setLoading(false))
  }, [groupId])

  const handleMemberAdded = (member: MemberDTO) => {
    setMembers((prev) => prev.find((m) => m.userId === member.userId) ? prev : [...prev, member])
  }

  const onlineMembers  = members.filter((m) => m.status === "ONLINE")
  const offlineMembers = members.filter((m) => m.status !== "ONLINE")

  const MemberItem = ({ member }: { member: MemberDTO }) => {
    const initials = member.username.substring(0, 2).toUpperCase()
    const isOnline  = member.status === "ONLINE"
    return (
      <div className="flex w-full items-center gap-2 rounded-md px-2 py-1.5 text-sm hover:bg-muted">
        <div className="relative shrink-0">
          <div className={cn("flex h-8 w-8 items-center justify-center rounded-full text-xs font-semibold",
            isOnline ? "bg-primary text-primary-foreground" : "bg-muted text-muted-foreground")}>
            {initials}
          </div>
          <span className={cn("absolute -bottom-0.5 -right-0.5 h-3 w-3 rounded-full border-2 border-secondary",
            isOnline ? "bg-green-500" : "bg-muted-foreground")} />
        </div>
        <div className="flex flex-1 items-center gap-1 min-w-0">
          <span className={cn("truncate", !isOnline && "text-muted-foreground")}>{member.username}</span>
          {member.role === "OWNER" && <Crown className="h-3 w-3 shrink-0 text-yellow-500" />}
          {member.role === "ADMIN" && <Shield className="h-3 w-3 shrink-0 text-primary" />}
        </div>
      </div>
    )
  }

  return (
    <>
      {showAddModal && (
        <AddMemberModal groupId={groupId} onClose={() => setShowAddModal(false)} onAdded={handleMemberAdded} />
      )}
      <div className="w-60 border-l border-border bg-secondary/30">
        <div className="flex items-center justify-between border-b border-border px-3 py-2">
          <span className="text-xs font-semibold uppercase text-muted-foreground">
            Miembros — {members.length}
          </span>
          <button onClick={() => setShowAddModal(true)} title="Anadir miembro"
            className="rounded p-1 text-muted-foreground hover:bg-muted hover:text-foreground">
            <UserPlus className="h-4 w-4" />
          </button>
        </div>
        <div className="p-3">
          {loading && <p className="text-center text-xs text-muted-foreground py-4">Cargando...</p>}
          {!loading && onlineMembers.length > 0 && (
            <>
              <h3 className="mb-1 px-2 text-xs font-semibold uppercase text-muted-foreground">
                En linea — {onlineMembers.length}
              </h3>
              <div className="space-y-0.5 mb-3">
                {onlineMembers.map((m) => <MemberItem key={m.userId} member={m} />)}
              </div>
            </>
          )}
          {!loading && offlineMembers.length > 0 && (
            <>
              <h3 className="mb-1 px-2 text-xs font-semibold uppercase text-muted-foreground">
                Desconectado — {offlineMembers.length}
              </h3>
              <div className="space-y-0.5">
                {offlineMembers.map((m) => <MemberItem key={m.userId} member={m} />)}
              </div>
            </>
          )}
        </div>
      </div>
    </>
  )
}
