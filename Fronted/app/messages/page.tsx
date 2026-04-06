"use client"

import { useEffect, useState, useRef } from "react"
import { Search, Plus, ArrowLeft, Users, X, Send, Paperclip, Smile, Loader2 } from "lucide-react"
import Link from "next/link"
import { Input } from "@/components/ui/input"
import { Button } from "@/components/ui/button"
import { getDirectHistory, sendMessage, type MessageDTO } from "@/lib/services/messages.service"
import { searchUsers, type UserSearchDTO } from "@/lib/services/groups.service"
import { useCurrentUser } from "@/hooks/use-current-user"
import { Tooltip, TooltipContent, TooltipProvider, TooltipTrigger } from "@/components/ui/tooltip"

type Participant  = { userId: number; username: string }
type Conversation = { participant: Participant; lastMessage: string; lastTime: Date | null; unread: boolean }
type UiMessage    = { id: string; content: string; isOwn: boolean; timestamp: Date }

// ── Modal buscar usuario ──────────────────────────────────────
function NewDMModal({ onClose, onOpen }: {
  onClose: () => void
  onOpen: (p: Participant) => void
}) {
  const [query, setQuery]         = useState("")
  const [results, setResults]     = useState<UserSearchDTO[]>([])
  const [searching, setSearching] = useState(false)

  useEffect(() => {
    if (!query.trim() || query.length < 2) { setResults([]); return }
    setSearching(true)
    const t = setTimeout(() => {
      searchUsers(query).then(setResults).catch(() => setResults([])).finally(() => setSearching(false))
    }, 400)
    return () => clearTimeout(t)
  }, [query])

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/60">
      <div className="w-full max-w-sm rounded-xl bg-card border border-border p-6 shadow-2xl">
        <div className="mb-4 flex items-center justify-between">
          <h2 className="text-lg font-semibold text-card-foreground">Nuevo mensaje directo</h2>
          <button onClick={onClose} className="text-muted-foreground hover:text-foreground"><X className="h-5 w-5" /></button>
        </div>
        <div className="relative mb-3">
          <Search className="absolute left-3 top-1/2 -translate-y-1/2 h-4 w-4 text-muted-foreground" />
          <input autoFocus value={query} onChange={e => setQuery(e.target.value)}
            placeholder="Buscar por nombre o email..."
            className="w-full rounded-lg border border-border bg-background py-2 pl-9 pr-3 text-sm text-foreground focus:outline-none focus:ring-2 focus:ring-primary" />
          {searching && <Loader2 className="absolute right-3 top-1/2 -translate-y-1/2 h-4 w-4 animate-spin text-muted-foreground" />}
        </div>
        <div className="max-h-64 overflow-y-auto space-y-1">
          {query.length < 2 && <p className="py-4 text-center text-sm text-muted-foreground">Escribe al menos 2 caracteres</p>}
          {results.length === 0 && query.length >= 2 && !searching && (
            <p className="py-4 text-center text-sm text-muted-foreground">No se encontraron usuarios</p>
          )}
          {results.map(u => (
            <button key={u.userId}
              onClick={() => { onOpen({ userId: u.userId, username: u.username }); onClose() }}
              className="flex w-full items-center gap-3 rounded-lg p-2 hover:bg-muted text-left">
              <div className="flex h-10 w-10 shrink-0 items-center justify-center rounded-full bg-primary/20 text-sm font-semibold text-primary">
                {u.username.substring(0,2).toUpperCase()}
              </div>
              <div className="flex-1 min-w-0">
                <p className="truncate text-sm font-medium text-foreground">{u.username}</p>
                <p className="truncate text-xs text-muted-foreground">{u.email}</p>
              </div>
              <span className={`h-2 w-2 rounded-full shrink-0 ${u.status === "ONLINE" ? "bg-green-500" : "bg-muted-foreground"}`} />
            </button>
          ))}
        </div>
      </div>
    </div>
  )
}

// ── Chat directo ──────────────────────────────────────────────
function DirectChat({ participant, currentUserId, onNewMessage, incomingMessage, onClose }: {
  participant: Participant; currentUserId: number
  onNewMessage: (c: string) => void
  incomingMessage: MessageDTO | null
  onClose: () => void   // ← botón cerrar
}) {
  const [messages, setMessages] = useState<UiMessage[]>([])
  const [loading, setLoading]   = useState(true)
  const [text, setText]         = useState("")
  const [sending, setSending]   = useState(false)
  const bottomRef = useRef<HTMLDivElement>(null)

  useEffect(() => {
    if (!currentUserId) return
    setLoading(true); setMessages([])
    getDirectHistory(participant.userId)
      .then(data => setMessages(data.map(m => ({
        id: String(m.id), content: m.content,
        isOwn: m.senderId === currentUserId,
        timestamp: new Date(m.createdAt),
      }))))
      .catch(console.error)
      .finally(() => setLoading(false))
  }, [participant.userId, currentUserId])

  // Recibir mensajes entrantes por WebSocket
  useEffect(() => {
    if (!incomingMessage) return
    if (incomingMessage.senderId !== participant.userId) return
    setMessages(prev =>
      prev.find(m => m.id === String(incomingMessage.id)) ? prev : [...prev, {
        id: String(incomingMessage.id), content: incomingMessage.content,
        isOwn: false, timestamp: new Date(incomingMessage.createdAt),
      }]
    )
  }, [incomingMessage, participant.userId])

  useEffect(() => { bottomRef.current?.scrollIntoView({ behavior: "smooth" }) }, [messages])

  const handleSend = async (e: React.FormEvent) => {
    e.preventDefault()
    if (!text.trim() || sending) return
    setSending(true)
    try {
      const sent = await sendMessage({ content: text, receiverId: participant.userId })
      // FIX duplicado: agregar solo si el ID no existe ya en la lista
      // (el WebSocket podría haberlo agregado antes por el broadcast del backend)
      setMessages(prev =>
        prev.find(m => m.id === String(sent.id)) ? prev : [...prev, {
          id: String(sent.id), content: sent.content,
          isOwn: true, timestamp: new Date(sent.createdAt),
        }]
      )
      onNewMessage(text)
      setText("")
    } catch (err) { console.error(err) }
    finally { setSending(false) }
  }

  return (
    <div className="flex flex-1 flex-col bg-background">
      {/* Header con botón cerrar */}
      <div className="flex h-14 items-center justify-between border-b border-border bg-card px-4">
        <div className="flex items-center gap-3">
          <div className="flex h-9 w-9 items-center justify-center rounded-full bg-primary/20 text-sm font-semibold text-primary">
            {participant.username.substring(0,2).toUpperCase()}
          </div>
          <div>
            <h3 className="font-semibold text-card-foreground">{participant.username}</h3>
            <p className="text-xs text-muted-foreground">Mensaje directo</p>
          </div>
        </div>
        {/* Botón cerrar chat */}
        <button
          onClick={onClose}
          className="flex h-8 w-8 items-center justify-center rounded-lg text-muted-foreground hover:bg-muted hover:text-foreground transition-colors"
          title="Cerrar conversación"
        >
          <X className="h-4 w-4" />
        </button>
      </div>

      <div className="flex-1 overflow-y-auto p-4 space-y-3">
        {loading && <p className="text-center text-sm text-muted-foreground">Cargando historial...</p>}
        {!loading && messages.length === 0 && <p className="text-center text-sm text-muted-foreground">Sin mensajes aún.</p>}
        {messages.map(msg => (
          <div key={msg.id} className={`flex ${msg.isOwn ? "justify-end" : "justify-start"}`}>
            <div className={`max-w-[70%] rounded-2xl px-4 py-2 text-sm ${
              msg.isOwn
                ? "bg-primary text-primary-foreground rounded-br-md"
                : "bg-card text-card-foreground rounded-bl-md shadow-sm border border-border"
            }`}>
              <p className="whitespace-pre-wrap">{msg.content}</p>
              <p className={`mt-1 text-[10px] ${msg.isOwn ? "text-primary-foreground/70 text-right" : "text-muted-foreground"}`}>
                {msg.timestamp.toLocaleTimeString([], { hour: "2-digit", minute: "2-digit" })}
              </p>
            </div>
          </div>
        ))}
        <div ref={bottomRef} />
      </div>

      <div className="border-t border-border bg-card p-4">
        <form onSubmit={handleSend} className="flex items-center gap-2">
          <Button type="button" variant="ghost" size="icon" className="h-9 w-9 shrink-0 text-muted-foreground">
            <Paperclip className="h-5 w-5" />
          </Button>
          <Input value={text} onChange={e => setText(e.target.value)}
            placeholder={`Mensaje a ${participant.username}`}
            className="flex-1 border-transparent bg-muted focus:border-ring" />
          <Button type="button" variant="ghost" size="icon" className="h-9 w-9 shrink-0 text-muted-foreground">
            <Smile className="h-5 w-5" />
          </Button>
          <Button type="submit" size="icon" className="h-9 w-9 shrink-0" disabled={!text.trim() || sending}>
            <Send className="h-4 w-4" />
          </Button>
        </form>
      </div>
    </div>
  )
}

// ── Página principal ──────────────────────────────────────────
export default function MessagesPage() {
  const currentUser   = useCurrentUser()
  const currentUserId = currentUser?.userId ?? 0

  const [conversations,       setConversations]       = useState<Conversation[]>([])
  const [selectedParticipant, setSelectedParticipant] = useState<Participant | null>(null)
  const [searchQuery,         setSearchQuery]         = useState("")
  const [showNewDM,           setShowNewDM]           = useState(false)
  const [incomingDM,          setIncomingDM]          = useState<MessageDTO | null>(null)

  // Escuchar el evento global de DMs emitido por StompProvider
  useEffect(() => {
    const handler = (e: Event) => {
      const msg = (e as CustomEvent<MessageDTO>).detail
      setIncomingDM(msg)
      setConversations(prev => {
        const exists = prev.find(c => c.participant.userId === msg.senderId)
        if (exists) return prev.map(c =>
          c.participant.userId === msg.senderId
            ? { ...c, lastMessage: msg.content, lastTime: new Date(msg.createdAt), unread: true }
            : c
        )
        return [{
          participant: { userId: msg.senderId, username: msg.senderUsername },
          lastMessage: msg.content, lastTime: new Date(msg.createdAt), unread: true,
        }, ...prev]
      })
    }
    window.addEventListener("dm:received", handler)
    return () => window.removeEventListener("dm:received", handler)
  }, [])

  const openConversation = (participant: Participant) => {
    setConversations(prev => {
      if (prev.find(c => c.participant.userId === participant.userId)) return prev
      return [{ participant, lastMessage: "", lastTime: null, unread: false }, ...prev]
    })
    setSelectedParticipant(participant)
  }

  const filtered = conversations.filter(c =>
    c.participant.username.toLowerCase().includes(searchQuery.toLowerCase())
  )

  return (
    <>
      {showNewDM && <NewDMModal onClose={() => setShowNewDM(false)} onOpen={openConversation} />}

      <div className="flex h-screen bg-background">
        {/* Sidebar de iconos */}
        <div className="flex w-[72px] flex-col items-center gap-2 bg-sidebar py-3">
          <TooltipProvider delayDuration={0}>
            <Tooltip>
              <TooltipTrigger asChild>
                <Link href="/" className="flex h-12 w-12 items-center justify-center rounded-2xl bg-sidebar-accent text-sidebar-foreground transition-all hover:rounded-xl hover:bg-sidebar-primary hover:text-sidebar-primary-foreground">
                  <ArrowLeft className="h-5 w-5" />
                </Link>
              </TooltipTrigger>
              <TooltipContent side="right" className="bg-card text-card-foreground border-border"><p>Volver a grupos</p></TooltipContent>
            </Tooltip>
          </TooltipProvider>

          <div className="mx-3 h-0.5 w-8 rounded-full bg-sidebar-border" />

          <TooltipProvider delayDuration={0}>
            <Tooltip>
              <TooltipTrigger asChild>
                <div className="flex h-12 w-12 items-center justify-center rounded-xl bg-primary text-primary-foreground">
                  <Users className="h-6 w-6" />
                </div>
              </TooltipTrigger>
              <TooltipContent side="right" className="bg-card text-card-foreground border-border"><p>Mensajes directos</p></TooltipContent>
            </Tooltip>
          </TooltipProvider>

          <div className="mt-auto">
            <TooltipProvider delayDuration={0}>
              <Tooltip>
                <TooltipTrigger asChild>
                  <div className="flex h-12 w-12 items-center justify-center rounded-2xl bg-sidebar-accent">
                    <div className="flex h-9 w-9 items-center justify-center rounded-full bg-primary text-xs font-bold text-primary-foreground">
                      {currentUser?.username?.substring(0,2).toUpperCase() ?? ""}
                    </div>
                  </div>
                </TooltipTrigger>
                <TooltipContent side="right" className="bg-card text-card-foreground border-border">
                  <p>{currentUser?.username ?? "Cargando..."}</p>
                </TooltipContent>
              </Tooltip>
            </TooltipProvider>
          </div>
        </div>

        {/* Lista de conversaciones */}
        <div className="flex w-72 flex-col border-r border-border bg-card">
          <div className="flex h-14 items-center justify-between border-b border-border px-4">
            <h2 className="text-lg font-semibold text-card-foreground">Mensajes</h2>
            <Button variant="ghost" size="icon" className="h-8 w-8" onClick={() => setShowNewDM(true)}>
              <Plus className="h-4 w-4" />
            </Button>
          </div>
          <div className="p-3">
            <div className="relative">
              <Search className="absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-muted-foreground" />
              <Input placeholder="Buscar..." value={searchQuery} onChange={e => setSearchQuery(e.target.value)}
                className="h-9 bg-muted/50 pl-9 border-transparent focus:border-ring" />
            </div>
          </div>
          <div className="flex-1 overflow-y-auto">
            {filtered.length === 0 && (
              <p className="px-4 py-6 text-center text-sm text-muted-foreground">
                Sin conversaciones.{" "}
                <button className="text-primary hover:underline" onClick={() => setShowNewDM(true)}>Iniciar una</button>
              </p>
            )}
            {filtered.map(conv => {
              const initials   = conv.participant.username.substring(0,2).toUpperCase()
              const isSelected = selectedParticipant?.userId === conv.participant.userId
              return (
                <button key={conv.participant.userId}
                  onClick={() => {
                    setSelectedParticipant(conv.participant)
                    setConversations(prev => prev.map(c =>
                      c.participant.userId === conv.participant.userId ? { ...c, unread: false } : c))
                  }}
                  className={`flex w-full items-center gap-3 px-3 py-3 transition-colors hover:bg-muted ${isSelected ? "bg-muted" : ""}`}>
                  <div className="relative shrink-0">
                    <div className="flex h-10 w-10 items-center justify-center rounded-full bg-primary/20 text-sm font-semibold text-primary">{initials}</div>
                    {conv.unread && <span className="absolute -top-0.5 -right-0.5 h-3 w-3 rounded-full border-2 border-card bg-primary" />}
                  </div>
                  <div className="flex-1 min-w-0 text-left">
                    <p className={`truncate text-sm font-medium ${conv.unread ? "text-foreground" : "text-card-foreground"}`}>{conv.participant.username}</p>
                    <p className={`truncate text-xs ${conv.unread ? "font-medium text-foreground" : "text-muted-foreground"}`}>{conv.lastMessage || "Iniciar conversación"}</p>
                  </div>
                </button>
              )
            })}
          </div>
        </div>

        {/* Área de chat */}
        {selectedParticipant && currentUserId ? (
          <DirectChat
            participant={selectedParticipant}
            currentUserId={currentUserId}
            incomingMessage={incomingDM}
            onClose={() => setSelectedParticipant(null)}
            onNewMessage={content => setConversations(prev => prev.map(c =>
              c.participant.userId === selectedParticipant.userId
                ? { ...c, lastMessage: content, lastTime: new Date(), unread: false } : c
            ))}
          />
        ) : selectedParticipant && !currentUserId ? (
          <div className="flex flex-1 items-center justify-center">
            <p className="text-sm text-muted-foreground">Cargando...</p>
          </div>
        ) : (
          <div className="flex flex-1 flex-col items-center justify-center bg-background">
            <div className="mb-4 flex h-20 w-20 items-center justify-center rounded-full bg-muted">
              <Users className="h-10 w-10 text-muted-foreground" />
            </div>
            <h3 className="mb-2 text-xl font-semibold text-foreground">Tus mensajes</h3>
            <p className="text-center text-muted-foreground max-w-xs">
              Haz clic en{" "}
              <button className="text-primary hover:underline" onClick={() => setShowNewDM(true)}>+</button>
              {" "}para iniciar un chat.
            </p>
          </div>
        )}
      </div>
    </>
  )
}
