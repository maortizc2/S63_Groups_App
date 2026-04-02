"use client"

import { useState } from "react"
import { Search, Plus, ArrowLeft, Users } from "lucide-react"
import Link from "next/link"
import { Input } from "@/components/ui/input"
import { Button } from "@/components/ui/button"
import { DirectMessageChat } from "@/components/direct-message-chat"
import {
  Tooltip,
  TooltipContent,
  TooltipProvider,
  TooltipTrigger,
} from "@/components/ui/tooltip"

type DirectMessage = {
  id: string
  participant: {
    id: string
    name: string
    avatar: string
    status: "online" | "offline" | "away" | "busy"
  }
  lastMessage: {
    content: string
    timestamp: Date
    isRead: boolean
    senderId: string
  }
}

type ChatMessage = {
  id: string
  content: string
  senderId: string
  timestamp: Date
  status: "sent" | "delivered" | "read"
  attachments?: {
    id: string
    name: string
    type: string
    url: string
  }[]
}

const mockDirectMessages: DirectMessage[] = [
  {
    id: "1",
    participant: { id: "1", name: "Carlos Martinez", avatar: "CM", status: "online" },
    lastMessage: {
      content: "Did you finish the Kafka implementation?",
      timestamp: new Date(Date.now() - 300000),
      isRead: false,
      senderId: "1",
    },
  },
  {
    id: "2",
    participant: { id: "2", name: "Sofia Rodriguez", avatar: "SR", status: "online" },
    lastMessage: {
      content: "Sure, I will send you the code review notes",
      timestamp: new Date(Date.now() - 1800000),
      isRead: true,
      senderId: "current",
    },
  },
  {
    id: "3",
    participant: { id: "3", name: "Miguel Torres", avatar: "MT", status: "away" },
    lastMessage: {
      content: "Let me know when the PR is ready",
      timestamp: new Date(Date.now() - 3600000),
      isRead: true,
      senderId: "3",
    },
  },
  {
    id: "4",
    participant: { id: "4", name: "Ana Garcia", avatar: "AG", status: "online" },
    lastMessage: {
      content: "Thanks for helping with the deployment!",
      timestamp: new Date(Date.now() - 7200000),
      isRead: true,
      senderId: "4",
    },
  },
  {
    id: "5",
    participant: { id: "5", name: "Luis Hernandez", avatar: "LH", status: "offline" },
    lastMessage: {
      content: "I will check the AWS configs tomorrow",
      timestamp: new Date(Date.now() - 86400000),
      isRead: true,
      senderId: "5",
    },
  },
]

const mockChatHistory: Record<string, ChatMessage[]> = {
  "1": [
    {
      id: "1",
      content: "Hey, how is the distributed system project going?",
      senderId: "1",
      timestamp: new Date(Date.now() - 3600000 * 2),
      status: "read",
    },
    {
      id: "2",
      content: "Going great! Just finished implementing the message queue.",
      senderId: "current",
      timestamp: new Date(Date.now() - 3600000),
      status: "read",
    },
    {
      id: "3",
      content: "Did you finish the Kafka implementation?",
      senderId: "1",
      timestamp: new Date(Date.now() - 300000),
      status: "read",
    },
  ],
  "2": [
    {
      id: "1",
      content: "Can you review my PR for the gRPC service?",
      senderId: "2",
      timestamp: new Date(Date.now() - 7200000),
      status: "read",
    },
    {
      id: "2",
      content: "Sure, I will send you the code review notes",
      senderId: "current",
      timestamp: new Date(Date.now() - 1800000),
      status: "delivered",
    },
  ],
}

const statusColors = {
  online: "bg-green-500",
  away: "bg-yellow-500",
  busy: "bg-red-500",
  offline: "bg-gray-400",
}

function formatTime(date: Date) {
  const now = new Date()
  const diff = now.getTime() - date.getTime()
  const hours = diff / (1000 * 60 * 60)

  if (hours < 24) {
    return date.toLocaleTimeString([], { hour: "2-digit", minute: "2-digit" })
  } else if (hours < 48) {
    return "Yesterday"
  } else {
    return date.toLocaleDateString([], { month: "short", day: "numeric" })
  }
}

export default function MessagesPage() {
  const [directMessages, setDirectMessages] = useState(mockDirectMessages)
  const [selectedChat, setSelectedChat] = useState<DirectMessage | null>(null)
  const [chatMessages, setChatMessages] = useState<Record<string, ChatMessage[]>>(mockChatHistory)
  const [searchQuery, setSearchQuery] = useState("")

  const filteredMessages = directMessages.filter((dm) =>
    dm.participant.name.toLowerCase().includes(searchQuery.toLowerCase())
  )

  const handleSendMessage = (content: string, attachments?: File[]) => {
    if (!selectedChat) return

    const newMessage: ChatMessage = {
      id: Date.now().toString(),
      content,
      senderId: "current",
      timestamp: new Date(),
      status: "sent",
      attachments: attachments?.map((file, i) => ({
        id: `att-${i}`,
        name: file.name,
        type: file.type,
        url: URL.createObjectURL(file),
      })),
    }

    setChatMessages((prev) => ({
      ...prev,
      [selectedChat.id]: [...(prev[selectedChat.id] || []), newMessage],
    }))

    // Update last message in conversation list
    setDirectMessages((prev) =>
      prev.map((dm) =>
        dm.id === selectedChat.id
          ? {
              ...dm,
              lastMessage: {
                content,
                timestamp: new Date(),
                isRead: true,
                senderId: "current",
              },
            }
          : dm
      )
    )

    // Simulate message delivery
    setTimeout(() => {
      setChatMessages((prev) => ({
        ...prev,
        [selectedChat.id]: prev[selectedChat.id].map((msg) =>
          msg.id === newMessage.id ? { ...msg, status: "delivered" } : msg
        ),
      }))
    }, 1000)

    // Simulate message read
    setTimeout(() => {
      setChatMessages((prev) => ({
        ...prev,
        [selectedChat.id]: prev[selectedChat.id].map((msg) =>
          msg.id === newMessage.id ? { ...msg, status: "read" } : msg
        ),
      }))
    }, 2500)
  }

  return (
    <div className="flex h-screen bg-background">
      {/* Sidebar */}
      <div className="flex w-[72px] flex-col items-center gap-2 bg-sidebar py-3">
        <TooltipProvider delayDuration={0}>
          <Tooltip>
            <TooltipTrigger asChild>
              <Link
                href="/"
                className="flex h-12 w-12 items-center justify-center rounded-2xl bg-sidebar-accent text-sidebar-foreground transition-all hover:rounded-xl hover:bg-sidebar-primary hover:text-sidebar-primary-foreground"
              >
                <ArrowLeft className="h-5 w-5" />
              </Link>
            </TooltipTrigger>
            <TooltipContent side="right" className="bg-card text-card-foreground border-border">
              <p>Back to Groups</p>
            </TooltipContent>
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
            <TooltipContent side="right" className="bg-card text-card-foreground border-border">
              <p>Direct Messages</p>
            </TooltipContent>
          </Tooltip>
        </TooltipProvider>
      </div>

      {/* Conversations list */}
      <div className="flex w-72 flex-col border-r border-border bg-card">
        <div className="flex h-14 items-center justify-between border-b border-border px-4">
          <h2 className="text-lg font-semibold text-card-foreground">Messages</h2>
          <Button variant="ghost" size="icon" className="h-8 w-8">
            <Plus className="h-4 w-4" />
          </Button>
        </div>

        <div className="p-3">
          <div className="relative">
            <Search className="absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-muted-foreground" />
            <Input
              placeholder="Search conversations..."
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              className="h-9 bg-muted/50 pl-9 border-transparent focus:border-ring"
            />
          </div>
        </div>

        <div className="flex-1 overflow-y-auto">
          {filteredMessages.map((dm) => (
            <button
              key={dm.id}
              onClick={() => setSelectedChat(dm)}
              className={`flex w-full items-center gap-3 px-3 py-3 transition-colors hover:bg-muted ${
                selectedChat?.id === dm.id ? "bg-muted" : ""
              }`}
            >
              <div className="relative shrink-0">
                <div className="flex h-10 w-10 items-center justify-center rounded-full bg-primary/20 text-sm font-semibold text-primary">
                  {dm.participant.avatar}
                </div>
                <span
                  className={`absolute -bottom-0.5 -right-0.5 h-3 w-3 rounded-full border-2 border-card ${
                    statusColors[dm.participant.status]
                  }`}
                />
              </div>
              <div className="flex-1 min-w-0 text-left">
                <div className="flex items-center justify-between gap-2">
                  <span className="truncate font-medium text-card-foreground">
                    {dm.participant.name}
                  </span>
                  <span className="shrink-0 text-xs text-muted-foreground">
                    {formatTime(dm.lastMessage.timestamp)}
                  </span>
                </div>
                <div className="flex items-center gap-1">
                  <p
                    className={`truncate text-sm ${
                      !dm.lastMessage.isRead && dm.lastMessage.senderId !== "current"
                        ? "font-medium text-card-foreground"
                        : "text-muted-foreground"
                    }`}
                  >
                    {dm.lastMessage.senderId === "current" && "You: "}
                    {dm.lastMessage.content}
                  </p>
                  {!dm.lastMessage.isRead && dm.lastMessage.senderId !== "current" && (
                    <span className="ml-auto shrink-0 h-2 w-2 rounded-full bg-primary" />
                  )}
                </div>
              </div>
            </button>
          ))}
        </div>
      </div>

      {/* Chat area */}
      {selectedChat ? (
        <DirectMessageChat
          participant={selectedChat.participant}
          messages={chatMessages[selectedChat.id] || []}
          onSendMessage={handleSendMessage}
        />
      ) : (
        <div className="flex flex-1 flex-col items-center justify-center bg-background">
          <div className="mb-4 flex h-20 w-20 items-center justify-center rounded-full bg-muted">
            <Users className="h-10 w-10 text-muted-foreground" />
          </div>
          <h3 className="mb-2 text-xl font-semibold text-foreground">Your Messages</h3>
          <p className="text-center text-muted-foreground max-w-xs">
            Select a conversation to start messaging or click the + button to start a new chat
          </p>
        </div>
      )}
    </div>
  )
}
