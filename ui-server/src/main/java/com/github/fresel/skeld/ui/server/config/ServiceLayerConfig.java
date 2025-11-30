package com.github.fresel.skeld.ui.server.config;

import com.github.fresel.skeld.ui.server.service.ProductService;
import com.github.fresel.skeld.ui.server.service.impl.DefaultProductService;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProvider;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProviderBuilder;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.web.client.RestClient;

import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
public class ServiceLayerConfig {

  @Bean
  OAuth2AuthorizedClientManager authorizedClientManager(
      ClientRegistrationRepository clientRegistrationRepository,
      OAuth2AuthorizedClientRepository authorizedClientRepository) {

    OAuth2AuthorizedClientProvider authorizedClientProvider = OAuth2AuthorizedClientProviderBuilder.builder()
        .authorizationCode()
        .refreshToken()
        .build();

    DefaultOAuth2AuthorizedClientManager authorizedClientManager = new DefaultOAuth2AuthorizedClientManager(
        clientRegistrationRepository, authorizedClientRepository);
    authorizedClientManager.setAuthorizedClientProvider(authorizedClientProvider);

    return authorizedClientManager;
  }

  @Bean
  RestClient restClient(OAuth2AuthorizedClientManager authorizedClientManager) {
    return RestClient.builder()
        .requestInterceptor((request, body, execution) -> {
          Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

          OAuth2AuthorizeRequest authorizeRequest = OAuth2AuthorizeRequest
              .withClientRegistrationId("keycloak")
              .principal(authentication)
              .build();

          OAuth2AuthorizedClient authorizedClient = authorizedClientManager.authorize(authorizeRequest);

          if (authorizedClient != null && authorizedClient.getAccessToken() != null) {
            String tokenValue = authorizedClient.getAccessToken().getTokenValue();
            log.debug("Using access token: {}", tokenValue);
            request.getHeaders().setBearerAuth(tokenValue);
          }

          return execution.execute(request, body);
        })
        .build();
  }

  /* Product Service */
  @Bean
  ProductService productService(RestClient restClient) {
    return new DefaultProductService(restClient);
  }
}
