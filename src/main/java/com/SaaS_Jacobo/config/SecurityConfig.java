package com.SaaS_Jacobo.config;

import com.SaaS_Jacobo.repository.UsuarioRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final UsuarioRepository usuarioRepository;

    public SecurityConfig(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/usuarios/nuevo", "/usuarios/guardar", "/css/**", "/js/**").permitAll()
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .successHandler(customSuccessHandler())
                .permitAll()
            )
            .logout(logout -> logout
                .logoutSuccessUrl("/login?logout")
                .permitAll()
            );
        
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return email -> usuarioRepository.encontrarPorEmail(email)
                .map(u -> User.withUsername(u.getEmail())
                        .password(u.getPassword())
                        .roles(u.getRol().replace("ROLE_", ""))
                        .build())
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
    }

    @Bean
    public AuthenticationSuccessHandler customSuccessHandler() {
        return (request, response, authentication) -> {
            boolean isAdmin = authentication.getAuthorities().stream()
                    .anyMatch(r -> r.getAuthority().equals("ROLE_ADMIN"));
            
            if (isAdmin) {
                response.sendRedirect("/admin/auditoria");
            } else {
                // Si es un usuario normal, lo llevamos a su suscripción
                // Para simplificar, buscamos al usuario por email y redirigimos
                String email = authentication.getName();
                usuarioRepository.encontrarPorEmail(email).ifPresent(u -> {
                    try {
                        response.sendRedirect("/suscripciones/ver/" + u.getId());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }
        };
    }
}
