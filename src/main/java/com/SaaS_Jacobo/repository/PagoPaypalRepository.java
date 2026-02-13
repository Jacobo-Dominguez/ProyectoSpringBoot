package com.SaaS_Jacobo.repository;

import com.SaaS_Jacobo.model.PagoPaypal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PagoPaypalRepository extends JpaRepository<PagoPaypal, Long> {
}
