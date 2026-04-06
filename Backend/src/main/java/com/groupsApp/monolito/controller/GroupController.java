package com.groupsapp.monolito.controller;

import com.groupsapp.monolito.dto.ApiResponse;
import com.groupsapp.monolito.dto.group.CreateGroupRequest;
import com.groupsapp.monolito.dto.group.GroupDTO;
import com.groupsapp.monolito.dto.group.MemberDTO;
import com.groupsapp.monolito.dto.group.UserSearchDTO;
import com.groupsapp.monolito.service.GroupService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/groups")
public class GroupController {

    private final GroupService groupService;

    public GroupController(GroupService groupService) {
        this.groupService = groupService;
    }

    // POST /api/groups — crear grupo
    @PostMapping
    public ResponseEntity<ApiResponse<GroupDTO>> createGroup(
            @Valid @RequestBody CreateGroupRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        GroupDTO group = groupService.createGroup(request, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.ok("Grupo creado exitosamente", group));
    }

    // GET /api/groups — mis grupos
    @GetMapping
    public ResponseEntity<ApiResponse<List<GroupDTO>>> getMyGroups(
            @AuthenticationPrincipal UserDetails userDetails) {
        List<GroupDTO> groups = groupService.getMyGroups(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.ok("Grupos obtenidos", groups));
    }

    // GET /api/groups/search?name= — buscar grupos públicos
    // IMPORTANTE: debe estar ANTES de /{id} para evitar colisión de rutas
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<GroupDTO>>> searchGroups(@RequestParam String name) {
        List<GroupDTO> groups = groupService.searchGroups(name);
        return ResponseEntity.ok(ApiResponse.ok("Busqueda completada", groups));
    }

    // GET /api/groups/search/users?q= — buscar usuarios (para DMs y añadir miembros)
    // IMPORTANTE: ruta estática antes de /{id} para evitar colisión
    @GetMapping("/search/users")
    public ResponseEntity<ApiResponse<List<UserSearchDTO>>> searchUsers(@RequestParam String q) {
        List<UserSearchDTO> users = groupService.searchUsers(q);
        return ResponseEntity.ok(ApiResponse.ok("Usuarios encontrados", users));
    }

    // GET /api/groups/{id}
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<GroupDTO>> getGroupById(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        GroupDTO group = groupService.getGroupById(id, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.ok("Grupo encontrado", group));
    }

    // POST /api/groups/{id}/join — el usuario se une
    @PostMapping("/{id}/join")
    public ResponseEntity<ApiResponse<Void>> joinGroup(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        groupService.joinGroup(id, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.ok("Te uniste al grupo exitosamente"));
    }

    // POST /api/groups/{id}/leave — el usuario sale
    @PostMapping("/{id}/leave")
    public ResponseEntity<ApiResponse<Void>> leaveGroup(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        groupService.leaveGroup(id, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.ok("Saliste del grupo"));
    }

    // GET /api/groups/{id}/members — listar miembros
    @GetMapping("/{id}/members")
    public ResponseEntity<ApiResponse<List<MemberDTO>>> getMembers(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        List<MemberDTO> members = groupService.getMembers(id, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.ok("Miembros obtenidos", members));
    }

    // POST /api/groups/{id}/members — añadir un usuario (solo admin/owner)
    @PostMapping("/{id}/members")
    public ResponseEntity<ApiResponse<MemberDTO>> addMember(
            @PathVariable Long id,
            @RequestBody Map<String, String> body,
            @AuthenticationPrincipal UserDetails userDetails) {
        String targetUsername = body.get("username");
        if (targetUsername == null || targetUsername.isBlank())
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("El campo 'username' es requerido"));
        MemberDTO member = groupService.addMember(id, targetUsername, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.ok("Usuario anadido al grupo", member));
    }
}
