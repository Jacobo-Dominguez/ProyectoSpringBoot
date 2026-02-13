package com.SaaS_Jacobo.repository;

import com.SaaS_Jacobo.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.history.RevisionRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long>, RevisionRepository<Usuario, Long, Integer> {
    // Buscar por email
    @Query("SELECT u FROM Usuario u WHERE u.email = :email")
    Optional<Usuario> encontrarPorEmail(String email);
}
