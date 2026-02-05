package com.SaaS_Jacobo.config;

import com.SaaS_Jacobo.model.Plan;
import com.SaaS_Jacobo.repository.PlanRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements CommandLineRunner {

    private final PlanRepository planRepository;

    public DataLoader(PlanRepository planRepository) {
        this.planRepository = planRepository;
    }

    @Override
    public void run(String... args) {
        if (planRepository.count() == 0) {
            planRepository.save(new Plan("Básico", 9.99));
            planRepository.save(new Plan("Pro", 19.99));
            planRepository.save(new Plan("Empresa", 39.99));
        }
    }
}
