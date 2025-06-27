package ru.gb.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import ru.gb.model.Roles;
import ru.gb.service.AdminDetailsService;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private static final String ROLE_ADMIN = Roles.ADMIN.getValue();
    private static final String ROLE_USER = Roles.USER.getValue();

    private final AdminDetailsService userDetailsService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/css/**", "/js/**", "/images/**", "/", "/index", "/login", "/home", "/home/**", "/error", "/favicon.ico", "/product/", "/product/**", "/cart", "/cart/**", "/register", "/register/**").permitAll()
                        .requestMatchers("/user").hasRole(ROLE_USER)
                        .requestMatchers("/admin").hasRole(ROLE_ADMIN)
                        .requestMatchers("/checkout").hasRole(ROLE_USER)
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .successHandler((request, response, authentication) -> {
                            String redirectUrl = request.getSession().getAttribute("redirectAfterLogin") != null
                                    ? (String) request.getSession().getAttribute("redirectAfterLogin")
                                    : authentication.getAuthorities().stream()
                                    .anyMatch(auth -> auth.getAuthority().equals("ROLE_" + ROLE_ADMIN))
                                    ? "/admin" : "/home";
                            response.sendRedirect(redirectUrl);
                        })
                        .failureUrl("/login?error=true")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutSuccessUrl("/")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                );
        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}