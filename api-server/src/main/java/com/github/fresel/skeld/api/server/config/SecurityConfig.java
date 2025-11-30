package com.github.fresel.skeld.api.server.config;

import java.util.Collection;
import java.util.Map;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

  @Bean
  SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/public/**").permitAll()
            .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/products")
            .hasAuthority("SCOPE_Products.Read")
            .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/products")
            .hasAuthority("SCOPE_Products.Write")
            .anyRequest().authenticated())
        .oauth2ResourceServer(oauth2 -> oauth2
            .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter())))
        .sessionManagement(session -> session
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

    return http.build();
  }

  @Bean
  JwtAuthenticationConverter jwtAuthenticationConverter() {
    JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
    converter.setJwtGrantedAuthoritiesConverter(jwt -> {
      Collection<GrantedAuthority> authorities = new java.util.ArrayList<>();

      // Extract realm roles from Keycloak token
      Map<String, Object> realmAccess = jwt.getClaim("realm_access");
      if (realmAccess != null) {
        @SuppressWarnings("unchecked")
        Collection<String> roles = (Collection<String>) realmAccess.get("roles");
        if (roles != null) {
          roles.stream()
              .map(role -> new SimpleGrantedAuthority("ROLE_" + role.toUpperCase()))
              .forEach(authorities::add);
        }
      }

      // Extract scopes from the token
      String scopeClaim = jwt.getClaimAsString("scope");
      if (scopeClaim != null && !scopeClaim.isEmpty()) {
        String[] scopes = scopeClaim.split(" ");
        for (String scope : scopes) {
          authorities.add(new SimpleGrantedAuthority("SCOPE_" + scope));
        }
      }

      return authorities;
    });

    return converter;
  }
}
