package ru.gb.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import ru.gb.model.Roles;
import ru.gb.service.AdminDetailsService;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@Order(1)
public class RestSecurityConfig {

    private final AdminDetailsService adminDetailsService;

    private static final String ROLE_ORDER_ADMIN = Roles.ORDER_ADMIN.getValue().replace("ROLE_", "");
    private static final String ROLE_USER = Roles.USER.getValue().replace("ROLE_", "");
    private static final String ROLE_WAREHOUSE_ADMIN = Roles.WAREHOUSE_ADMIN.getValue().replace("ROLE_", "");

    @Bean
    public SecurityFilterChain restSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/api/**")
                .userDetailsService(adminDetailsService)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/api/admin-login",
                                "/api/home",
                                "/api/product/**",
                                "/api/check-email",
                                "/api/send-sms",
                                "/api/verify-sms"
                        ).permitAll()
                        .requestMatchers("/api/check-email", "/api/send-sms", "/api/verify-sms").anonymous()
                        .requestMatchers("/api/user/**", "/api/orders/**").hasRole(ROLE_USER)
                        .requestMatchers("/api/order-admin/**").hasRole(ROLE_ORDER_ADMIN)
                        .requestMatchers("/api/warehouse-admin/**").hasRole(ROLE_WAREHOUSE_ADMIN)
                        .anyRequest().authenticated()
                )
                .httpBasic(httpBasic -> httpBasic.realmName("API Realm"))
                .csrf(AbstractHttpConfigurer::disable) // csrf -> csrf.disable() bo'ladi bizada. Lambda bo'gani uchun shunaqa holatga keb qoldi.
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}