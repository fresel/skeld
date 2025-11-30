package com.github.fresel.skeld.api.server.config;

import com.github.fresel.skeld.api.server.service.ProductService;
import com.github.fresel.skeld.api.server.service.impl.InMemoryProductService;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ServiceLayerConfig {

  @Bean
  ProductService productService() {
    return new InMemoryProductService();
  }
}
