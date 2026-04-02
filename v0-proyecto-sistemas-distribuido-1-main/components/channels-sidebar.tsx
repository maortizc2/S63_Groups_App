"use client"

import { Hash, Megaphone, ChevronDown, Plus, Search, UserPlus } from "lucide-react"
import { cn } from "@/lib/utils"
import type { Channel } from "@/app/page"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuSeparator,
  DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu"

interface ChannelsSidebarProps {
  groupName: string
  channels: Channel[]
  selectedChannel: Channel
  onSelectChannel: (channel: Channel) => void
}

const channelIcons = {
  text: Hash,
  announcement: Megaphone,
}

export function ChannelsSidebar({
  groupName,
  channels,
  selectedChannel,
  onSelectChannel,
}: ChannelsSidebarProps) {
  const textChannels = channels.filter((c) => c.type === "text" || c.type === "announcement")

  return (
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
          <DropdownMenuItem className="cursor-pointer focus:bg-muted">
            <UserPlus className="mr-2 h-4 w-4" />
            Invite People
          </DropdownMenuItem>
          <DropdownMenuItem className="cursor-pointer focus:bg-muted">
            <Plus className="mr-2 h-4 w-4" />
            Create Channel
          </DropdownMenuItem>
          <DropdownMenuSeparator className="bg-border" />
          <DropdownMenuItem className="cursor-pointer focus:bg-muted">
            Group Settings
          </DropdownMenuItem>
        </DropdownMenuContent>
      </DropdownMenu>

      {/* Search */}
      <div className="p-2">
        <div className="relative">
          <Search className="absolute left-2.5 top-1/2 h-4 w-4 -translate-y-1/2 text-muted-foreground" />
          <Input
            placeholder="Search"
            className="h-8 bg-muted/50 pl-8 text-sm border-transparent focus:border-ring"
          />
        </div>
      </div>

      {/* Channels list */}
      <div className="flex-1 overflow-y-auto px-2 pb-2">
        {/* Text Channels */}
        <div className="mb-1">
          <div className="flex items-center justify-between px-1 py-1.5">
            <span className="text-xs font-semibold uppercase text-muted-foreground">
              Text Channels
            </span>
            <Button variant="ghost" size="icon" className="h-4 w-4 text-muted-foreground hover:text-foreground">
              <Plus className="h-3 w-3" />
            </Button>
          </div>
          {textChannels.map((channel) => {
            const Icon = channelIcons[channel.type]
            return (
              <button
                key={channel.id}
                onClick={() => onSelectChannel(channel)}
                className={cn(
                  "mb-0.5 flex w-full items-center gap-1.5 rounded-md px-2 py-1.5 text-sm text-muted-foreground transition-colors hover:bg-muted hover:text-foreground",
                  selectedChannel.id === channel.id &&
                    "bg-muted text-foreground font-medium"
                )}
              >
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
            YO
          </div>
          <span className="absolute -bottom-0.5 -right-0.5 h-3 w-3 rounded-full border-2 border-secondary bg-green-500" />
        </div>
        <div className="flex-1 min-w-0">
          <p className="truncate text-sm font-medium text-foreground">You</p>
          <p className="truncate text-xs text-muted-foreground">Online</p>
        </div>
      </div>
    </div>
  )
}
