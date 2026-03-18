package com.groupsapp.monolito.dto.message;

import com.groupsapp.monolito.model.Message;

public class SendMessageRequest {
    private String content;
    private Message.MessageType type = Message.MessageType.TEXT;
    private Long channelId;
    private Long receiverId;
    private Long fileId;

    // Getters & Setters
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public Message.MessageType getType() { return type; }
    public void setType(Message.MessageType type) { this.type = type; }

    public Long getChannelId() { return channelId; }
    public void setChannelId(Long channelId) { this.channelId = channelId; }

    public Long getReceiverId() { return receiverId; }
    public void setReceiverId(Long receiverId) { this.receiverId = receiverId; }

    public Long getFileId() { return fileId; }
    public void setFileId(Long fileId) { this.fileId = fileId; }
}
