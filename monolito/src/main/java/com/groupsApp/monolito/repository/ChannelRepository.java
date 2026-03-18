package com.groupsapp.monolito.repository;

import com.groupsapp.monolito.model.Channel;
import com.groupsapp.monolito.model.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChannelRepository extends JpaRepository<Channel, Long> {

    // Todos los canales de un grupo
    List<Channel> findByGroup(Group group);

    // Todos los canales de un grupo por su ID
    List<Channel> findByGroupId(Long groupId);

    // Buscar canal por nombre dentro de un grupo
    Optional<Channel> findByNameAndGroup(String name, Group group);

    // Verificar si ya existe un canal con ese nombre en el grupo
    boolean existsByNameAndGroup(String name, Group group);
}