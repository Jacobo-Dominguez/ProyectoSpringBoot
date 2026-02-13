package com.SaaS_Jacobo.repository;

import com.SaaS_Jacobo.model.Factura;
import com.SaaS_Jacobo.model.Suscripcion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface FacturaRepository extends JpaRepository<Factura, Long> {
    // Buscar facturas de una suscripción
    @org.springframework.data.jpa.repository.Query("SELECT f FROM Factura f WHERE f.suscripcion = :suscripcion")
    List<Factura> encontrarPorSuscripcion(Suscripcion suscripcion);

    // Filtrar por fecha de generación
    @org.springframework.data.jpa.repository.Query("SELECT f FROM Factura f WHERE f.fechaGeneracion BETWEEN :inicio AND :fin")
    List<Factura> encontrarPorFechaGeneracion(LocalDate inicio, LocalDate fin);

    // Filtrar por cantidad total mayor que
    @org.springframework.data.jpa.repository.Query("SELECT f FROM Factura f WHERE f.montoTotal > :monto")
    List<Factura> encontrarPorCantidadMayor(Double monto);

    // Buscar facturas por usuario ID
    @org.springframework.data.jpa.repository.Query("SELECT f FROM Factura f WHERE f.suscripcion.usuario.id = :usuarioId")
    List<Factura> encontrarPorUsuarioID(Long usuarioId);
    
    // Buscar facturas de prorrateo
    @org.springframework.data.jpa.repository.Query("SELECT f FROM Factura f WHERE f.esProrrateo = :esProrrateo")
    List<Factura> encontrarEsProrrateo(Boolean esProrrateo);
}

