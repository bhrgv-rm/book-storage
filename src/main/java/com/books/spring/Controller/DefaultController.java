package com.books.spring.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class DefaultController {

  @Autowired
  private RequestMappingHandlerMapping requestMappingHandlerMapping;

  @GetMapping("/")
  public Map<String, String> getEndpoints() {
    Map<String, String> endpoints = new HashMap<>();

    // Retrieve all the handler methods from the requestMappingHandlerMapping
    requestMappingHandlerMapping.getHandlerMethods().forEach((key, value) -> {
      // Clean up the path formatting to remove square brackets
      String endpoint = key.getPatternValues().stream()
          .collect(Collectors.joining(", ")); // Join multiple patterns if there are any

      // Clean up the HTTP methods formatting (to remove brackets)
      String methods = key.getMethodsCondition().getMethods().stream()
          .map(Enum::name)
          .collect(Collectors.joining(", ")); // Join multiple methods with commas

      endpoints.put(endpoint, methods.isEmpty() ? "[]" : methods);
    });

    return endpoints;
  }
}
