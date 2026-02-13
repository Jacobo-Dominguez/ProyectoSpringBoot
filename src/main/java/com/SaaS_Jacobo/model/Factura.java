package com.SaaS_Jacobo.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Audited
public class Factura {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Suscripcion suscripcion;

    private Double montoBase; // Precio sin impuestos
    private Double tasaImpuesto; // Porcentaje de impuesto aplicado
    private Double impuesto; // Cantidad del impuesto
    private Double montoTotal; // Precio total con impuestos
    private Boolean esProrrateo; // Indica si es factura de prorrateo por cambio de plan
    private String detalles; // Desglose o descripción de la factura
    
    private LocalDate fechaGeneracion;

    @ManyToOne
    @JoinColumn(name = "pago_id")
    @NotAudited  // No auditar la relación con Pago
    private Pago pago;
}
