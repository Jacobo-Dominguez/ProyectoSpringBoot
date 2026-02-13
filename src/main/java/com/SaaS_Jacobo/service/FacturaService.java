package com.SaaS_Jacobo.service;

import com.SaaS_Jacobo.model.Factura;
import com.SaaS_Jacobo.model.Pais;
import com.SaaS_Jacobo.model.Suscripcion;
import com.SaaS_Jacobo.repository.FacturaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class FacturaService {

    private final FacturaRepository facturaRepository;

    //Genera una factura mensual para una suscripción
    @Transactional
    public Factura generarFactura(Suscripcion suscripcion) {
        Pais pais = suscripcion.getUsuario().getPerfil().getPais();
        Double montoBase = suscripcion.getPlan().getPrecio();
        String detalles = "Mensualidad Plan " + suscripcion.getPlan().getNombre();
        
        return generarFacturaConMonto(suscripcion, montoBase, false, pais, detalles);
    }
    
    //Genera una factura de prorrateo por cambio de plan
    @Transactional
    public Factura generarFacturaProrrateo(Suscripcion suscripcion, Double montoProrrateo, String detalles) {
        Pais pais = suscripcion.getUsuario().getPerfil().getPais();
        return generarFacturaConMonto(suscripcion, montoProrrateo, true, pais, detalles);
    }
    
    //Método privado para generar factura con monto específico
    private Factura generarFacturaConMonto(Suscripcion suscripcion, Double montoBase, 
                                           Boolean esProrrateo, Pais pais, String detalles) {
        Double tasaImpuesto = pais.getTasaImpuesto();
        Double impuesto = calcularImpuesto(montoBase, tasaImpuesto);
        Double montoTotal = montoBase + impuesto;

        Factura factura = Factura.builder()
                .suscripcion(suscripcion)
                .montoBase(montoBase)
                .tasaImpuesto(tasaImpuesto)
                .impuesto(impuesto)
                .montoTotal(montoTotal)
                .esProrrateo(esProrrateo)
                .detalles(detalles)
                .fechaGeneracion(LocalDate.now())
                .build();

        return facturaRepository.save(factura);
    }

    //Calcula el impuesto según el país
    public Double calcularImpuesto(Double monto, Double tasaImpuesto) {
        return monto * (tasaImpuesto / 100.0);
    }
}
