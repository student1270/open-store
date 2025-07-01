package ru.gb.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import ru.gb.model.Roles;
import ru.gb.service.UserService;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private static final String ROLE_ADMIN = Roles.ADMIN.getValue().replace("ROLE_", "");
    private static final String ROLE_USER = Roles.USER.getValue().replace("ROLE_", "");

    private final UserService userDetailsService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/css/**", "/js/**", "/images/**", "/", "/index", "/login", "/admin-login",
                                "/home", "/home/**", "/error", "/favicon.ico", "/product/", "/product/**",
                                "/cart", "/cart/**").permitAll()
                        .requestMatchers("/api/**").permitAll()
                        .requestMatchers("/register", "/register/**", "/check-user-details").anonymous()
                        .requestMatchers("/user").hasRole(ROLE_USER)
                        .requestMatchers("/admin").hasRole(ROLE_ADMIN)
                        .requestMatchers("/checkout").hasRole(ROLE_USER)
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/home", true)
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
                        .ignoringRequestMatchers("/api/**") // API uchun CSRF ni o'chirish
                );

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }
}