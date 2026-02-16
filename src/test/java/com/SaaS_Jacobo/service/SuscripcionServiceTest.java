package com.SaaS_Jacobo.service;

import com.SaaS_Jacobo.model.Plan;
import com.SaaS_Jacobo.model.Suscripcion;
import com.SaaS_Jacobo.repository.PlanRepository;
import com.SaaS_Jacobo.repository.SuscripcionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class SuscripcionServiceTest {

    @Mock
    private SuscripcionRepository suscripcionRepository;
    @Mock
    private PlanRepository planRepository;
    @Mock
    private FacturaService facturaService;

    @InjectMocks
    private SuscripcionService suscripcionService;

    @Test
    void testCalcularProrrateo_MesCompleto() {
        // Setup
        Plan planActual = Plan.builder().precio(10.0).build();
        Plan nuevoPlan = Plan.builder().precio(40.0).build(); // Diferencia: 30.0
        
        // Próxima renovación en 30 días
        Suscripcion suscripcion = Suscripcion.builder()
                .plan(planActual)
                .fechaProximaRenovacion(LocalDate.now().plusDays(30))
                .build();

        Double prorrateo = suscripcionService.calcularProrrateo(suscripcion, nuevoPlan);

        assertEquals(30.0, prorrateo, 0.001, "Si faltan 30 días, el prorrateo debe ser la diferencia total (30€)");
    }

    @Test
    void testCalcularProrrateo_MedioMes() {
        // Setup
        Plan planActual = Plan.builder().precio(10.0).build();
        Plan nuevoPlan = Plan.builder().precio(30.0).build(); // Diferencia: 20.0
        
        // Próxima renovación en 15 días
        Suscripcion suscripcion = Suscripcion.builder()
                .plan(planActual)
                .fechaProximaRenovacion(LocalDate.now().plusDays(15))
                .build();

        Double prorrateo = suscripcionService.calcularProrrateo(suscripcion, nuevoPlan);

        assertEquals(10.0, prorrateo, 0.001, "Si faltan 15 días, el prorrateo debe ser la mitad de la diferencia (10€)");
    }

    @Test
    void testCalcularProrrateo_SinDiasRestantes() {
        // Setup
        Plan planActual = Plan.builder().precio(10.0).build();
        Plan nuevoPlan = Plan.builder().precio(40.0).build();
        
        Suscripcion suscripcion = Suscripcion.builder()
                .plan(planActual)
                .fechaProximaRenovacion(LocalDate.now())
                .build();

        Double prorrateo = suscripcionService.calcularProrrateo(suscripcion, nuevoPlan);

        assertEquals(0.0, prorrateo, 0.001, "Si no quedan días, el prorrateo debe ser 0");
    }
}
