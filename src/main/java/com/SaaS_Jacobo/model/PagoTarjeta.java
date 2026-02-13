package com.SaaS_Jacobo.model;

import jakarta.persistence.Entity;
import lombok.*;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class PagoTarjeta extends Pago {
    private String numeroTarjeta;
    private String titular;
    private String fechaExpiracion;
}
