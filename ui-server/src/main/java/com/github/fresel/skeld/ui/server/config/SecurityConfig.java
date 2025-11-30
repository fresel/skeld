package com.github.fresel.skeld.ui.server.config;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

  @Bean
  SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/public/**", "/error", "/logged-out", "/access-denied", "/", "/webjars/**").permitAll()
            .anyRequest().authenticated())
        .oauth2Login(oauth2 -> oauth2
            .userInfoEndpoint(userInfo -> userInfo
                .oidcUserService(this.oidcUserService()))
            .defaultSuccessUrl("/", true))
        .exceptionHandling(exceptions -> exceptions
            .accessDeniedHandler((request, response, accessDeniedException) -> {
              response.sendRedirect("/access-denied");
            }))
        .logout(logout -> logout
            .logoutUrl("/logout")
            .logoutSuccessUrl("/logged-out")
            .invalidateHttpSession(true)
            .clearAuthentication(true)
            .deleteCookies("JSESSIONID", "KEYCLOAK_SESSION", "KEYCLOAK_IDENTITY", "KEYCLOAK_IDENTITY_LEGACY")
            .permitAll());

    return http.build();
  }

  @Bean
  OAuth2UserService<OidcUserRequest, OidcUser> oidcUserService() {
    final OidcUserService delegate = new OidcUserService();

    return (userRequest) -> {
      OidcUser oidcUser = delegate.loadUser(userRequest);

      // Extract scopes from the access token and map them to authorities
      Set<GrantedAuthority> mappedAuthorities = new HashSet<>(oidcUser.getAuthorities());

      // Get scopes from the access token
      Collection<String> scopes = userRequest.getAccessToken().getScopes();

      // Map each scope to a SCOPE_ authority
      Set<GrantedAuthority> scopeAuthorities = scopes.stream()
          .map(scope -> new SimpleGrantedAuthority("SCOPE_" + scope))
          .collect(Collectors.toSet());

      mappedAuthorities.addAll(scopeAuthorities);

      return new DefaultOidcUser(mappedAuthorities, oidcUser.getIdToken(), oidcUser.getUserInfo());
    };
  }
}
