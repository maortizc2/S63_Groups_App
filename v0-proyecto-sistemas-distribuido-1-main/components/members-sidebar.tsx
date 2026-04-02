"use client"

import { cn } from "@/lib/utils"
import type { Member } from "@/app/page"
import { Crown, Shield } from "lucide-react"

interface MembersSidebarProps {
  members: Member[]
}

const statusColors = {
  online: "bg-green-500",
  offline: "bg-muted-foreground",
  away: "bg-yellow-500",
  busy: "bg-destructive",
}

const statusLabels = {
  online: "Online",
  offline: "Offline",
  away: "Away",
  busy: "Do Not Disturb",
}

export function MembersSidebar({ members }: MembersSidebarProps) {
  const onlineMembers = members.filter((m) => m.status !== "offline")
  const offlineMembers = members.filter((m) => m.status === "offline")

  const MemberItem = ({ member }: { member: Member }) => (
    <button
      className="flex w-full items-center gap-2 rounded-md px-2 py-1.5 text-sm transition-colors hover:bg-muted group"
    >
      <div className="relative">
        <div
          className={cn(
            "flex h-8 w-8 items-center justify-center rounded-full text-xs font-semibold",
            member.status === "online" && "bg-primary text-primary-foreground",
            member.status === "offline" && "bg-muted text-muted-foreground",
            member.status === "away" && "bg-yellow-500/20 text-yellow-600",
            member.status === "busy" && "bg-destructive/20 text-destructive"
          )}
        >
          {member.avatar}
        </div>
        <span
          className={cn(
            "absolute -bottom-0.5 -right-0.5 h-3 w-3 rounded-full border-2 border-secondary",
            statusColors[member.status]
          )}
        />
      </div>
      <div className="flex flex-1 items-center gap-1 min-w-0">
        <span
          className={cn(
            "truncate",
            member.status === "offline" ? "text-muted-foreground" : "text-foreground"
          )}
        >
          {member.name}
        </span>
        {member.role === "admin" && (
          <Crown className="h-3 w-3 shrink-0 text-yellow-500" />
        )}
        {member.role === "moderator" && (
          <Shield className="h-3 w-3 shrink-0 text-primary" />
        )}
      </div>
    </button>
  )

  return (
    <div className="w-60 border-l border-border bg-secondary/30">
      <div className="p-3">
        <h3 className="mb-2 px-2 text-xs font-semibold uppercase text-muted-foreground">
          Online — {onlineMembers.length}
        </h3>
        <div className="space-y-0.5">
          {onlineMembers.map((member) => (
            <MemberItem key={member.id} member={member} />
          ))}
        </div>

        {offlineMembers.length > 0 && (
          <>
            <h3 className="mb-2 mt-4 px-2 text-xs font-semibold uppercase text-muted-foreground">
              Offline — {offlineMembers.length}
            </h3>
            <div className="space-y-0.5">
              {offlineMembers.map((member) => (
                <MemberItem key={member.id} member={member} />
              ))}
            </div>
          </>
        )}
      </div>
    </div>
  )
}
