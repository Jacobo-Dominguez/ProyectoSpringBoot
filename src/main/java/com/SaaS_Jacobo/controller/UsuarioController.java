package com.SaaS_Jacobo.controller;

import com.SaaS_Jacobo.model.*;
import com.SaaS_Jacobo.repository.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Controller
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioRepository usuarioRepository;
    private final PlanRepository planRepository;
    private final SuscripcionRepository suscripcionRepository;

    public UsuarioController(UsuarioRepository usuarioRepository,
                             PlanRepository planRepository,
                             SuscripcionRepository suscripcionRepository) {
        this.usuarioRepository = usuarioRepository;
        this.planRepository = planRepository;
        this.suscripcionRepository = suscripcionRepository;
    }

    @GetMapping("/nuevo")
    public String mostrarFormulario(Model model) {
        model.addAttribute("usuario", new Usuario());
        model.addAttribute("planes", planRepository.findAll());
        return "registroUsuario";
    }

    @PostMapping("/guardar")
    public String guardarUsuario(@ModelAttribute Usuario usuario,
                                 @RequestParam Long planId) {

        usuarioRepository.save(usuario);

        Plan plan = planRepository.findById(planId).orElseThrow();

        Suscripcion suscripcion = new Suscripcion();
        suscripcion.setUsuario(usuario);
        suscripcion.setPlan(plan);
        suscripcion.setFechaInicio(LocalDate.now());
        suscripcion.setFechaFin(LocalDate.now().plusMonths(1));
        suscripcion.setEstado(EstadoSuscripcion.ACTIVA);

        suscripcionRepository.save(suscripcion);

        return "redirect:/suscripciones/ver/" + usuario.getId();
    }
}
