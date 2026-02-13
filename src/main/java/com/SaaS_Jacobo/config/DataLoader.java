package com.SaaS_Jacobo.config;

import com.SaaS_Jacobo.enums.EstadoSuscripcion;
import com.SaaS_Jacobo.enums.TipoPlan;
import com.SaaS_Jacobo.model.*;
import com.SaaS_Jacobo.repository.*;
import com.SaaS_Jacobo.service.FacturaService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class DataLoader implements CommandLineRunner {

    private final PlanRepository planRepository;
    private final PaisRepository paisRepository;
    private final UsuarioRepository usuarioRepository;
    private final PerfilRepository perfilRepository;
    private final SuscripcionRepository suscripcionRepository;
    private final FacturaService facturaService;
    private final org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    public DataLoader(PlanRepository planRepository, 
                      PaisRepository paisRepository,
                      UsuarioRepository usuarioRepository,
                      PerfilRepository perfilRepository,
                      SuscripcionRepository suscripcionRepository,
                      FacturaService facturaService,
                      org.springframework.security.crypto.password.PasswordEncoder passwordEncoder) {
        this.planRepository = planRepository;
        this.paisRepository = paisRepository;
        this.usuarioRepository = usuarioRepository;
        this.perfilRepository = perfilRepository;
        this.suscripcionRepository = suscripcionRepository;
        this.facturaService = facturaService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        try {
            System.out.println("Cargando datos maestros si es necesario...");
            
            // Cargar Planes si no existen
            if (planRepository.count() == 0) {
                Plan pBasic = new Plan();
                pBasic.setNombre("Basic");
                pBasic.setTipo(TipoPlan.BASIC);
                pBasic.setDescripcion("Plan básico con funcionalidades esenciales");
                pBasic.setPrecio(9.99);
                
                Plan pPremium = new Plan();
                pPremium.setNombre("Premium");
                pPremium.setTipo(TipoPlan.PREMIUM);
                pPremium.setDescripcion("Plan premium con funcionalidades avanzadas");
                pPremium.setPrecio(19.99);
                
                Plan pEnterprise = new Plan();
                pEnterprise.setNombre("Enterprise");
                pEnterprise.setTipo(TipoPlan.ENTERPRISE);
                pEnterprise.setDescripcion("Plan empresarial con todas las funcionalidades");
                pEnterprise.setPrecio(39.99);

                planRepository.save(pBasic);
                planRepository.save(pPremium);
                planRepository.save(pEnterprise);
                System.out.println("✓ Planes cargados correctamente");
            }
            
            // Cargar Países si no existen
            if (paisRepository.count() == 0) {
                Pais esp = new Pais();
                esp.setNombre("España");
                esp.setCodigo("ES");
                esp.setTasaImpuesto(21.0);
                
                Pais mex = new Pais();
                mex.setNombre("México");
                mex.setCodigo("MX");
                mex.setTasaImpuesto(16.0);
                
                Pais us = new Pais();
                us.setNombre("Estados Unidos");
                us.setCodigo("US");
                us.setTasaImpuesto(0.0);

                paisRepository.save(esp);
                paisRepository.save(mex);
                paisRepository.save(us);
                System.out.println("✓ Países cargados correctamente");
            }

            // Cargar usuarios de prueba solo si no hay usuarios
            if (usuarioRepository.count() == 0) {
                // Recuperar referencias necesarias
                Plan basic = planRepository.findAll().stream()
                        .filter(p -> p.getTipo() == TipoPlan.BASIC).findFirst().orElseThrow();
                Plan enterprise = planRepository.findAll().stream()
                        .filter(p -> p.getTipo() == TipoPlan.ENTERPRISE).findFirst().orElseThrow();
                Pais espana = paisRepository.findAll().stream()
                        .filter(p -> p.getNombre().equals("España")).findFirst().orElseThrow();
                // Usar las variables ya definidas anteriormente: basic, enterprise, espana
                
                // Admin de prueba
                Usuario admin = new Usuario();
                admin.setNombre("Administrador");
                admin.setEmail("admin@saas.com");
                admin.setPassword(passwordEncoder.encode("admin123"));
                admin.setRol("ROLE_ADMIN");
                admin.setPlanActual(enterprise);
                usuarioRepository.save(admin);

                // Usuario de prueba
                Usuario usuario = new Usuario();
                usuario.setNombre("Usuario Demo");
                usuario.setEmail("demo@saas.com");
                usuario.setPassword(passwordEncoder.encode("demo123"));
                usuario.setRol("ROLE_USER");
                usuario.setPlanActual(basic);
                
                usuarioRepository.save(usuario);

                // Crear perfiles
                Perfil pAdmin = new Perfil();
                pAdmin.setDireccion("Oficina Central");
                pAdmin.setCiudad("Madrid");
                pAdmin.setTelefono("+34 900 000 000");
                pAdmin.setUsuario(admin);
                pAdmin.setPais(espana);
                admin.setPerfil(pAdmin); // Enlazar bidireccionalmente
                perfilRepository.save(pAdmin);

                Perfil perfil = new Perfil();
                perfil.setDireccion("Calle Principal 123");
                perfil.setCiudad("Madrid");
                perfil.setTelefono("+34 600 000 000");
                perfil.setUsuario(usuario);
                perfil.setPais(espana);
                usuario.setPerfil(perfil); // Enlazar bidireccionalmente
                perfilRepository.save(perfil);

                // Crear suscripción activa para el demo
                Suscripcion suscripcion = new Suscripcion();
                suscripcion.setUsuario(usuario);
                suscripcion.setPlan(basic);
                suscripcion.setEstado(EstadoSuscripcion.ACTIVA);
                suscripcion.setFechaInicio(LocalDate.now());
                suscripcion.setFechaFin(LocalDate.now().plusMonths(1));
                suscripcion.setFechaProximaRenovacion(LocalDate.now().plusMonths(1));
                suscripcionRepository.save(suscripcion);

                // Generar factura inicial
                facturaService.generarFactura(suscripcion);
                
                System.out.println("Usuarios creados:");
                System.out.println("- Admin: admin@saas.com / admin123");
                System.out.println("- User: demo@saas.com / demo123");
            }
            
            System.out.println("Carga de datos completada exitosamente");
            
        } catch (Exception e) {
            System.err.println("Error al cargar datos: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
