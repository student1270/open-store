package ru.gb.config;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import ru.gb.model.Roles;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private static final Logger log = LoggerFactory.getLogger(SecurityConfig.class);

    private static final String ROLE_ADMIN = Roles.ADMIN.getValue().replace("ROLE_", "");
    private static final String ROLE_USER = Roles.USER.getValue().replace("ROLE_", "");

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/css/**", "/js/**", "/images/**", "/", "/index",
                                "/login", "/admin-login", "/home", "/home/**", "/error",
                                "/favicon.ico", "/product/**", "/cart", "/cart/add").permitAll()
                        .requestMatchers("/api/**").permitAll()
                        .requestMatchers("/register/**", "/check-user-details").anonymous()
                        .requestMatchers("/user").hasRole(ROLE_USER)
                        .requestMatchers("/admin").hasRole(ROLE_ADMIN)
                        .requestMatchers("/cart/checkout", "/cart/confirm").hasRole(ROLE_USER)
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/home", true)
                        .successHandler((request, response, authentication) -> {
                            HttpSession session = request.getSession(false);
                            log.info("Login successful. User: {}, Session ID: {}, Roles: {}",
                                    authentication.getName(), session != null ? session.getId() : "null", authentication.getAuthorities());
                            String redirectUrl = (String) session.getAttribute("redirectAfterLogin");
                            if (redirectUrl != null) {
                                session.removeAttribute("redirectAfterLogin");
                                response.sendRedirect(redirectUrl);
                            } else {
                                response.sendRedirect("/home");
                            }
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
                        .ignoringRequestMatchers("/api/**", "/cart/checkout", "/cart/confirm") // Test uchun CSRF o‘chirildi
                )
                .sessionManagement(session -> session
                                .sessionFixation().migrateSession()
                        // Test uchun maximumSessions o‘chirildi
                );

        return http.build();
    }
}