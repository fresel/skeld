package com.github.fresel.skeld.ui.server.service.impl;

import com.github.fresel.skeld.ui.server.model.Product;
import com.github.fresel.skeld.ui.server.service.ProductService;

import java.util.List;

public class DefaultProductService implements ProductService {

  @Override
  public List<Product> getAllProducts() {
    return fetchProductsFromDataSource();
  }

  private List<Product> fetchProductsFromDataSource() {
    // Implementation to fetch products from a data source (e.g., database, API)
    return List.of(
        new Product("1", "Product A", "Description for Product A", 19.99),
        new Product("2", "Product B", "Description for Product B", 29.99),
        new Product("3", "Product C", "Description for Product C", 39.99));
  }
}
