package ru.gb.config;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import ru.gb.model.Roles;
import ru.gb.service.AdminDetailsService;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private static final Logger log = LoggerFactory.getLogger(SecurityConfig.class);

    private final AdminDetailsService adminDetailsService;

    private static final String ROLE_ORDER_ADMIN = Roles.ORDER_ADMIN.getValue().replace("ROLE_", "");
    private static final String ROLE_USER = Roles.USER.getValue().replace("ROLE_", "");
    private static final String ROLE_WAREHOUSE_ADMIN = Roles.WAREHOUSE_ADMIN.getValue().replace("ROLE_", "");

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .userDetailsService(adminDetailsService)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/css/**", "/js/**", "/images/**", "/", "/index",
                                "/login", "/admin-login", "/home", "/home/**", "/error",
                                "/favicon.ico", "/product/**", "/cart", "/cart/add", "/{productId}/reviews", "/reviews", "/reviews/**"
                        ).permitAll()
                        .requestMatchers("/api/**", "/ws/**").permitAll()
                        .requestMatchers("/register/**", "/check-user-details").anonymous()
                        .requestMatchers("/actuator/**").permitAll()
                        .requestMatchers("/user").hasRole(ROLE_USER)
                        .requestMatchers("/order-admin", "/order-admin/**").hasRole(ROLE_ORDER_ADMIN)
                        .requestMatchers("/warehouse-admin").hasRole(ROLE_WAREHOUSE_ADMIN)
                        .requestMatchers("/cart/checkout", "/cart/confirm").hasRole(ROLE_USER)
                        .anyRequest().authenticated()
                )

                .formLogin(form -> form
                        .loginPage("/admin-login")
                        .loginProcessingUrl("/login")
                        .defaultSuccessUrl("/home", true)
                        .successHandler((request, response, authentication) -> {
                            HttpSession session = request.getSession(false);
                            log.info("Login successful. User: {}, Session ID: {}, Roles: {}",
                                    authentication.getName(),
                                    session != null ? session.getId() : "null",
                                    authentication.getAuthorities());
                            Object principal = authentication.getPrincipal();
                            String redirectUrl = "/home";
                            if (principal instanceof ru.gb.service.impl.AdminDetails adminDetails) {
                                String role = adminDetails.getAdmin().getRole().getValue();
                                switch (role) {
                                    case "ORDER_ADMIN" -> redirectUrl = "/order-admin";
                                    case "WAREHOUSE_ADMIN" -> redirectUrl = "/warehouse-admin";
                                }
                            }
                            response.sendRedirect(redirectUrl);
                        })
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutSuccessUrl("/")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                )
                .csrf(csrf -> csrf
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                        .ignoringRequestMatchers("/api/**", "/cart/checkout", "/cart/confirm", "/ws/**")
                )
                .sessionManagement(session -> session
                        .sessionFixation().migrateSession()
                        .maximumSessions(1)
                        .maxSessionsPreventsLogin(false)
                );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
