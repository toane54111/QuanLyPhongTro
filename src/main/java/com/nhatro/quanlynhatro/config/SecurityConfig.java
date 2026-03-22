package com.nhatro.quanlynhatro.config;

import com.nhatro.quanlynhatro.security.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;

    public SecurityConfig(CustomUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                // Tài nguyên tĩnh - cho phép tất cả
                .requestMatchers("/css/**", "/js/**", "/images/**", "/webjars/**").permitAll()
                // Trang đăng nhập - cho phép tất cả
                .requestMatchers("/login", "/error").permitAll()
                // Phân hệ Chủ trọ - chỉ CHU_TRO
                .requestMatchers("/landlord/**").hasRole("CHU_TRO")
                // Phân hệ Khách thuê - chỉ KHACH_THUE
                .requestMatchers("/tenant/**").hasRole("KHACH_THUE")
                // Tất cả request khác - phải đăng nhập
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .successHandler((request, response, authentication) -> {
                    // Chuyển hướng theo vai trò sau khi đăng nhập
                    var authorities = authentication.getAuthorities().toString();
                    if (authorities.contains("ROLE_CHU_TRO")) {
                        response.sendRedirect("/landlord/dashboard");
                    } else {
                        response.sendRedirect("/tenant/dashboard");
                    }
                })
                .failureUrl("/login?error=true")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout=true")
                .permitAll()
            );

        return http.build();
    }
}
