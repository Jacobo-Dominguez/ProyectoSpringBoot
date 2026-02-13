package com.SaaS_Jacobo.repository;

import com.SaaS_Jacobo.model.Plan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlanRepository extends JpaRepository<Plan, Long> {
    @org.springframework.data.jpa.repository.Query("SELECT p FROM Plan p WHERE p.nombre = :nombre")
    Plan encontrarPorNombre(String nombre);
}
