package com.SaaS_Jacobo.model;

import com.SaaS_Jacobo.enums.EstadoSuscripcion;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.envers.Audited;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Audited
public class Suscripcion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Usuario usuario;

    @ManyToOne
    private Plan plan;

    @Enumerated(EnumType.STRING)
    private EstadoSuscripcion estado;

    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private LocalDate fechaProximaRenovacion;

    @OneToMany(mappedBy = "suscripcion", cascade = CascadeType.ALL)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Factura> facturas;

    //Calcula los días restantes hasta la próxima renovación
    public long obtenerDiasRestantes() {
        if (fechaProximaRenovacion == null) {
            return 0;
        }
        return ChronoUnit.DAYS.between(LocalDate.now(), fechaProximaRenovacion);
    }
}
