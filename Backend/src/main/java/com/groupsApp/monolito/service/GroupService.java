package com.groupsapp.monolito.service;

import com.groupsapp.monolito.dto.group.CreateGroupRequest;
import com.groupsapp.monolito.dto.group.GroupDTO;
import com.groupsapp.monolito.dto.group.MemberDTO;
import com.groupsapp.monolito.dto.group.UserSearchDTO;
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

    private final GroupRepository       groupRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final ChannelRepository     channelRepository;
    private final UserRepository        userRepository;

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
        User owner = findUserByEmail(ownerEmail);
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

        return GroupDTO.fromEntity(saved, groupMemberRepository.countByGroup(saved));
    }

    @Transactional
    public void joinGroup(Long groupId, String userEmail) {
        User user = findUserByEmail(userEmail);
        Group group = findGroup(groupId);
        if (groupMemberRepository.existsByUserAndGroup(user, group))
            throw new RuntimeException("Ya eres miembro de este grupo");
        if (group.getType() == Group.GroupType.PRIVATE)
            throw new RuntimeException("Este grupo es privado, necesitas invitacion");
        GroupMember member = new GroupMember();
        member.setUser(user);
        member.setGroup(group);
        member.setRole(GroupMember.MemberRole.MEMBER);
        groupMemberRepository.save(member);
    }

    @Transactional
    public MemberDTO addMember(Long groupId, String targetUsername, String requesterEmail) {
        User requester = findUserByEmail(requesterEmail);
        Group group = findGroup(groupId);
        GroupMember requesterMembership = groupMemberRepository
                .findByUserAndGroup(requester, group)
                .orElseThrow(() -> new RuntimeException("No eres miembro de este grupo"));
        if (requesterMembership.getRole() == GroupMember.MemberRole.MEMBER)
            throw new RuntimeException("Solo admins y el owner pueden aniadir miembros");
        User target = userRepository.findByUsername(targetUsername)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + targetUsername));
        if (groupMemberRepository.existsByUserAndGroup(target, group))
            throw new RuntimeException("El usuario ya es miembro del grupo");
        GroupMember newMember = new GroupMember();
        newMember.setUser(target);
        newMember.setGroup(group);
        newMember.setRole(GroupMember.MemberRole.MEMBER);
        groupMemberRepository.save(newMember);
        return MemberDTO.fromEntity(newMember);
    }

    @Transactional
    public void leaveGroup(Long groupId, String userEmail) {
        User user = findUserByEmail(userEmail);
        Group group = findGroup(groupId);
        GroupMember member = groupMemberRepository.findByUserAndGroup(user, group)
                .orElseThrow(() -> new RuntimeException("No eres miembro de este grupo"));
        if (member.getRole() == GroupMember.MemberRole.OWNER)
            throw new RuntimeException("Transfiere la propiedad antes de salir");
        groupMemberRepository.delete(member);
    }

    @Transactional(readOnly = true)
    public List<GroupDTO> getMyGroups(String userEmail) {
        User user = findUserByEmail(userEmail);
        return groupMemberRepository.findByUser(user).stream()
                .map(m -> GroupDTO.fromEntity(m.getGroup(),
                        groupMemberRepository.countByGroup(m.getGroup())))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<GroupDTO> searchGroups(String name) {
        return groupRepository.findByNameContainingIgnoreCase(name).stream()
                .filter(g -> g.getType() != Group.GroupType.PRIVATE)
                .map(g -> GroupDTO.fromEntity(g, groupMemberRepository.countByGroup(g)))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public GroupDTO getGroupById(Long groupId, String userEmail) {
        Group group = findGroup(groupId);
        User user = findUserByEmail(userEmail);
        if (!groupMemberRepository.existsByUserAndGroup(user, group))
            throw new RuntimeException("No tienes acceso a este grupo");
        return GroupDTO.fromEntity(group, groupMemberRepository.countByGroup(group));
    }

    @Transactional(readOnly = true)
    public List<MemberDTO> getMembers(Long groupId, String requesterEmail) {
        Group group = findGroup(groupId);
        User requester = findUserByEmail(requesterEmail);
        if (!groupMemberRepository.existsByUserAndGroup(requester, group))
            throw new RuntimeException("No tienes acceso a este grupo");
        return groupMemberRepository.findByGroup(group).stream()
                .map(MemberDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<UserSearchDTO> searchUsers(String query) {
        return userRepository.searchByUsernameOrEmail(query).stream()
                .map(UserSearchDTO::fromEntity)
                .collect(Collectors.toList());
    }

    private User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    private Group findGroup(Long id) {
        return groupRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Grupo no encontrado"));
    }
}
