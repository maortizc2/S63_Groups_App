"use client"

import { useState, useEffect, useRef } from "react"
import { useRouter } from "next/navigation"
import { GroupsSidebar } from "@/components/groups-sidebar"
import { ChannelsSidebar } from "@/components/channels-sidebar"
import { ChatArea } from "@/components/chat-area"
import { MembersSidebar } from "@/components/members-sidebar"
import { getToken } from "@/lib/api"
import { getChannelsByGroup, type ChannelDTO } from "@/lib/services/channels.service"
import { type GroupDTO } from "@/lib/services/groups.service"
import { getChannelHistory, sendMessage, type MessageDTO } from "@/lib/services/messages.service"
import { initStomp, subscribeToChannel, disconnectStomp } from "@/lib/services/websocket.service"

export type Group   = { id: string; name: string; avatar: string; unread: number }
export type Channel = { id: string; name: string; type: "text" | "announcement"; unread: number }
export type Message = {
  id: string; content: string
  sender: { id: string; name: string; avatar: string; status: "online"|"offline"|"away"|"busy" }
  timestamp: Date; status: "sent"|"delivered"|"read"
  attachments?: { id: string; name: string; type: string; url: string }[]
}
export type Member = {
  id: string; name: string; avatar: string
  status: "online"|"offline"|"away"|"busy"; role: "admin"|"moderator"|"member"
}

function toUiGroup(g: GroupDTO): Group {
  const w = g.name.trim().split(" ")
  const avatar = w.length >= 2 ? (w[0][0]+w[1][0]).toUpperCase() : g.name.substring(0,2).toUpperCase()
  return { id: String(g.id), name: g.name, avatar, unread: 0 }
}
function toUiChannel(c: ChannelDTO): Channel {
  return { id: String(c.id), name: c.name, type: "text", unread: 0 }
}
function toUiMessage(m: MessageDTO): Message {
  return {
    id: String(m.id), content: m.content,
    sender: {
      id: String(m.senderId), name: m.senderUsername,
      avatar: m.senderUsername.substring(0,2).toUpperCase(), status: "online",
    },
    timestamp: new Date(m.createdAt), status: "read",
  }
}

export default function Home() {
  const router = useRouter()
  const [selectedGroup,   setSelectedGroup]   = useState<Group | null>(null)
  const [selectedChannel, setSelectedChannel] = useState<Channel | null>(null)
  const [channels,        setChannels]        = useState<Channel[]>([])
  const [messages,        setMessages]        = useState<Message[]>([])
  const [showMembers,     setShowMembers]     = useState(true)
  const [loadingChannels, setLoadingChannels] = useState(false)
  const [loadingMessages, setLoadingMessages] = useState(false)
  const unsubscribeRef = useRef<(() => void) | null>(null)

  // Guard + iniciar STOMP una sola vez al montar
  useEffect(() => {
    if (!getToken()) { router.push("/login"); return }
    initStomp()
    return () => { disconnectStomp() }
  }, [router])

  // Canales al cambiar de grupo
  useEffect(() => {
    if (!selectedGroup) return
    setChannels([]); setSelectedChannel(null); setMessages([])
    setLoadingChannels(true)
    getChannelsByGroup(Number(selectedGroup.id))
      .then(d => setChannels(d.map(toUiChannel)))
      .catch(console.error)
      .finally(() => setLoadingChannels(false))
  }, [selectedGroup])

  // Historial + suscripción WS al cambiar de canal
  useEffect(() => {
    if (!selectedChannel) return

    // Cancelar suscripción anterior
    unsubscribeRef.current?.()

    setMessages([]); setLoadingMessages(true)

    getChannelHistory(Number(selectedChannel.id))
      .then(d => setMessages(d.map(toUiMessage)))
      .catch(console.error)
      .finally(() => setLoadingMessages(false))

    // Suscribir — síncrono, la conexión se encola si aún no está lista
    const unsub = subscribeToChannel(Number(selectedChannel.id), (msg: MessageDTO) => {
      setMessages(prev =>
        prev.find(m => m.id === String(msg.id)) ? prev : [...prev, toUiMessage(msg)]
      )
    })
    unsubscribeRef.current = unsub

    return () => { unsub(); unsubscribeRef.current = null }
  }, [selectedChannel])

  const handleSendMessage = async (content: string, attachments?: File[]) => {
    if (!content.trim() || !selectedChannel) return
    try {
      const sent = await sendMessage({ content, channelId: Number(selectedChannel.id) })
      setMessages(prev => [...prev, toUiMessage(sent)])
      if (attachments?.length) {
        const token = getToken()
        const base = (process.env.NEXT_PUBLIC_API_URL ?? "http://localhost:8080/api")
        for (const file of attachments) {
          const form = new FormData()
          form.append("file", file); form.append("channelId", selectedChannel.id)
          await fetch(`${base}/files/upload`, {
            method: "POST",
            headers: token ? { Authorization: `Bearer ${token}` } : {},
            body: form,
          })
        }
      }
    } catch (err) { console.error(err) }
  }

  if (!selectedGroup) return (
    <div className="flex h-screen bg-background">
      <GroupsSidebar groups={[]} selectedGroup={null as unknown as Group} onSelectGroup={setSelectedGroup} />
      <div className="flex flex-1 items-center justify-center">
        <p className="text-muted-foreground">Selecciona un grupo para comenzar</p>
      </div>
    </div>
  )

  return (
    <div className="flex h-screen bg-background">
      <GroupsSidebar groups={[]} selectedGroup={selectedGroup} onSelectGroup={setSelectedGroup} />
      <ChannelsSidebar
        groupId={Number(selectedGroup.id)}
        groupName={selectedGroup.name}
        channels={loadingChannels ? [] : channels}
        selectedChannel={selectedChannel ?? { id:"", name:"", type:"text", unread:0 }}
        onSelectChannel={setSelectedChannel}
        onChannelCreated={(ch) => { setChannels(p => [...p, ch]); setSelectedChannel(ch) }}
      />
      {selectedChannel ? (
        <ChatArea channel={selectedChannel} messages={loadingMessages ? [] : messages}
          onSendMessage={handleSendMessage}
          onToggleMembers={() => setShowMembers(!showMembers)}
          showMembers={showMembers} />
      ) : (
        <div className="flex flex-1 items-center justify-center">
          <p className="text-muted-foreground">Selecciona un canal</p>
        </div>
      )}
      {showMembers && selectedChannel && <MembersSidebar groupId={Number(selectedGroup.id)} />}
    </div>
  )
}
