package com.SaaS_Jacobo.scheduler;

import com.SaaS_Jacobo.service.SuscripcionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class RenovacionScheduler {

    private final SuscripcionService suscripcionService;

    //Ejecuta la renovación automática de suscripciones
    @Scheduled(cron = "0 0 0 * * *")
    public void renovarSuscripcionesAutomaticamente() {
        log.info("Iniciando renovación automática de suscripciones...");
        suscripcionService.renovarSuscripciones();
        log.info("Renovación automática completada");
    }
}
