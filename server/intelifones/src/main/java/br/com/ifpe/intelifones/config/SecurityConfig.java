package br.com.ifpe.intelifones.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final UserDetailsService userDetailsService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .cors(cors -> cors.configurationSource(request -> {
                var config = new org.springframework.web.cors.CorsConfiguration();
                config.setAllowedOrigins(java.util.List.of("*"));
                config.setAllowedMethods(java.util.List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
                config.setAllowedHeaders(java.util.List.of("*"));
                return config;
            }))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                // Auth — público
                .requestMatchers("/api/auth/**").permitAll()
                
                // Frete — autenticado
                .requestMatchers("/api/frete/**").permitAll()

                // Swagger
                .requestMatchers("/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()

                // Uploads (servir imagens sem auth)
                .requestMatchers("/uploads/**").permitAll()

                // Categorias — leitura pública
                .requestMatchers(HttpMethod.GET, "/api/categorias/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/categorias").permitAll()
                .requestMatchers(HttpMethod.PUT, "/api/categorias/**").permitAll()
                .requestMatchers(HttpMethod.DELETE, "/api/categorias/**").permitAll()

                // Produtos — leitura pública
                .requestMatchers(HttpMethod.GET, "/api/produtos").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/produtos/disponiveis").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/produtos/buscar").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/produtos/categoria/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/produtos/{id}").permitAll()
                // Produtos — escrita somente VENDEDOR
                .requestMatchers(HttpMethod.POST, "/api/produtos").hasRole("VENDEDOR")
                .requestMatchers(HttpMethod.PUT, "/api/produtos/**").hasRole("VENDEDOR")
                .requestMatchers(HttpMethod.DELETE, "/api/produtos/**").hasRole("VENDEDOR")
                .requestMatchers("/api/produtos/meus").hasRole("VENDEDOR")
                .requestMatchers("/api/produtos/*/repor").hasRole("VENDEDOR")
                .requestMatchers(HttpMethod.POST, "/api/produtos/*/imagem").hasRole("VENDEDOR")
                // Compra de produto — autenticado
                .requestMatchers("/api/produtos/*/comprar").authenticated()

                // Carrinho — autenticado
                .requestMatchers("/api/carrinho/**").authenticated()

                // Pedidos — autenticado; vendas só para VENDEDOR
                .requestMatchers("/api/pedidos/vendas").hasRole("VENDEDOR")
                .requestMatchers("/api/pedidos/**").authenticated()

                

                // Usuários — autenticado
                .requestMatchers("/api/usuarios/**").authenticated()

                // Favoritos — autenticado
                .requestMatchers("/api/favoritos/**").authenticated()

                .anyRequest().authenticated()
            )
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authenticationProvider(authenticationProvider())
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
