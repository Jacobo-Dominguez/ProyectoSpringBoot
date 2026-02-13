package com.SaaS_Jacobo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableJpaAuditing
@EnableScheduling
public class JpaConfig {
    // Habilita auditoría automática de JPA (@CreatedDate, @LastModifiedDate)
    // Habilita tareas programadas (@Scheduled)
}
