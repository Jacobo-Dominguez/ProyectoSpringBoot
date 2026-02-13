package com.SaaS_Jacobo.repository;

import com.SaaS_Jacobo.enums.EstadoSuscripcion;
import com.SaaS_Jacobo.model.Suscripcion;
import com.SaaS_Jacobo.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface SuscripcionRepository extends JpaRepository<Suscripcion, Long> {
    // Suscripciones activas de un usuario
    @org.springframework.data.jpa.repository.Query("SELECT s FROM Suscripcion s WHERE s.usuario = :usuario AND s.estado = :estado")
    List<Suscripcion> encontrarPorUsuarioYEstado(Usuario usuario, EstadoSuscripcion estado);

    // Suscripciones que terminan antes de cierta fecha
    @org.springframework.data.jpa.repository.Query("SELECT s FROM Suscripcion s WHERE s.fechaFin < :fecha")
    List<Suscripcion> encontrarPorFechaFin(LocalDate fecha);

    // Suscripciones que necesitan renovación (por fecha de próxima renovación)
    @org.springframework.data.jpa.repository.Query("SELECT s FROM Suscripcion s WHERE s.estado = :estado AND s.fechaProximaRenovacion < :fecha")
    List<Suscripcion> encontrarPorEstadoYFechaRenovacionAntes(EstadoSuscripcion estado, LocalDate fecha);
    
    // Suscripciones por usuario ID y estado
    @org.springframework.data.jpa.repository.Query("SELECT s FROM Suscripcion s WHERE s.usuario.id = :usuarioId AND s.estado = :estado")
    List<Suscripcion> encontrarPorUsuarioIDYEstado(Long usuarioId, EstadoSuscripcion estado);
    
    // Buscar todas las suscripciones de un usuario
    @org.springframework.data.jpa.repository.Query("SELECT s FROM Suscripcion s WHERE s.usuario = :usuario")
    List<Suscripcion> encontrarPorUsuario(Usuario usuario);
}
