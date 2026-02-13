package com.SaaS_Jacobo.controller;

import com.SaaS_Jacobo.model.Usuario;
import com.SaaS_Jacobo.repository.UsuarioRepository;
import org.springframework.data.history.Revision;
import org.springframework.data.history.Revisions;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AuditoriaController {

    private final UsuarioRepository usuarioRepository;

    public AuditoriaController(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    @GetMapping("/auditoria")
    public String verAuditoria(Model model) {
        List<Usuario> usuarios = usuarioRepository.findAll();
        // Inicializar suscripciones para evitar LazyInitializationException en la vista
        usuarios.forEach(u -> u.getSuscripciones().size());
        model.addAttribute("usuarios", usuarios);
        return "auditoria";
    }

    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    @GetMapping("/usuario/{id}")
    public String verUsuarioDetalle(@PathVariable Long id, Model model) {
        Usuario usuario = usuarioRepository.findById(id).orElseThrow();
        
        // Cargar colecciones para evitar lazy loading errors
        if (usuario.getSuscripciones() != null) {
            usuario.getSuscripciones().size();
            usuario.getSuscripciones().forEach(s -> {
                if(s.getPlan() != null) s.getPlan().getNombre();
            });
        }

        if (usuario.getPlanActual() != null) {
            usuario.getPlanActual().getNombre();
        }
        
        // Inicializar propiedades OneToOne y ManyToOne (sugerencia del usuario)
        if (usuario.getPerfil() != null) {
            usuario.getPerfil().getDireccion();
            usuario.getPerfil().getTelefono();
            if (usuario.getPerfil().getPais() != null) {
                usuario.getPerfil().getPais().getNombre();
            }
        }

        try {
            Revisions<Integer, Usuario> revisions = usuarioRepository.findRevisions(id);
            List<Revision<Integer, Usuario>> revisionList = revisions.getContent();
            model.addAttribute("revisions", revisionList);
        } catch (Exception e) {
            // Si falla la auditoría, al menos mostramos el detalle del usuario
            model.addAttribute("revisions", java.util.Collections.emptyList());
            model.addAttribute("auditError", "No se pudo cargar el historial de cambios: " + e.getMessage());
        }
        
        model.addAttribute("usuario", usuario);
        return "usuarioDetalleAudit";
    }
}
