"use client"

import { useState, useRef, useEffect } from "react"
import {
  Hash,
  Users,
  Pin,
  Bell,
  Search,
  Paperclip,
  Send,
  Smile,
  Check,
  CheckCheck,
  FileText,
  X,
} from "lucide-react"
import { cn } from "@/lib/utils"
import type { Channel, Message } from "@/app/page"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"

interface ChatAreaProps {
  channel: Channel
  messages: Message[]
  onSendMessage: (content: string, attachments?: File[]) => void
  onToggleMembers: () => void
  showMembers: boolean
}

function MessageStatus({ status }: { status: Message["status"] }) {
  if (status === "sent") {
    return <Check className="h-3 w-3 text-muted-foreground" />
  }
  if (status === "delivered") {
    return <CheckCheck className="h-3 w-3 text-muted-foreground" />
  }
  return <CheckCheck className="h-3 w-3 text-primary" />
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
  }
  if (date.toDateString() === yesterday.toDateString()) {
    return "Yesterday"
  }
  return date.toLocaleDateString([], {
    weekday: "long",
    month: "long",
    day: "numeric",
  })
}

export function ChatArea({
  channel,
  messages,
  onSendMessage,
  onToggleMembers,
  showMembers,
}: ChatAreaProps) {
  const [inputValue, setInputValue] = useState("")
  const [attachments, setAttachments] = useState<File[]>([])
  const messagesEndRef = useRef<HTMLDivElement>(null)
  const fileInputRef = useRef<HTMLInputElement>(null)

  useEffect(() => {
    messagesEndRef.current?.scrollIntoView({ behavior: "smooth" })
  }, [messages])

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault()
    if (inputValue.trim() || attachments.length > 0) {
      onSendMessage(inputValue.trim(), attachments.length > 0 ? attachments : undefined)
      setInputValue("")
      setAttachments([])
    }
  }

  const handleFileSelect = (e: React.ChangeEvent<HTMLInputElement>) => {
    if (e.target.files) {
      setAttachments([...attachments, ...Array.from(e.target.files)])
    }
  }

  const removeAttachment = (index: number) => {
    setAttachments(attachments.filter((_, i) => i !== index))
  }

  // Group messages by date
  const messagesByDate = messages.reduce(
    (acc, message) => {
      const dateKey = message.timestamp.toDateString()
      if (!acc[dateKey]) {
        acc[dateKey] = []
      }
      acc[dateKey].push(message)
      return acc
    },
    {} as Record<string, Message[]>
  )

  return (
    <div className="flex flex-1 flex-col bg-background">
      {/* Header */}
      <div className="flex h-12 items-center justify-between border-b border-border px-4 shadow-sm">
        <div className="flex items-center gap-2">
          <Hash className="h-5 w-5 text-muted-foreground" />
          <span className="font-semibold text-foreground">{channel.name}</span>
        </div>
        <div className="flex items-center gap-1">
          <Button variant="ghost" size="icon" className="h-8 w-8 text-muted-foreground hover:text-foreground">
            <Pin className="h-4 w-4" />
          </Button>
          <Button variant="ghost" size="icon" className="h-8 w-8 text-muted-foreground hover:text-foreground">
            <Bell className="h-4 w-4" />
          </Button>
          <Button
            variant="ghost"
            size="icon"
            className={cn(
              "h-8 w-8 text-muted-foreground hover:text-foreground",
              showMembers && "bg-muted text-foreground"
            )}
            onClick={onToggleMembers}
          >
            <Users className="h-4 w-4" />
          </Button>
          <div className="ml-2 w-40">
            <div className="relative">
              <Search className="absolute left-2 top-1/2 h-4 w-4 -translate-y-1/2 text-muted-foreground" />
              <Input
                placeholder="Search"
                className="h-7 bg-muted/50 pl-7 text-sm border-transparent focus:border-ring"
              />
            </div>
          </div>
        </div>
      </div>

      {/* Messages */}
      <div className="flex-1 overflow-y-auto p-4">
        {Object.entries(messagesByDate).map(([dateKey, dateMessages]) => (
          <div key={dateKey}>
            {/* Date separator */}
            <div className="relative my-4 flex items-center">
              <div className="flex-1 border-t border-border" />
              <span className="mx-4 text-xs font-medium text-muted-foreground" suppressHydrationWarning>
                {formatDate(new Date(dateKey))}
              </span>
              <div className="flex-1 border-t border-border" />
            </div>

            {/* Messages for this date */}
            {dateMessages.map((message) => (
              <div key={message.id} className="group mb-4 flex gap-3">
                {/* Avatar */}
                <div
                  className={cn(
                    "flex h-10 w-10 shrink-0 items-center justify-center rounded-full text-sm font-semibold",
                    message.sender.status === "online" && "bg-primary text-primary-foreground",
                    message.sender.status === "offline" && "bg-muted text-muted-foreground",
                    message.sender.status === "away" && "bg-yellow-500 text-white",
                    message.sender.status === "busy" && "bg-destructive text-destructive-foreground"
                  )}
                >
                  {message.sender.avatar}
                </div>

                {/* Message content */}
                <div className="min-w-0 flex-1">
                  <div className="flex items-baseline gap-2">
                    <span className="font-semibold text-foreground">
                      {message.sender.name}
                    </span>
                    <span className="text-xs text-muted-foreground" suppressHydrationWarning>
                      {formatTime(message.timestamp)}
                    </span>
                    <MessageStatus status={message.status} />
                  </div>
                  <p className="mt-0.5 text-foreground leading-relaxed">{message.content}</p>

                  {/* Attachments */}
                  {message.attachments && message.attachments.length > 0 && (
                    <div className="mt-2 flex flex-wrap gap-2">
                      {message.attachments.map((attachment) => (
                        <div
                          key={attachment.id}
                          className="flex items-center gap-2 rounded-lg bg-card border border-border px-3 py-2"
                        >
                          <FileText className="h-4 w-4 text-primary" />
                          <span className="text-sm text-foreground">
                            {attachment.name}
                          </span>
                        </div>
                      ))}
                    </div>
                  )}
                </div>
              </div>
            ))}
          </div>
        ))}
        <div ref={messagesEndRef} />
      </div>

      {/* Input area */}
      <div className="border-t border-border p-4">
        {/* Attachment preview */}
        {attachments.length > 0 && (
          <div className="mb-2 flex flex-wrap gap-2">
            {attachments.map((file, index) => (
              <div
                key={index}
                className="flex items-center gap-2 rounded-lg bg-card border border-border px-3 py-1.5"
              >
                <FileText className="h-4 w-4 text-primary" />
                <span className="text-sm text-foreground">{file.name}</span>
                <button
                  onClick={() => removeAttachment(index)}
                  className="rounded-full p-0.5 hover:bg-muted"
                >
                  <X className="h-3 w-3 text-muted-foreground" />
                </button>
              </div>
            ))}
          </div>
        )}

        <form onSubmit={handleSubmit} className="flex items-center gap-2">
          <div className="relative flex-1">
            <input
              type="file"
              ref={fileInputRef}
              onChange={handleFileSelect}
              className="hidden"
              multiple
            />
            <Button
              type="button"
              variant="ghost"
              size="icon"
              className="absolute left-2 top-1/2 h-8 w-8 -translate-y-1/2 text-muted-foreground hover:text-foreground"
              onClick={() => fileInputRef.current?.click()}
            >
              <Paperclip className="h-5 w-5" />
            </Button>
            <Input
              value={inputValue}
              onChange={(e) => setInputValue(e.target.value)}
              placeholder={`Message #${channel.name}`}
              className="h-11 bg-card border-border pl-12 pr-20 text-foreground placeholder:text-muted-foreground"
            />
            <div className="absolute right-2 top-1/2 flex -translate-y-1/2 items-center gap-1">
              <Button
                type="button"
                variant="ghost"
                size="icon"
                className="h-8 w-8 text-muted-foreground hover:text-foreground"
              >
                <Smile className="h-5 w-5" />
              </Button>
            </div>
          </div>
          <Button
            type="submit"
            size="icon"
            className="h-11 w-11 bg-primary text-primary-foreground hover:bg-accent"
            disabled={!inputValue.trim() && attachments.length === 0}
          >
            <Send className="h-5 w-5" />
          </Button>
        </form>
      </div>
    </div>
  )
}
