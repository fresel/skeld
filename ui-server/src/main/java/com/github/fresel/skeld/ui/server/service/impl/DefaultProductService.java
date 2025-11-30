package com.github.fresel.skeld.ui.server.service.impl;

import com.github.fresel.skeld.ui.server.model.Product;
import com.github.fresel.skeld.ui.server.service.ProductService;
import com.github.fresel.skeld.ui.server.service.dto.ProductDto;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.client.RestClient;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DefaultProductService implements ProductService {

  private final RestClient restClient;

  @Value("${api.server.base-url}")
  private String apiServerBaseUrl;

  @Override
  public List<Product> getAllProducts() {
    // log the access token for debugging purposes
    List<ProductDto> productDtos = restClient.get()
        .uri(apiServerBaseUrl + "/api/products")
        .retrieve()
        .body(new ParameterizedTypeReference<List<ProductDto>>() {
        });
    return productDtos.stream()
        .map(dto -> new Product(dto.getId(), dto.getName(), dto.getDescription(), randomPrice()))
        .toList();
  }

  private double randomPrice() {
    return Math.round((10 + Math.random() * 90) * 100.0) / 100.0;
  }
}
