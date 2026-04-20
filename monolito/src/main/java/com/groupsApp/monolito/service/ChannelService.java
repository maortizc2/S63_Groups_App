package com.groupsapp.monolito.service;

import com.groupsapp.monolito.dto.group.CreateChannelRequest;
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

@Service
public class ChannelService {

    private final ChannelRepository channelRepository;
    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final UserRepository userRepository;

    public ChannelService(ChannelRepository channelRepository,
                          GroupRepository groupRepository,
                          GroupMemberRepository groupMemberRepository,
                          UserRepository userRepository) {
        this.channelRepository     = channelRepository;
        this.groupRepository       = groupRepository;
        this.groupMemberRepository = groupMemberRepository;
        this.userRepository        = userRepository;
    }

    @Transactional
    public Channel createChannel(Long groupId, CreateChannelRequest request, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Grupo no encontrado"));
        GroupMember member = groupMemberRepository.findByUserAndGroup(user, group)
                .orElseThrow(() -> new RuntimeException("No eres miembro de este grupo"));
        if (member.getRole() == GroupMember.MemberRole.MEMBER)
            throw new RuntimeException("Solo los administradores pueden crear canales");
        if (channelRepository.existsByNameAndGroup(request.getName(), group))
            throw new RuntimeException("Ya existe un canal con ese nombre en el grupo");
        Channel channel = new Channel();
        channel.setName(request.getName());
        channel.setDescription(request.getDescription());
        channel.setReadOnly(request.getReadOnly());
        channel.setGroup(group);
        return channelRepository.save(channel);
    }

    public List<Channel> getChannelsByGroup(Long groupId, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Grupo no encontrado"));
        if (!groupMemberRepository.existsByUserAndGroup(user, group))
            throw new RuntimeException("No tienes acceso a este grupo");
        return channelRepository.findByGroup(group);
    }

    @Transactional
    public void deleteChannel(Long channelId, String userEmail) {
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new RuntimeException("Canal no encontrado"));
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        GroupMember member = groupMemberRepository.findByUserAndGroup(user, channel.getGroup())
                .orElseThrow(() -> new RuntimeException("No eres miembro de este grupo"));
        if (member.getRole() == GroupMember.MemberRole.MEMBER)
            throw new RuntimeException("Solo los administradores pueden eliminar canales");
        if (channel.getName().equals("general"))
            throw new RuntimeException("No se puede eliminar el canal general");
        channelRepository.delete(channel);
    }
}