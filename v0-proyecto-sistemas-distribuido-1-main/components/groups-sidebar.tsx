// ─────────────────────────────────────────────────────────────
//  Groups Sidebar — con modal para crear grupo
// ─────────────────────────────────────────────────────────────
"use client"

import { useEffect, useState } from "react"
import { Plus, Settings, MessageCircle, LogOut, X } from "lucide-react"
import { cn } from "@/lib/utils"
import Link from "next/link"
import type { Group } from "@/app/page"
import { getMyGroups, createGroup, type GroupDTO } from "@/lib/services/groups.service"
import { logout } from "@/lib/services/auth.service"
import {
  Tooltip,
  TooltipContent,
  TooltipProvider,
  TooltipTrigger,
} from "@/components/ui/tooltip"

interface GroupsSidebarProps {
  groups: Group[]
  selectedGroup: Group | null
  onSelectGroup: (group: Group) => void
}

function toUiGroup(g: GroupDTO): Group {
  const words = g.name.trim().split(" ")
  const avatar =
    words.length >= 2
      ? (words[0][0] + words[1][0]).toUpperCase()
      : g.name.substring(0, 2).toUpperCase()
  return { id: String(g.id), name: g.name, avatar, unread: 0 }
}

// ── Modal crear grupo ─────────────────────────────────────────
function CreateGroupModal({
  onClose,
  onCreated,
}: {
  onClose: () => void
  onCreated: (group: Group) => void
}) {
  const [name, setName] = useState("")
  const [description, setDescription] = useState("")
  const [type, setType] = useState<"PUBLIC" | "PRIVATE">("PUBLIC")
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState<string | null>(null)

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    if (!name.trim()) return
    setLoading(true)
    setError(null)
    try {
      const created = await createGroup({ name: name.trim(), description: description.trim(), type })
      onCreated(toUiGroup(created))
      onClose()
    } catch (err: unknown) {
      setError(err instanceof Error ? err.message : "Error al crear el grupo")
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/60">
      <div className="w-full max-w-md rounded-xl bg-card border border-border p-6 shadow-2xl">
        <div className="mb-4 flex items-center justify-between">
          <h2 className="text-lg font-semibold text-card-foreground">Crear grupo</h2>
          <button onClick={onClose} className="text-muted-foreground hover:text-foreground">
            <X className="h-5 w-5" />
          </button>
        </div>

        <form onSubmit={handleSubmit} className="space-y-4">
          <div>
            <label className="mb-1 block text-sm font-medium text-foreground">
              Nombre <span className="text-destructive">*</span>
            </label>
            <input
              value={name}
              onChange={(e) => setName(e.target.value)}
              placeholder="Ej: Backend Team"
              required
              className="w-full rounded-lg border border-border bg-background px-3 py-2 text-sm text-foreground focus:outline-none focus:ring-2 focus:ring-primary"
            />
          </div>

          <div>
            <label className="mb-1 block text-sm font-medium text-foreground">
              Descripción
            </label>
            <textarea
              value={description}
              onChange={(e) => setDescription(e.target.value)}
              placeholder="¿De qué trata este grupo?"
              rows={3}
              className="w-full rounded-lg border border-border bg-background px-3 py-2 text-sm text-foreground focus:outline-none focus:ring-2 focus:ring-primary resize-none"
            />
          </div>

          <div>
            <label className="mb-1 block text-sm font-medium text-foreground">Tipo</label>
            <div className="flex gap-3">
              {(["PUBLIC", "PRIVATE"] as const).map((t) => (
                <button
                  key={t}
                  type="button"
                  onClick={() => setType(t)}
                  className={cn(
                    "flex-1 rounded-lg border py-2 text-sm font-medium transition-colors",
                    type === t
                      ? "border-primary bg-primary text-primary-foreground"
                      : "border-border bg-background text-muted-foreground hover:border-primary"
                  )}
                >
                  {t === "PUBLIC" ? "🌐 Público" : "🔒 Privado"}
                </button>
              ))}
            </div>
          </div>

          {error && (
            <p className="rounded-lg bg-destructive/10 px-3 py-2 text-sm text-destructive">
              {error}
            </p>
          )}

          <div className="flex gap-3 pt-2">
            <button
              type="button"
              onClick={onClose}
              className="flex-1 rounded-lg border border-border py-2 text-sm font-medium text-muted-foreground hover:bg-muted"
            >
              Cancelar
            </button>
            <button
              type="submit"
              disabled={loading || !name.trim()}
              className="flex-1 rounded-lg bg-primary py-2 text-sm font-medium text-primary-foreground hover:bg-primary/90 disabled:opacity-50"
            >
              {loading ? "Creando..." : "Crear grupo"}
            </button>
          </div>
        </form>
      </div>
    </div>
  )
}

// ── Sidebar principal ─────────────────────────────────────────
export function GroupsSidebar({ selectedGroup, onSelectGroup }: GroupsSidebarProps) {
  const [groups, setGroups] = useState<Group[]>([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)
  const [showModal, setShowModal] = useState(false)

  useEffect(() => {
    getMyGroups()
      .then((data) => setGroups(data.map(toUiGroup)))
      .catch(() => setError("Error cargando grupos"))
      .finally(() => setLoading(false))
  }, [])

  const handleCreated = (group: Group) => {
    setGroups((prev) => [...prev, group])
    onSelectGroup(group)
  }

  const handleLogout = async () => {
    await logout()
    window.location.href = "/login"
  }

  return (
    <>
      {showModal && (
        <CreateGroupModal onClose={() => setShowModal(false)} onCreated={handleCreated} />
      )}

      <div className="flex w-[72px] flex-col items-center gap-2 bg-sidebar py-3">
        {/* Direct Messages */}
        <TooltipProvider delayDuration={0}>
          <Tooltip>
            <TooltipTrigger asChild>
              <Link
                href="/messages"
                className="flex h-12 w-12 items-center justify-center rounded-2xl bg-primary text-primary-foreground transition-all hover:rounded-xl hover:bg-accent"
              >
                <MessageCircle className="h-6 w-6" />
              </Link>
            </TooltipTrigger>
            <TooltipContent side="right" className="bg-card text-card-foreground border-border">
              <p>Direct Messages</p>
            </TooltipContent>
          </Tooltip>
        </TooltipProvider>

        <div className="mx-3 h-0.5 w-8 rounded-full bg-sidebar-border" />

        {/* Groups list */}
        <div className="flex flex-1 flex-col items-center gap-2 overflow-y-auto scrollbar-hide">
          {loading && (
            <div className="flex h-12 w-12 items-center justify-center text-xs text-muted-foreground">
              ...
            </div>
          )}
          {error && (
            <div className="flex h-12 w-12 items-center justify-center text-xs text-destructive">
              !
            </div>
          )}
          <TooltipProvider delayDuration={0}>
            {groups.map((group) => (
              <Tooltip key={group.id}>
                <TooltipTrigger asChild>
                  <button
                    onClick={() => onSelectGroup(group)}
                    className={cn(
                      "relative flex h-12 w-12 items-center justify-center rounded-2xl bg-sidebar-accent text-sidebar-foreground font-semibold text-sm transition-all hover:rounded-xl hover:bg-primary hover:text-primary-foreground",
                      selectedGroup?.id === group.id &&
                        "rounded-xl bg-primary text-primary-foreground"
                    )}
                  >
                    {group.avatar}
                    {group.unread > 0 && (
                      <span className="absolute -bottom-0.5 -right-0.5 flex h-5 w-5 items-center justify-center rounded-full bg-destructive text-[10px] font-bold text-destructive-foreground">
                        {group.unread > 9 ? "9+" : group.unread}
                      </span>
                    )}
                  </button>
                </TooltipTrigger>
                <TooltipContent side="right" className="bg-card text-card-foreground border-border">
                  <p>{group.name}</p>
                </TooltipContent>
              </Tooltip>
            ))}
          </TooltipProvider>
        </div>

        <div className="mx-3 h-0.5 w-8 rounded-full bg-sidebar-border" />

        {/* Crear grupo */}
        <TooltipProvider delayDuration={0}>
          <Tooltip>
            <TooltipTrigger asChild>
              <button
                onClick={() => setShowModal(true)}
                className="flex h-12 w-12 items-center justify-center rounded-2xl bg-sidebar-accent text-green-500 transition-all hover:rounded-xl hover:bg-green-500 hover:text-white"
              >
                <Plus className="h-6 w-6" />
              </button>
            </TooltipTrigger>
            <TooltipContent side="right" className="bg-card text-card-foreground border-border">
              <p>Create a Group</p>
            </TooltipContent>
          </Tooltip>
        </TooltipProvider>

        {/* Settings */}
        <TooltipProvider delayDuration={0}>
          <Tooltip>
            <TooltipTrigger asChild>
              <button className="flex h-12 w-12 items-center justify-center rounded-2xl bg-sidebar-accent text-sidebar-foreground transition-all hover:rounded-xl hover:bg-sidebar-primary hover:text-sidebar-primary-foreground">
                <Settings className="h-5 w-5" />
              </button>
            </TooltipTrigger>
            <TooltipContent side="right" className="bg-card text-card-foreground border-border">
              <p>Settings</p>
            </TooltipContent>
          </Tooltip>
        </TooltipProvider>

        {/* Logout */}
        <TooltipProvider delayDuration={0}>
          <Tooltip>
            <TooltipTrigger asChild>
              <button
                onClick={handleLogout}
                className="flex h-12 w-12 items-center justify-center rounded-2xl bg-sidebar-accent text-destructive transition-all hover:rounded-xl hover:bg-destructive hover:text-white"
              >
                <LogOut className="h-5 w-5" />
              </button>
            </TooltipTrigger>
            <TooltipContent side="right" className="bg-card text-card-foreground border-border">
              <p>Cerrar sesión</p>
            </TooltipContent>
          </Tooltip>
        </TooltipProvider>
      </div>
    </>
  )
}
