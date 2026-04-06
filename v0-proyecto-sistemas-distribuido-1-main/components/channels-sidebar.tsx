"use client"

import { useState } from "react"
import { Hash, Megaphone, ChevronDown, Plus, Search, UserPlus, X, Loader2 } from "lucide-react"
import { cn } from "@/lib/utils"
import type { Channel } from "@/app/page"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { getUser } from "@/lib/api"
import { createChannel } from "@/lib/services/channels.service"
import { addGroupMember, searchUsers, type UserSearchDTO } from "@/lib/services/groups.service"
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuSeparator,
  DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu"

interface ChannelsSidebarProps {
  groupId: number
  groupName: string
  channels: Channel[]
  selectedChannel: Channel
  onSelectChannel: (channel: Channel) => void
  onChannelCreated: (channel: Channel) => void
}

const channelIcons = {
  text: Hash,
  announcement: Megaphone,
}

// ── Modal: crear canal ────────────────────────────────────────
function CreateChannelModal({ groupId, onClose, onCreated }: {
  groupId: number
  onClose: () => void
  onCreated: (channel: Channel) => void
}) {
  const [name, setName] = useState("")
  const [description, setDescription] = useState("")
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState<string | null>(null)

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    if (!name.trim()) return
    setLoading(true); setError(null)
    try {
      const created = await createChannel(groupId, {
        name: name.trim().toLowerCase().replace(/\s+/g, "-"),
        description: description.trim(),
      })
      onCreated({ id: String(created.id), name: created.name, type: "text", unread: 0 })
      onClose()
    } catch (err: unknown) {
      setError(err instanceof Error ? err.message : "Error al crear el canal")
    } finally { setLoading(false) }
  }

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/60">
      <div className="w-full max-w-sm rounded-xl bg-card border border-border p-6 shadow-2xl">
        <div className="mb-4 flex items-center justify-between">
          <h2 className="text-lg font-semibold text-card-foreground">Crear canal</h2>
          <button onClick={onClose} className="text-muted-foreground hover:text-foreground"><X className="h-5 w-5" /></button>
        </div>
        <form onSubmit={handleSubmit} className="space-y-4">
          <div>
            <label className="mb-1 block text-sm font-medium text-foreground">Nombre <span className="text-destructive">*</span></label>
            <div className="flex items-center gap-1 rounded-lg border border-border bg-background px-3 py-2">
              <Hash className="h-4 w-4 text-muted-foreground shrink-0" />
              <input value={name} onChange={(e) => setName(e.target.value)} placeholder="nombre-del-canal" required
                className="flex-1 bg-transparent text-sm text-foreground focus:outline-none" />
            </div>
          </div>
          <div>
            <label className="mb-1 block text-sm font-medium text-foreground">Descripcion</label>
            <input value={description} onChange={(e) => setDescription(e.target.value)}
              placeholder="De que trata este canal?"
              className="w-full rounded-lg border border-border bg-background px-3 py-2 text-sm text-foreground focus:outline-none focus:ring-2 focus:ring-primary" />
          </div>
          {error && <p className="rounded-lg bg-destructive/10 px-3 py-2 text-sm text-destructive">{error}</p>}
          <div className="flex gap-3 pt-2">
            <button type="button" onClick={onClose}
              className="flex-1 rounded-lg border border-border py-2 text-sm font-medium text-muted-foreground hover:bg-muted">
              Cancelar
            </button>
            <button type="submit" disabled={loading || !name.trim()}
              className="flex-1 rounded-lg bg-primary py-2 text-sm font-medium text-primary-foreground hover:bg-primary/90 disabled:opacity-50">
              {loading ? "Creando..." : "Crear"}
            </button>
          </div>
        </form>
      </div>
    </div>
  )
}

// ── Modal: invitar persona ────────────────────────────────────
function InviteModal({ groupId, onClose }: { groupId: number; onClose: () => void }) {
  const [query, setQuery] = useState("")
  const [results, setResults] = useState<UserSearchDTO[]>([])
  const [searching, setSearching] = useState(false)
  const [adding, setAdding] = useState<number | null>(null)
  const [success, setSuccess] = useState<string | null>(null)
  const [error, setError] = useState<string | null>(null)

  const handleSearch = (q: string) => {
    setQuery(q); setSuccess(null); setError(null)
    if (q.length < 2) { setResults([]); return }
    setSearching(true)
    const t = setTimeout(() => {
      searchUsers(q).then(setResults).catch(() => setResults([])).finally(() => setSearching(false))
    }, 400)
    return () => clearTimeout(t)
  }

  const handleAdd = async (username: string, userId: number) => {
    setAdding(userId); setError(null); setSuccess(null)
    try {
      await addGroupMember(groupId, username)
      setSuccess(`${username} fue anadido al grupo`)
      setResults((prev) => prev.filter((u) => u.userId !== userId))
    } catch (err: unknown) {
      setError(err instanceof Error ? err.message : "Error")
    } finally { setAdding(null) }
  }

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/60">
      <div className="w-full max-w-sm rounded-xl bg-card border border-border p-6 shadow-2xl">
        <div className="mb-4 flex items-center justify-between">
          <h2 className="text-lg font-semibold text-card-foreground">Invitar personas</h2>
          <button onClick={onClose} className="text-muted-foreground hover:text-foreground"><X className="h-5 w-5" /></button>
        </div>
        <div className="relative mb-3">
          <Search className="absolute left-3 top-1/2 -translate-y-1/2 h-4 w-4 text-muted-foreground" />
          <input autoFocus value={query} onChange={(e) => handleSearch(e.target.value)}
            placeholder="Buscar por nombre o email..."
            className="w-full rounded-lg border border-border bg-background py-2 pl-9 pr-3 text-sm text-foreground focus:outline-none focus:ring-2 focus:ring-primary" />
          {searching && <Loader2 className="absolute right-3 top-1/2 -translate-y-1/2 h-4 w-4 animate-spin text-muted-foreground" />}
        </div>
        <div className="max-h-52 overflow-y-auto space-y-1">
          {results.length === 0 && query.length >= 2 && !searching && (
            <p className="py-3 text-center text-sm text-muted-foreground">Sin resultados</p>
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
                {adding === u.userId ? "..." : "Invitar"}
              </button>
            </div>
          ))}
        </div>
        {success && <p className="mt-3 rounded-lg bg-green-500/10 px-3 py-2 text-sm text-green-600">{success}</p>}
        {error   && <p className="mt-3 rounded-lg bg-destructive/10 px-3 py-2 text-sm text-destructive">{error}</p>}
      </div>
    </div>
  )
}

// ── Sidebar principal ─────────────────────────────────────────
export function ChannelsSidebar({
  groupId,
  groupName,
  channels,
  selectedChannel,
  onSelectChannel,
  onChannelCreated,
}: ChannelsSidebarProps) {
  const currentUser = getUser<{ userId: number; username: string; email: string }>()
  const initials = currentUser?.username?.substring(0, 2).toUpperCase() ?? "??"

  const [showCreateChannel, setShowCreateChannel] = useState(false)
  const [showInvite, setShowInvite] = useState(false)
  const [search, setSearch] = useState("")

  const filtered = channels.filter((c) =>
    c.name.toLowerCase().includes(search.toLowerCase())
  )

  return (
    <>
      {showCreateChannel && (
        <CreateChannelModal
          groupId={groupId}
          onClose={() => setShowCreateChannel(false)}
          onCreated={(ch) => { onChannelCreated(ch); setShowCreateChannel(false) }}
        />
      )}
      {showInvite && (
        <InviteModal groupId={groupId} onClose={() => setShowInvite(false)} />
      )}

      <div className="flex w-60 flex-col bg-secondary/50">
        {/* Group header */}
        <DropdownMenu>
          <DropdownMenuTrigger asChild>
            <button className="flex h-12 items-center justify-between border-b border-border px-4 font-semibold text-foreground shadow-sm transition-colors hover:bg-muted">
              <span className="truncate">{groupName}</span>
              <ChevronDown className="h-4 w-4 text-muted-foreground" />
            </button>
          </DropdownMenuTrigger>
          <DropdownMenuContent className="w-56 bg-card text-card-foreground border-border" align="start">
            <DropdownMenuItem className="cursor-pointer focus:bg-muted" onClick={() => setShowInvite(true)}>
              <UserPlus className="mr-2 h-4 w-4" />
              Invitar personas
            </DropdownMenuItem>
            <DropdownMenuItem className="cursor-pointer focus:bg-muted" onClick={() => setShowCreateChannel(true)}>
              <Plus className="mr-2 h-4 w-4" />
              Crear canal
            </DropdownMenuItem>
            <DropdownMenuSeparator className="bg-border" />
            <DropdownMenuItem className="cursor-pointer focus:bg-muted">
              Configuracion del grupo
            </DropdownMenuItem>
          </DropdownMenuContent>
        </DropdownMenu>

        {/* Search */}
        <div className="p-2">
          <div className="relative">
            <Search className="absolute left-2.5 top-1/2 h-4 w-4 -translate-y-1/2 text-muted-foreground" />
            <Input placeholder="Buscar canal" value={search} onChange={(e) => setSearch(e.target.value)}
              className="h-8 bg-muted/50 pl-8 text-sm border-transparent focus:border-ring" />
          </div>
        </div>

        {/* Channels list */}
        <div className="flex-1 overflow-y-auto px-2 pb-2">
          <div className="mb-1">
            <div className="flex items-center justify-between px-1 py-1.5">
              <span className="text-xs font-semibold uppercase text-muted-foreground">Canales de texto</span>
              <Button variant="ghost" size="icon" className="h-4 w-4 text-muted-foreground hover:text-foreground"
                onClick={() => setShowCreateChannel(true)}>
                <Plus className="h-3 w-3" />
              </Button>
            </div>
            {filtered.length === 0 && (
              <p className="px-2 py-1 text-xs text-muted-foreground">Sin canales</p>
            )}
            {filtered.map((channel) => {
              const Icon = channelIcons[channel.type] ?? Hash
              return (
                <button key={channel.id} onClick={() => onSelectChannel(channel)}
                  className={cn(
                    "mb-0.5 flex w-full items-center gap-1.5 rounded-md px-2 py-1.5 text-sm text-muted-foreground transition-colors hover:bg-muted hover:text-foreground",
                    selectedChannel?.id === channel.id && "bg-muted text-foreground font-medium"
                  )}>
                  <Icon className="h-4 w-4 shrink-0" />
                  <span className="truncate">{channel.name}</span>
                  {channel.unread > 0 && (
                    <span className="ml-auto flex h-4 min-w-4 items-center justify-center rounded-full bg-primary px-1 text-[10px] font-bold text-primary-foreground">
                      {channel.unread}
                    </span>
                  )}
                </button>
              )
            })}
          </div>
        </div>

        {/* User panel */}
        <div className="flex items-center gap-2 border-t border-border bg-secondary/80 p-2">
          <div className="relative">
            <div className="flex h-8 w-8 items-center justify-center rounded-full bg-primary text-xs font-semibold text-primary-foreground">
              {initials}
            </div>
            <span className="absolute -bottom-0.5 -right-0.5 h-3 w-3 rounded-full border-2 border-secondary bg-green-500" />
          </div>
          <div className="flex-1 min-w-0">
            <p className="truncate text-sm font-medium text-foreground">{currentUser?.username ?? "Usuario"}</p>
            <p className="truncate text-xs text-muted-foreground">En linea</p>
          </div>
        </div>
      </div>
    </>
  )
}
