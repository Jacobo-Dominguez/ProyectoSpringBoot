package com.SaaS_Jacobo.config;

import com.SaaS_Jacobo.model.Usuario;
import com.SaaS_Jacobo.repository.UsuarioRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.Optional;

@ControllerAdvice
public class GlobalControllerAdvice {

    private final UsuarioRepository usuarioRepository;

    public GlobalControllerAdvice(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @ModelAttribute
    public void addAttributes(Model model, Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            String email = authentication.getName();
            Optional<Usuario> usuarioOpt = usuarioRepository.encontrarPorEmail(email);
            usuarioOpt.ifPresent(usuario -> model.addAttribute("currentUser", usuario));
        }
    }
}
