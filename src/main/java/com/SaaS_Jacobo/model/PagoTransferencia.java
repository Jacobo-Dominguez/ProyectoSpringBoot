package com.SaaS_Jacobo.model;

import jakarta.persistence.Entity;
import lombok.*;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class PagoTransferencia extends Pago {
    private String banco;
    private String numeroCuenta;
    private String titularCuenta;
}
