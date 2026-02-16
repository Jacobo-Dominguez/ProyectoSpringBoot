package com.SaaS_Jacobo.service;

import com.SaaS_Jacobo.model.Factura;
import com.SaaS_Jacobo.model.Pais;
import com.SaaS_Jacobo.model.Perfil;
import com.SaaS_Jacobo.model.Suscripcion;
import com.SaaS_Jacobo.model.Usuario;
import com.SaaS_Jacobo.model.Plan;
import com.SaaS_Jacobo.repository.FacturaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class FacturaServiceTest {

    @Mock
    private FacturaRepository facturaRepository;

    @InjectMocks
    private FacturaService facturaService;

    private Pais espana;
    private Pais usa;

    @BeforeEach
    void setUp() {
        espana = Pais.builder().nombre("España").tasaImpuesto(21.0).build();
        usa = Pais.builder().nombre("USA").tasaImpuesto(0.0).build();
    }

    @Test
    void testCalcularImpuesto_Espana() {
        Double monto = 10.0;
        Double impuesto = facturaService.calcularImpuesto(monto, espana.getTasaImpuesto());
        assertEquals(2.1, impuesto, 0.001, "El impuesto de España (21%) sobre 10€ debe ser 2.1€");
    }

    @Test
    void testCalcularImpuesto_USA() {
        Double monto = 100.0;
        Double impuesto = facturaService.calcularImpuesto(monto, usa.getTasaImpuesto());
        assertEquals(0.0, impuesto, 0.001, "El impuesto de USA (0%) sobre 100€ debe ser 0€");
    }

    @Test
    void testGenerarFactura() {
        // Setup
        Plan plan = Plan.builder().nombre("Basic").precio(10.0).build();
        Perfil perfil = Perfil.builder().pais(espana).build();
        Usuario usuario = Usuario.builder().perfil(perfil).build();
        Suscripcion suscripcion = Suscripcion.builder().usuario(usuario).plan(plan).build();

        when(facturaRepository.save(any(Factura.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Execute
        Factura factura = facturaService.generarFactura(suscripcion);

        // Verify
        assertNotNull(factura);
        assertEquals(10.0, factura.getMontoBase());
        assertEquals(2.1, factura.getImpuesto());
        assertEquals(12.1, factura.getMontoTotal());
        assertFalse(factura.getEsProrrateo());
        assertTrue(factura.getDetalles().contains("Basic"));
    }
}
