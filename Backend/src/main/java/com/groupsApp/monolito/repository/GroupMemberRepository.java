package com.groupsapp.monolito.repository;

import com.groupsapp.monolito.model.Group;
import com.groupsapp.monolito.model.GroupMember;
import com.groupsapp.monolito.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GroupMemberRepository extends JpaRepository<GroupMember, Long> {

    // Todos los miembros de un grupo
    List<GroupMember> findByGroup(Group group);

    // Todos los grupos donde está un usuario
    List<GroupMember> findByUser(User user);

    // Buscar la membresía específica de un usuario en un grupo
    Optional<GroupMember> findByUserAndGroup(User user, Group group);

    // Verificar si un usuario ya es miembro de un grupo
    boolean existsByUserAndGroup(User user, Group group);

    // Contar cuántos miembros tiene un grupo
    long countByGroup(Group group);
}