package com.github.fresel.skeld.api.server.domain;

import lombok.Value;

@Value
public class Product {
  private final String id;
  private final String name;
  private final String description;
}
