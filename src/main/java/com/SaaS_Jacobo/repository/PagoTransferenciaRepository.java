package com.SaaS_Jacobo.repository;

import com.SaaS_Jacobo.model.PagoTransferencia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PagoTransferenciaRepository extends JpaRepository<PagoTransferencia, Long> {
}