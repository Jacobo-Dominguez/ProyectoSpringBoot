package com.SaaS_Jacobo.controller;

import com.SaaS_Jacobo.model.Suscripcion;
import com.SaaS_Jacobo.model.Usuario;
import com.SaaS_Jacobo.repository.PlanRepository;
import com.SaaS_Jacobo.repository.SuscripcionRepository;
import com.SaaS_Jacobo.repository.UsuarioRepository;
import com.SaaS_Jacobo.service.SuscripcionService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/suscripciones")
public class SuscripcionController {

    private final UsuarioRepository usuarioRepository;
    private final SuscripcionRepository suscripcionRepository;
    private final PlanRepository planRepository;
    private final SuscripcionService suscripcionService;

    public SuscripcionController(UsuarioRepository usuarioRepository,
                                 SuscripcionRepository suscripcionRepository,
                                 PlanRepository planRepository,
                                 SuscripcionService suscripcionService) {
        this.usuarioRepository = usuarioRepository;
        this.suscripcionRepository = suscripcionRepository;
        this.planRepository = planRepository;
        this.suscripcionService = suscripcionService;
    }

    @GetMapping("/ver/{usuarioId}")
    public String verSuscripciones(@PathVariable Long usuarioId, Model model) {
        Usuario usuario = usuarioRepository.findById(usuarioId).orElseThrow();
        model.addAttribute("usuario", usuario);
        model.addAttribute("suscripciones", suscripcionRepository.encontrarPorUsuario(usuario));
        return "suscripcion";
    }

    @GetMapping("/cambiar-plan/{suscripcionId}")
    public String mostrarFormularioCambioPlan(@PathVariable Long suscripcionId, Model model) {
        Suscripcion suscripcion = suscripcionRepository.findById(suscripcionId).orElseThrow();
        model.addAttribute("suscripcion", suscripcion);
        
        java.util.List<com.SaaS_Jacobo.model.Plan> planes = planRepository.findAll();
        model.addAttribute("planes", planes);
        
        // Calcular previsualización de prorrateo para cada plan
        java.util.Map<Long, Double> prorrateos = new java.util.HashMap<>();
        for (com.SaaS_Jacobo.model.Plan plan : planes) {
            if (plan.getPrecio() > suscripcion.getPlan().getPrecio()) {
                prorrateos.put(plan.getId(), suscripcionService.calcularProrrateo(suscripcion, plan));
            } else {
                prorrateos.put(plan.getId(), 0.0);
            }
        }
        model.addAttribute("prorrateos", prorrateos);
        
        return "cambiarPlan";
    }

    @PostMapping("/cambiar-plan")
    public String procesarCambioPlan(@RequestParam Long suscripcionId,
                                     @RequestParam Long nuevoPlanId) {
        Suscripcion suscripcion = suscripcionService.cambiarPlan(suscripcionId, nuevoPlanId);
        return "redirect:/suscripciones/ver/" + suscripcion.getUsuario().getId();
    }

    @PostMapping("/cambiar-estado")
    public String cambiarEstadoSuscripcion(@RequestParam Long suscripcionId,
                                            @RequestParam String nuevoEstado) {
        Suscripcion suscripcion = suscripcionRepository.findById(suscripcionId).orElseThrow();
        suscripcion.setEstado(com.SaaS_Jacobo.enums.EstadoSuscripcion.valueOf(nuevoEstado));
        suscripcionRepository.save(suscripcion);
        return "redirect:/admin/auditoria";
    }
}
