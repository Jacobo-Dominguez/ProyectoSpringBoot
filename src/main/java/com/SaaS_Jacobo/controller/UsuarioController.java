package com.SaaS_Jacobo.controller;

import com.SaaS_Jacobo.enums.EstadoSuscripcion;
import com.SaaS_Jacobo.model.*;
import com.SaaS_Jacobo.repository.*;
import com.SaaS_Jacobo.service.FacturaService;
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
    private final PerfilRepository perfilRepository;
    private final PaisRepository paisRepository;
    private final FacturaService facturaService;
    private final org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    public UsuarioController(UsuarioRepository usuarioRepository,
                             PlanRepository planRepository,
                             SuscripcionRepository suscripcionRepository,
                             PerfilRepository perfilRepository,
                             PaisRepository paisRepository,
                             FacturaService facturaService,
                             org.springframework.security.crypto.password.PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.planRepository = planRepository;
        this.suscripcionRepository = suscripcionRepository;
        this.perfilRepository = perfilRepository;
        this.paisRepository = paisRepository;
        this.facturaService = facturaService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/nuevo")
    public String mostrarFormulario(Model model) {
        model.addAttribute("usuario", new Usuario());
        model.addAttribute("perfil", new Perfil());
        model.addAttribute("planes", planRepository.findAll());
        model.addAttribute("paises", paisRepository.findAll());
        return "registroUsuario";
    }

    @org.springframework.transaction.annotation.Transactional
    @PostMapping("/guardar")
    public String guardarUsuario(@ModelAttribute Usuario usuario,
                                 @RequestParam Long planId,
                                 @RequestParam String direccion,
                                 @RequestParam String ciudad,
                                 @RequestParam String telefono,
                                 @RequestParam Long paisId) {

        Plan plan = planRepository.findById(planId).orElseThrow();
        Pais pais = paisRepository.findById(paisId).orElseThrow();

        // Comprobar si el email ya existe
        if (usuarioRepository.encontrarPorEmail(usuario.getEmail()).isPresent()) {
            return "redirect:/usuarios/nuevo?error_email";
        }

        // Establecer plan actual y rol
        usuario.setPlanActual(plan);
        usuario.setRol("ROLE_USER");
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        
        // Guardar usuario primero
        usuarioRepository.save(usuario);

        // Crear perfil
        Perfil perfil = Perfil.builder()
                .usuario(usuario)
                .direccion(direccion)
                .ciudad(ciudad)
                .telefono(telefono)
                .pais(pais)
                .build();
        
        perfilRepository.save(perfil);
        
        // VINCULACIÓN CRÍTICA EN MEMORIA para que FacturaService encuentre el perfil
        usuario.setPerfil(perfil);

        // Crear suscripción
        Suscripcion suscripcion = Suscripcion.builder()
                .usuario(usuario)
                .plan(plan)
                .fechaInicio(LocalDate.now())
                .fechaFin(LocalDate.now().plusMonths(1))
                .fechaProximaRenovacion(LocalDate.now().plusMonths(1))
                .estado(EstadoSuscripcion.ACTIVA)
                .build();

        suscripcionRepository.save(suscripcion);
        
        // Vincular en memoria también la suscripción
        if (usuario.getSuscripciones() == null) {
            usuario.setSuscripciones(new java.util.ArrayList<>());
        }
        usuario.getSuscripciones().add(suscripcion);

        // Generar factura inicial (ahora sí encontrará el perfil/país)
        facturaService.generarFactura(suscripcion);

        return "redirect:/login";
    }
}
