package com.SaaS_Jacobo.service;

import com.SaaS_Jacobo.enums.EstadoSuscripcion;
import com.SaaS_Jacobo.model.Plan;
import com.SaaS_Jacobo.model.Suscripcion;
import com.SaaS_Jacobo.repository.PlanRepository;
import com.SaaS_Jacobo.repository.SuscripcionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SuscripcionService {

    private final SuscripcionRepository suscripcionRepository;
    private final PlanRepository planRepository;
    private final FacturaService facturaService;

    //Renueva automáticamente las suscripciones que expiran hoy
    @Transactional
    public void renovarSuscripciones() {
        LocalDate hoy = LocalDate.now();
        
        // Buscar suscripciones activas que necesitan renovación
        List<Suscripcion> suscripcionesARenovar = suscripcionRepository
                .encontrarPorEstadoYFechaRenovacionAntes(EstadoSuscripcion.ACTIVA, hoy.plusDays(1));

        log.info("Renovando {} suscripciones", suscripcionesARenovar.size());

        for (Suscripcion suscripcion : suscripcionesARenovar) {
            try {
                // Generar factura automática
                facturaService.generarFactura(suscripcion);

                // Actualizar fechas de la suscripción
                suscripcion.setFechaInicio(suscripcion.getFechaFin());
                suscripcion.setFechaFin(suscripcion.getFechaFin().plusMonths(1));
                suscripcion.setFechaProximaRenovacion(suscripcion.getFechaFin());

                suscripcionRepository.save(suscripcion);
                
                log.info("Suscripción {} renovada correctamente", suscripcion.getId());
            } catch (Exception e) {
                log.error("Error renovando suscripción {}: {}", suscripcion.getId(), e.getMessage());
                // Marcar como morosa si falla el pago
                suscripcion.setEstado(EstadoSuscripcion.MOROSA);
                suscripcionRepository.save(suscripcion);
            }
        }
    }

    /**
     * Cambia el plan de una suscripción con cálculo de prorrateo
     * Si el nuevo plan es más caro, se cobra la diferencia proporcional
     */
    @Transactional
    public Suscripcion cambiarPlan(Long suscripcionId, Long nuevoPlanId) {
        Suscripcion suscripcion = suscripcionRepository.findById(suscripcionId)
                .orElseThrow(() -> new RuntimeException("Suscripción no encontrada"));
        
        Plan nuevoPlan = planRepository.findById(nuevoPlanId)
                .orElseThrow(() -> new RuntimeException("Plan no encontrado"));

        Plan planAnterior = suscripcion.getPlan();

        // Si es el mismo plan, no hacer nada
        if (planAnterior.getId().equals(nuevoPlan.getId())) {
            return suscripcion;
        }

        // Calcular prorrateo si el nuevo plan es más caro
        if (nuevoPlan.getPrecio() > planAnterior.getPrecio()) {
            Double montoProrrateo = calcularProrrateo(suscripcion, nuevoPlan);
            
            // Construir detalle descriptivo
            String detalleBreakdown = String.format("Cambio de %s (%.2f€) a %s (%.2f€). Proporción por días restantes.", 
                                                   planAnterior.getNombre(), planAnterior.getPrecio(),
                                                   nuevoPlan.getNombre(), nuevoPlan.getPrecio());

            // Generar factura de prorrateo
            facturaService.generarFacturaProrrateo(suscripcion, montoProrrateo, detalleBreakdown);
            
            log.info("Prorrateo calculado: €{} para cambio de {} a {}", 
                    montoProrrateo, planAnterior.getNombre(), nuevoPlan.getNombre());
        }

        // Actualizar plan (Envers registrará este cambio automáticamente)
        suscripcion.setPlan(nuevoPlan);
        suscripcion.getUsuario().setPlanActual(nuevoPlan);

        return suscripcionRepository.save(suscripcion);
    }

    // Calcula el monto de prorrateo al cambiar a un plan más caro
    public Double calcularProrrateo(Suscripcion suscripcion, Plan nuevoPlan) {
        Plan planActual = suscripcion.getPlan();
        
        // Diferencia de precio
        Double diferenciaPrecio = nuevoPlan.getPrecio() - planActual.getPrecio();

        // Días restantes hasta la próxima renovación
        long diasRestantes = ChronoUnit.DAYS.between(LocalDate.now(), suscripcion.getFechaProximaRenovacion());
        
        // Si no quedan días o es negativo, no hay prorrateo
        if (diasRestantes <= 0) {
            return 0.0;
        }

        // Calcular prorrateo proporcional (asumiendo mes de 30 días)
        Double prorrateo = diferenciaPrecio * (diasRestantes / 30.0);

        return Math.max(0.0, prorrateo); // Asegurar que no sea negativo
    }
}
