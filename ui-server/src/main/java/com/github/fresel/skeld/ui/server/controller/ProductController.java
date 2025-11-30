package com.github.fresel.skeld.ui.server.controller;

import com.github.fresel.skeld.ui.server.service.ProductService;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequestMapping("/products")
@Controller
@RequiredArgsConstructor
@Slf4j
public class ProductController {

  private final ProductService productService;

  @GetMapping
  @PreAuthorize("hasAuthority('SCOPE_Products.Read')")
  public String getMethodName(Model model) {
    model.addAttribute("products", productService.getAllProducts());
    return "product";
  }

}
