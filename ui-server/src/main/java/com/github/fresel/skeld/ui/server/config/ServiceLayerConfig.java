package com.github.fresel.skeld.ui.server.config;

import com.github.fresel.skeld.ui.server.service.ProductService;
import com.github.fresel.skeld.ui.server.service.impl.DefaultProductService;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ServiceLayerConfig {

  /* Product Service */
  @Bean
  ProductService productService() {
    return new DefaultProductService();
  }
}
