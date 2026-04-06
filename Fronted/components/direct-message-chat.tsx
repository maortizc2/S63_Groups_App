"use client"

import { useState, useRef, useEffect } from "react"
import {
  Send,
  Paperclip,
  Smile,
  MoreVertical,
  Phone,
  Video,
  Check,
  CheckCheck,
} from "lucide-react"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu"

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

type Participant = {
  id: string
  name: string
  avatar: string
  status: "online" | "offline" | "away" | "busy"
}

interface DirectMessageChatProps {
  participant: Participant
  messages: ChatMessage[]
  onSendMessage: (content: string, attachments?: File[]) => void
}

const statusColors = {
  online: "bg-green-500",
  away: "bg-yellow-500",
  busy: "bg-red-500",
  offline: "bg-gray-400",
}

const statusText = {
  online: "Online",
  away: "Away",
  busy: "Do Not Disturb",
  offline: "Offline",
}

function formatTime(date: Date) {
  return date.toLocaleTimeString([], { hour: "2-digit", minute: "2-digit" })
}

function formatDate(date: Date) {
  const today = new Date()
  const yesterday = new Date(today)
  yesterday.setDate(yesterday.getDate() - 1)

  if (date.toDateString() === today.toDateString()) {
    return "Today"
  } else if (date.toDateString() === yesterday.toDateString()) {
    return "Yesterday"
  } else {
    return date.toLocaleDateString([], {
      weekday: "long",
      month: "long",
      day: "numeric",
    })
  }
}

function shouldShowDateSeparator(current: Date, previous: Date | null) {
  if (!previous) return true
  return current.toDateString() !== previous.toDateString()
}

export function DirectMessageChat({
  participant,
  messages,
  onSendMessage,
}: DirectMessageChatProps) {
  const [newMessage, setNewMessage] = useState("")
  const [attachments, setAttachments] = useState<File[]>([])
  const messagesEndRef = useRef<HTMLDivElement>(null)
  const fileInputRef = useRef<HTMLInputElement>(null)

  useEffect(() => {
    messagesEndRef.current?.scrollIntoView({ behavior: "smooth" })
  }, [messages])

  const handleSend = () => {
    if (newMessage.trim() || attachments.length > 0) {
      onSendMessage(newMessage, attachments.length > 0 ? attachments : undefined)
      setNewMessage("")
      setAttachments([])
    }
  }

  const handleKeyPress = (e: React.KeyboardEvent) => {
    if (e.key === "Enter" && !e.shiftKey) {
      e.preventDefault()
      handleSend()
    }
  }

  const handleFileSelect = (e: React.ChangeEvent<HTMLInputElement>) => {
    if (e.target.files) {
      setAttachments((prev) => [...prev, ...Array.from(e.target.files!)])
    }
  }

  const removeAttachment = (index: number) => {
    setAttachments((prev) => prev.filter((_, i) => i !== index))
  }

  return (
    <div className="flex flex-1 flex-col bg-background">
      {/* Header */}
      <div className="flex h-14 items-center justify-between border-b border-border bg-card px-4">
        <div className="flex items-center gap-3">
          <div className="relative">
            <div className="flex h-9 w-9 items-center justify-center rounded-full bg-primary/20 text-sm font-semibold text-primary">
              {participant.avatar}
            </div>
            <span
              className={`absolute -bottom-0.5 -right-0.5 h-2.5 w-2.5 rounded-full border-2 border-card ${
                statusColors[participant.status]
              }`}
            />
          </div>
          <div>
            <h3 className="font-semibold text-card-foreground">{participant.name}</h3>
            <p className="text-xs text-muted-foreground">{statusText[participant.status]}</p>
          </div>
        </div>
        <div className="flex items-center gap-1">
          <Button variant="ghost" size="icon" className="h-8 w-8 text-muted-foreground hover:text-foreground">
            <Phone className="h-4 w-4" />
          </Button>
          <Button variant="ghost" size="icon" className="h-8 w-8 text-muted-foreground hover:text-foreground">
            <Video className="h-4 w-4" />
          </Button>
          <DropdownMenu>
            <DropdownMenuTrigger asChild>
              <Button variant="ghost" size="icon" className="h-8 w-8 text-muted-foreground hover:text-foreground">
                <MoreVertical className="h-4 w-4" />
              </Button>
            </DropdownMenuTrigger>
            <DropdownMenuContent align="end" className="bg-card text-card-foreground border-border">
              <DropdownMenuItem className="cursor-pointer focus:bg-muted">View Profile</DropdownMenuItem>
              <DropdownMenuItem className="cursor-pointer focus:bg-muted">Search in Conversation</DropdownMenuItem>
              <DropdownMenuItem className="cursor-pointer focus:bg-muted text-destructive">Block User</DropdownMenuItem>
            </DropdownMenuContent>
          </DropdownMenu>
        </div>
      </div>

      {/* Messages */}
      <div className="flex-1 overflow-y-auto p-4">
        {messages.map((message, index) => {
          const isOwn = message.senderId === "current"
          const previousMessage = index > 0 ? messages[index - 1] : null
          const showDateSeparator = shouldShowDateSeparator(
            message.timestamp,
            previousMessage?.timestamp || null
          )

          return (
            <div key={message.id}>
              {showDateSeparator && (
                <div className="my-4 flex items-center gap-4">
                  <div className="h-px flex-1 bg-border" />
                  <span className="text-xs font-medium text-muted-foreground">
                    {formatDate(message.timestamp)}
                  </span>
                  <div className="h-px flex-1 bg-border" />
                </div>
              )}

              <div
                className={`mb-3 flex ${isOwn ? "justify-end" : "justify-start"}`}
              >
                <div
                  className={`max-w-[70%] rounded-2xl px-4 py-2 ${
                    isOwn
                      ? "bg-primary text-primary-foreground rounded-br-md"
                      : "bg-card text-card-foreground rounded-bl-md shadow-sm border border-border"
                  }`}
                >
                  <p className="text-sm whitespace-pre-wrap">{message.content}</p>
                  
                  {message.attachments && message.attachments.length > 0 && (
                    <div className="mt-2 space-y-1">
                      {message.attachments.map((attachment) => (
                        <div
                          key={attachment.id}
                          className={`flex items-center gap-2 rounded-lg p-2 ${
                            isOwn ? "bg-primary-foreground/10" : "bg-muted"
                          }`}
                        >
                          <Paperclip className="h-4 w-4" />
                          <span className="text-xs truncate">{attachment.name}</span>
                        </div>
                      ))}
                    </div>
                  )}

                  <div
                    className={`mt-1 flex items-center gap-1 text-[10px] ${
                      isOwn ? "justify-end text-primary-foreground/70" : "text-muted-foreground"
                    }`}
                  >
                    <span>{formatTime(message.timestamp)}</span>
                    {isOwn && (
                      <span className="ml-0.5">
                        {message.status === "sent" && <Check className="h-3 w-3" />}
                        {message.status === "delivered" && (
                          <CheckCheck className="h-3 w-3" />
                        )}
                        {message.status === "read" && (
                          <CheckCheck className="h-3 w-3 text-blue-300" />
                        )}
                      </span>
                    )}
                  </div>
                </div>
              </div>
            </div>
          )
        })}
        <div ref={messagesEndRef} />
      </div>

      {/* Attachments preview */}
      {attachments.length > 0 && (
        <div className="border-t border-border bg-card px-4 py-2">
          <div className="flex flex-wrap gap-2">
            {attachments.map((file, index) => (
              <div
                key={index}
                className="flex items-center gap-2 rounded-lg bg-muted px-3 py-1.5"
              >
                <Paperclip className="h-3 w-3 text-muted-foreground" />
                <span className="max-w-32 truncate text-xs text-foreground">
                  {file.name}
                </span>
                <button
                  onClick={() => removeAttachment(index)}
                  className="text-muted-foreground hover:text-foreground"
                >
                  &times;
                </button>
              </div>
            ))}
          </div>
        </div>
      )}

      {/* Message input */}
      <div className="border-t border-border bg-card p-4">
        <div className="flex items-center gap-2">
          <input
            type="file"
            ref={fileInputRef}
            onChange={handleFileSelect}
            className="hidden"
            multiple
          />
          <Button
            variant="ghost"
            size="icon"
            className="h-9 w-9 shrink-0 text-muted-foreground hover:text-foreground"
            onClick={() => fileInputRef.current?.click()}
          >
            <Paperclip className="h-5 w-5" />
          </Button>
          <Input
            value={newMessage}
            onChange={(e) => setNewMessage(e.target.value)}
            onKeyPress={handleKeyPress}
            placeholder={`Message ${participant.name}`}
            className="flex-1 border-transparent bg-muted focus:border-ring"
          />
          <Button
            variant="ghost"
            size="icon"
            className="h-9 w-9 shrink-0 text-muted-foreground hover:text-foreground"
          >
            <Smile className="h-5 w-5" />
          </Button>
          <Button
            size="icon"
            className="h-9 w-9 shrink-0"
            onClick={handleSend}
            disabled={!newMessage.trim() && attachments.length === 0}
          >
            <Send className="h-4 w-4" />
          </Button>
        </div>
      </div>
    </div>
  )
}
