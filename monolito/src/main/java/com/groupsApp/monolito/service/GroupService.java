package com.groupsapp.monolito.service;

import com.groupsapp.monolito.dto.group.CreateGroupRequest;
import com.groupsapp.monolito.dto.group.GroupDTO;
import com.groupsapp.monolito.model.Channel;
import com.groupsapp.monolito.model.Group;
import com.groupsapp.monolito.model.GroupMember;
import com.groupsapp.monolito.model.User;
import com.groupsapp.monolito.repository.ChannelRepository;
import com.groupsapp.monolito.repository.GroupMemberRepository;
import com.groupsapp.monolito.repository.GroupRepository;
import com.groupsapp.monolito.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class GroupService {

    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;

    public GroupService(GroupRepository groupRepository,
                        GroupMemberRepository groupMemberRepository,
                        ChannelRepository channelRepository,
                        UserRepository userRepository) {
        this.groupRepository       = groupRepository;
        this.groupMemberRepository = groupMemberRepository;
        this.channelRepository     = channelRepository;
        this.userRepository        = userRepository;
    }

    @Transactional
    public GroupDTO createGroup(CreateGroupRequest request, String ownerEmail) {
        User owner = userRepository.findByEmail(ownerEmail)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        Group group = new Group();
        group.setName(request.getName());
        group.setDescription(request.getDescription());
        group.setType(request.getType());
        group.setOwner(owner);
        Group saved = groupRepository.save(group);

        GroupMember ownerMember = new GroupMember();
        ownerMember.setUser(owner);
        ownerMember.setGroup(saved);
        ownerMember.setRole(GroupMember.MemberRole.OWNER);
        groupMemberRepository.save(ownerMember);

        Channel general = new Channel();
        general.setName("general");
        general.setDescription("Canal principal del grupo");
        general.setGroup(saved);
        channelRepository.save(general);

        long count = groupMemberRepository.countByGroup(saved);
        return GroupDTO.fromEntity(saved, count);
    }

    @Transactional
    public void joinGroup(Long groupId, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Grupo no encontrado"));
        if (groupMemberRepository.existsByUserAndGroup(user, group))
            throw new RuntimeException("Ya eres miembro de este grupo");
        if (group.getType() == Group.GroupType.PRIVATE)
            throw new RuntimeException("Este grupo es privado, necesitas invitación");
        GroupMember member = new GroupMember();
        member.setUser(user);
        member.setGroup(group);
        member.setRole(GroupMember.MemberRole.MEMBER);
        groupMemberRepository.save(member);
    }

    @Transactional
    public void leaveGroup(Long groupId, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Grupo no encontrado"));
        GroupMember member = groupMemberRepository.findByUserAndGroup(user, group)
                .orElseThrow(() -> new RuntimeException("No eres miembro de este grupo"));
        if (member.getRole() == GroupMember.MemberRole.OWNER)
            throw new RuntimeException("Transfiere la propiedad antes de salir");
        groupMemberRepository.delete(member);
    }

    public List<GroupDTO> getMyGroups(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return groupMemberRepository.findByUser(user).stream()
                .map(m -> GroupDTO.fromEntity(m.getGroup(), groupMemberRepository.countByGroup(m.getGroup())))
                .collect(Collectors.toList());
    }

    public List<GroupDTO> searchGroups(String name) {
        return groupRepository.findByNameContainingIgnoreCase(name).stream()
                .filter(g -> g.getType() != Group.GroupType.PRIVATE)
                .map(g -> GroupDTO.fromEntity(g, groupMemberRepository.countByGroup(g)))
                .collect(Collectors.toList());
    }

    public GroupDTO getGroupById(Long groupId, String userEmail) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Grupo no encontrado"));
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        if (!groupMemberRepository.existsByUserAndGroup(user, group))
            throw new RuntimeException("No tienes acceso a este grupo");
        return GroupDTO.fromEntity(group, groupMemberRepository.countByGroup(group));
    }
}