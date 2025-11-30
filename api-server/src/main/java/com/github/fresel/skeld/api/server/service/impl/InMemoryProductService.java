package com.github.fresel.skeld.api.server.service.impl;

import com.github.fresel.skeld.api.server.domain.Product;
import com.github.fresel.skeld.api.server.service.ProductService;

import java.util.Collection;
import java.util.List;

public class InMemoryProductService implements ProductService {

  private List<Product> products;

  public InMemoryProductService() {
    this.products = List.copyOf(createProducts());
  }

  @Override
  public List<Product> getAllProducts() {
    return products;
  }

  private static Collection<Product> createProducts() {
    return List.of(
        new Product("prod-1", "Product 1", "Description for product 1"),
        new Product("prod-2", "Product 2", "Description for product 2"),
        new Product("prod-3", "Product 3", "Description for product 3"));
  }
}
