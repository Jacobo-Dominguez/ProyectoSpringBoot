package com.SaaS_Jacobo.repository;

import com.SaaS_Jacobo.model.Pais;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaisRepository extends JpaRepository<Pais, Long> {
    @org.springframework.data.jpa.repository.Query("SELECT p FROM Pais p WHERE p.codigo = :codigo")
    java.util.Optional<Pais> encontrarPorCodigo(String codigo);
}
