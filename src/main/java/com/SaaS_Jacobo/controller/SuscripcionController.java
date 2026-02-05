package com.SaaS_Jacobo.controller;

import com.SaaS_Jacobo.model.Usuario;
import com.SaaS_Jacobo.repository.SuscripcionRepository;
import com.SaaS_Jacobo.repository.UsuarioRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/suscripciones")
public class SuscripcionController {

    private final UsuarioRepository usuarioRepository;
    private final SuscripcionRepository suscripcionRepository;

    public SuscripcionController(UsuarioRepository usuarioRepository,
                                 SuscripcionRepository suscripcionRepository) {
        this.usuarioRepository = usuarioRepository;
        this.suscripcionRepository = suscripcionRepository;
    }

    @GetMapping("/ver/{usuarioId}")
    public String verSuscripciones(@PathVariable Long usuarioId, Model model) {
        Usuario usuario = usuarioRepository.findById(usuarioId).orElseThrow();
        model.addAttribute("usuario", usuario);
        model.addAttribute("suscripciones", suscripcionRepository.findByUsuario(usuario));
        return "suscripcion";
    }
}
