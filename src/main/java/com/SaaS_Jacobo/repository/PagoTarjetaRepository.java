package com.SaaS_Jacobo.repository;

import com.SaaS_Jacobo.model.PagoTarjeta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PagoTarjetaRepository extends JpaRepository<PagoTarjeta, Long> {
}
