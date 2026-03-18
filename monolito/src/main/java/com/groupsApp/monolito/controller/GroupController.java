package com.groupsapp.monolito.controller;

import com.groupsapp.monolito.dto.ApiResponse;
import com.groupsapp.monolito.dto.group.CreateGroupRequest;
import com.groupsapp.monolito.dto.group.GroupDTO;
import com.groupsapp.monolito.service.GroupService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/groups")
public class GroupController {

    private final GroupService groupService;

    public GroupController(GroupService groupService) {
        this.groupService = groupService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<GroupDTO>> createGroup(
            @Valid @RequestBody CreateGroupRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        GroupDTO group = groupService.createGroup(request, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.ok("Grupo creado exitosamente", group));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<GroupDTO>>> getMyGroups(
            @AuthenticationPrincipal UserDetails userDetails) {
        List<GroupDTO> groups = groupService.getMyGroups(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.ok("Grupos obtenidos", groups));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<GroupDTO>> getGroupById(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        GroupDTO group = groupService.getGroupById(id, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.ok("Grupo encontrado", group));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<GroupDTO>>> searchGroups(@RequestParam String name) {
        List<GroupDTO> groups = groupService.searchGroups(name);
        return ResponseEntity.ok(ApiResponse.ok("Búsqueda completada", groups));
    }

    @PostMapping("/{id}/join")
    public ResponseEntity<ApiResponse<Void>> joinGroup(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        groupService.joinGroup(id, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.ok("Te uniste al grupo exitosamente"));
    }

    @PostMapping("/{id}/leave")
    public ResponseEntity<ApiResponse<Void>> leaveGroup(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        groupService.leaveGroup(id, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.ok("Saliste del grupo"));
    }
}