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
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.security.config.annotation.web.configurers.ExceptionHandlingConfigurer;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.config.annotation.web.configurers.oauth2.client.OAuth2LoginConfigurer;
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

  private static final String[] PUBLIC_PATHS = {
      "/public/**", "/error", "/logged-out", "/access-denied", "/", "/webjars/**"
  };

  private static final String[] COOKIES_TO_DELETE = {
      "JSESSIONID", "KEYCLOAK_SESSION", "KEYCLOAK_IDENTITY", "KEYCLOAK_IDENTITY_LEGACY"
  };

  @Bean
  SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    return http
        .authorizeHttpRequests(this::configureAuthorization)
        .oauth2Login(this::configureOAuth2Login)
        .exceptionHandling(this::configureExceptionHandling)
        .logout(this::configureLogout)
        .build();
  }

  @Bean
  OAuth2UserService<OidcUserRequest, OidcUser> oidcUserService() {
    OidcUserService delegate = new OidcUserService();
    return userRequest -> enrichUserWithScopes(delegate.loadUser(userRequest), userRequest);
  }

  private void configureAuthorization(
      AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry auth) {
    auth.requestMatchers(PUBLIC_PATHS).permitAll()
        .anyRequest().authenticated();
  }

  private void configureOAuth2Login(OAuth2LoginConfigurer<HttpSecurity> oauth2) {
    oauth2.userInfoEndpoint(userInfo -> userInfo.oidcUserService(oidcUserService()))
        .defaultSuccessUrl("/", true);
  }

  private void configureExceptionHandling(ExceptionHandlingConfigurer<HttpSecurity> exceptions) {
    exceptions.accessDeniedHandler((request, response, ex) -> response.sendRedirect("/access-denied"));
  }

  private void configureLogout(LogoutConfigurer<HttpSecurity> logout) {
    logout
        .logoutSuccessUrl("/")
        .invalidateHttpSession(true)
        .clearAuthentication(true)
        .deleteCookies(COOKIES_TO_DELETE)
        .permitAll();
  }

  private OidcUser enrichUserWithScopes(OidcUser oidcUser, OidcUserRequest userRequest) {
    Set<GrantedAuthority> authorities = new HashSet<>(oidcUser.getAuthorities());
    authorities.addAll(extractScopeAuthorities(userRequest.getAccessToken().getScopes()));
    return new DefaultOidcUser(authorities, oidcUser.getIdToken(), oidcUser.getUserInfo());
  }

  private Set<GrantedAuthority> extractScopeAuthorities(Collection<String> scopes) {
    return scopes.stream()
        .map(scope -> new SimpleGrantedAuthority("SCOPE_" + scope))
        .collect(Collectors.toSet());
  }
}
