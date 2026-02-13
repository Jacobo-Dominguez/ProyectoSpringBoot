package com.SaaS_Jacobo.model;

import jakarta.persistence.Entity;
import lombok.*;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class PagoPaypal extends Pago {
    private String cuentaPaypal;
}