"use client"

import { useState } from "react"
import { GroupsSidebar } from "@/components/groups-sidebar"
import { ChannelsSidebar } from "@/components/channels-sidebar"
import { ChatArea } from "@/components/chat-area"
import { MembersSidebar } from "@/components/members-sidebar"

export type Group = {
  id: string
  name: string
  avatar: string
  unread: number
}

export type Channel = {
  id: string
  name: string
  type: "text" | "announcement"
  unread: number
}

export type Message = {
  id: string
  content: string
  sender: {
    id: string
    name: string
    avatar: string
    status: "online" | "offline" | "away" | "busy"
  }
  timestamp: Date
  status: "sent" | "delivered" | "read"
  attachments?: {
    id: string
    name: string
    type: string
    url: string
  }[]
}

export type Member = {
  id: string
  name: string
  avatar: string
  status: "online" | "offline" | "away" | "busy"
  role: "admin" | "moderator" | "member"
}

const mockGroups: Group[] = [
  { id: "1", name: "Distributed Systems", avatar: "DS", unread: 5 },
  { id: "2", name: "Backend Team", avatar: "BT", unread: 0 },
  { id: "3", name: "Frontend Guild", avatar: "FG", unread: 12 },
  { id: "4", name: "DevOps", avatar: "DO", unread: 0 },
  { id: "5", name: "Project Alpha", avatar: "PA", unread: 3 },
]

const mockChannels: Channel[] = [
  { id: "1", name: "general", type: "text", unread: 2 },
  { id: "2", name: "announcements", type: "announcement", unread: 1 },
  { id: "3", name: "kafka-questions", type: "text", unread: 0 },
  { id: "4", name: "grpc-help", type: "text", unread: 0 },
  { id: "5", name: "code-review", type: "text", unread: 2 },
]

// Use fixed dates to avoid hydration mismatch
const today = new Date("2026-03-26T10:00:00")

const mockMessages: Message[] = [
  {
    id: "1",
    content: "Has anyone implemented the Kafka consumer for the message queue?",
    sender: { id: "1", name: "Carlos Martinez", avatar: "CM", status: "online" },
    timestamp: new Date("2026-03-26T08:00:00"),
    status: "read",
  },
  {
    id: "2",
    content: "Yes! I just pushed the code to the repo. Check the kafka-consumer branch.",
    sender: { id: "2", name: "Sofia Rodriguez", avatar: "SR", status: "online" },
    timestamp: new Date("2026-03-26T09:00:00"),
    status: "read",
    attachments: [
      { id: "1", name: "kafka-config.json", type: "application/json", url: "#" },
    ],
  },
  {
    id: "3",
    content: "Perfect! I will review it and test the partitioning strategy we discussed.",
    sender: { id: "3", name: "Miguel Torres", avatar: "MT", status: "away" },
    timestamp: new Date("2026-03-26T09:30:00"),
    status: "delivered",
  },
  {
    id: "4",
    content: "Remember we need to handle the replication factor for fault tolerance. The AWS deployment requires at least 3 replicas.",
    sender: { id: "1", name: "Carlos Martinez", avatar: "CM", status: "online" },
    timestamp: new Date("2026-03-26T09:45:00"),
    status: "read",
  },
  {
    id: "5",
    content: "Good point! I will update the configuration. Also, should we use gRPC or REST for the sync communication between services?",
    sender: { id: "2", name: "Sofia Rodriguez", avatar: "SR", status: "online" },
    timestamp: new Date("2026-03-26T09:55:00"),
    status: "sent",
  },
]

const mockMembers: Member[] = [
  { id: "1", name: "Carlos Martinez", avatar: "CM", status: "online", role: "admin" },
  { id: "2", name: "Sofia Rodriguez", avatar: "SR", status: "online", role: "moderator" },
  { id: "3", name: "Miguel Torres", avatar: "MT", status: "away", role: "member" },
  { id: "4", name: "Ana Garcia", avatar: "AG", status: "online", role: "member" },
  { id: "5", name: "Luis Hernandez", avatar: "LH", status: "offline", role: "member" },
  { id: "6", name: "Maria Lopez", avatar: "ML", status: "busy", role: "member" },
  { id: "7", name: "Diego Sanchez", avatar: "DS", status: "offline", role: "member" },
]

export default function Home() {
  const [selectedGroup, setSelectedGroup] = useState<Group>(mockGroups[0])
  const [selectedChannel, setSelectedChannel] = useState<Channel>(mockChannels[0])
  const [messages, setMessages] = useState<Message[]>(mockMessages)
  const [showMembers, setShowMembers] = useState(true)

  const handleSendMessage = (content: string, attachments?: File[]) => {
    const newMessage: Message = {
      id: Date.now().toString(),
      content,
      sender: { id: "current", name: "You", avatar: "YO", status: "online" },
      timestamp: new Date(),
      status: "sent",
      attachments: attachments?.map((file, i) => ({
        id: `att-${i}`,
        name: file.name,
        type: file.type,
        url: URL.createObjectURL(file),
      })),
    }
    setMessages([...messages, newMessage])
    
    // Simulate message delivery
    setTimeout(() => {
      setMessages((prev) =>
        prev.map((msg) =>
          msg.id === newMessage.id ? { ...msg, status: "delivered" } : msg
        )
      )
    }, 1000)
    
    // Simulate message read
    setTimeout(() => {
      setMessages((prev) =>
        prev.map((msg) =>
          msg.id === newMessage.id ? { ...msg, status: "read" } : msg
        )
      )
    }, 2500)
  }

  return (
    <div className="flex h-screen bg-background">
      <GroupsSidebar
        groups={mockGroups}
        selectedGroup={selectedGroup}
        onSelectGroup={setSelectedGroup}
      />
      <ChannelsSidebar
        groupName={selectedGroup.name}
        channels={mockChannels}
        selectedChannel={selectedChannel}
        onSelectChannel={setSelectedChannel}
      />
      <ChatArea
        channel={selectedChannel}
        messages={messages}
        onSendMessage={handleSendMessage}
        onToggleMembers={() => setShowMembers(!showMembers)}
        showMembers={showMembers}
      />
      {showMembers && <MembersSidebar members={mockMembers} />}
    </div>
  )
}
