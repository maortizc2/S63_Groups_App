package com.groupsapp.monolito.repository;

import com.groupsapp.monolito.model.Group;
import com.groupsapp.monolito.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GroupRepository extends JpaRepository<Group, Long> {

    // Grupos creados por un usuario
    List<Group> findByOwner(User owner);

    // Buscar grupos por nombre (búsqueda parcial, sin importar mayúsculas)
    List<Group> findByNameContainingIgnoreCase(String name);

    // Todos los grupos a los que pertenece un usuario (via GroupMember)
    @Query("SELECT gm.group FROM GroupMember gm WHERE gm.user.id = :userId")
    List<Group> findGroupsByUserId(@Param("userId") Long userId);

    // Verificar si un grupo existe por nombre
    boolean existsByName(String name);
}