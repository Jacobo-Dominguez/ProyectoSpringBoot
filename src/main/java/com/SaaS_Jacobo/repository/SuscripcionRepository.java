package com.SaaS_Jacobo.repository;

import com.SaaS_Jacobo.model.Suscripcion;
import com.SaaS_Jacobo.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SuscripcionRepository extends JpaRepository<Suscripcion, Long> {
    List<Suscripcion> findByUsuario(Usuario usuario);
}
