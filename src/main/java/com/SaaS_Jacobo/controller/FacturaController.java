package com.SaaS_Jacobo.controller;

import com.SaaS_Jacobo.model.Factura;
import com.SaaS_Jacobo.repository.FacturaRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/facturas")
public class FacturaController {

    private final FacturaRepository facturaRepository;

    public FacturaController(FacturaRepository facturaRepository) {
        this.facturaRepository = facturaRepository;
    }

    //Lista de facturas de un usuario
    @GetMapping("/usuario/{usuarioId}")
    public String listarFacturasUsuario(@PathVariable Long usuarioId, Model model) {
        List<Factura> facturas = facturaRepository.encontrarPorUsuarioID(usuarioId);
        model.addAttribute("facturas", facturas);
        model.addAttribute("usuarioId", usuarioId);
        return "facturacion";
    }

    //Detalle de una factura específica
    @GetMapping("/{facturaId}")
    public String verDetalleFactura(@PathVariable Long facturaId, Model model) {
        Factura factura = facturaRepository.findById(facturaId)
                .orElseThrow(() -> new RuntimeException("Factura no encontrada"));
        model.addAttribute("factura", factura);
        return "facturaDetalle";
    }

    //Filtrar facturas de un usuario
    @GetMapping("/usuario/{usuarioId}/filtrar")
    public String filtrarFacturasUsuario(@PathVariable Long usuarioId,
                                         @RequestParam(required = false) String fechaInicio,
                                         @RequestParam(required = false) String fechaFin,
                                         @RequestParam(required = false) Double montoMinimo,
                                         Model model) {
        
        List<Factura> facturas = facturaRepository.encontrarPorUsuarioID(usuarioId);
        
        if (fechaInicio != null && !fechaInicio.isEmpty() && fechaFin != null && !fechaFin.isEmpty()) {
            LocalDate inicio = LocalDate.parse(fechaInicio);
            LocalDate fin = LocalDate.parse(fechaFin);
            facturas = facturas.stream()
                    .filter(f -> !f.getFechaGeneracion().isBefore(inicio) && !f.getFechaGeneracion().isAfter(fin))
                    .collect(java.util.stream.Collectors.toList());
        }
        
        if (montoMinimo != null) {
            facturas = facturas.stream()
                    .filter(f -> f.getMontoTotal() >= montoMinimo)
                    .collect(java.util.stream.Collectors.toList());
        }

        model.addAttribute("facturas", facturas);
        model.addAttribute("usuarioId", usuarioId);
        return "facturacion";
    }
}
