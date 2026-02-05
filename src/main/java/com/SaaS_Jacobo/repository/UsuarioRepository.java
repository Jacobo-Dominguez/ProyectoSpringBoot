package com.SaaS_Jacobo.repository;

import com.SaaS_Jacobo.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

}
