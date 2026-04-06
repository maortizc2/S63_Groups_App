package com.groupsapp.monolito.controller;

import com.groupsapp.monolito.dto.ApiResponse;
import com.groupsapp.monolito.dto.group.CreateChannelRequest;
import com.groupsapp.monolito.model.Channel;
import com.groupsapp.monolito.service.ChannelService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/groups/{groupId}/channels")
public class ChannelController {

    private final ChannelService channelService;

    public ChannelController(ChannelService channelService) {
        this.channelService = channelService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Channel>>> getChannels(
            @PathVariable Long groupId,
            @AuthenticationPrincipal UserDetails userDetails) {
        List<Channel> channels = channelService.getChannelsByGroup(groupId, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.ok("Canales obtenidos", channels));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Channel>> createChannel(
            @PathVariable Long groupId,
            @Valid @RequestBody CreateChannelRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        Channel channel = channelService.createChannel(groupId, request, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.ok("Canal creado exitosamente", channel));
    }

    @DeleteMapping("/{channelId}")
    public ResponseEntity<ApiResponse<Void>> deleteChannel(
            @PathVariable Long groupId,
            @PathVariable Long channelId,
            @AuthenticationPrincipal UserDetails userDetails) {
        channelService.deleteChannel(channelId, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.ok("Canal eliminado"));
    }
}