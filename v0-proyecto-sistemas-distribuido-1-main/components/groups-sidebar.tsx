"use client"

import { Plus, Settings, MessageCircle } from "lucide-react"
import { cn } from "@/lib/utils"
import Link from "next/link"
import type { Group } from "@/app/page"
import {
  Tooltip,
  TooltipContent,
  TooltipProvider,
  TooltipTrigger,
} from "@/components/ui/tooltip"

interface GroupsSidebarProps {
  groups: Group[]
  selectedGroup: Group
  onSelectGroup: (group: Group) => void
}

export function GroupsSidebar({
  groups,
  selectedGroup,
  onSelectGroup,
}: GroupsSidebarProps) {
  return (
    <div className="flex w-[72px] flex-col items-center gap-2 bg-sidebar py-3">
      {/* Home / DMs button */}
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
        <TooltipProvider delayDuration={0}>
          {groups.map((group) => (
            <Tooltip key={group.id}>
              <TooltipTrigger asChild>
                <button
                  onClick={() => onSelectGroup(group)}
                  className={cn(
                    "relative flex h-12 w-12 items-center justify-center rounded-2xl bg-sidebar-accent text-sidebar-foreground font-semibold text-sm transition-all hover:rounded-xl hover:bg-primary hover:text-primary-foreground",
                    selectedGroup.id === group.id &&
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

      {/* Add group button */}
      <TooltipProvider delayDuration={0}>
        <Tooltip>
          <TooltipTrigger asChild>
            <button className="flex h-12 w-12 items-center justify-center rounded-2xl bg-sidebar-accent text-green-500 transition-all hover:rounded-xl hover:bg-green-500 hover:text-white">
              <Plus className="h-6 w-6" />
            </button>
          </TooltipTrigger>
          <TooltipContent side="right" className="bg-card text-card-foreground border-border">
            <p>Create a Group</p>
          </TooltipContent>
        </Tooltip>
      </TooltipProvider>

      {/* Settings button */}
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
    </div>
  )
}
