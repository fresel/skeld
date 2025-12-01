package com.github.fresel.skeld.ui.server.exception;

import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;

import lombok.extern.slf4j.Slf4j;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

  @ExceptionHandler(HttpClientErrorException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public String handleHttpClientError(HttpClientErrorException ex, Model model) {
    log.error("Client error occurred: {}", ex.getMessage(), ex);
    model.addAttribute("message", "Request Error");
    model.addAttribute("details", "There was a problem with your request: " + ex.getStatusText());
    return "error";
  }

  @ExceptionHandler(HttpServerErrorException.class)
  @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
  public String handleHttpServerError(HttpServerErrorException ex, Model model) {
    log.error("Server error occurred: {}", ex.getMessage(), ex);
    model.addAttribute("message", "Service Error");
    model.addAttribute("details", "The service is temporarily unavailable. Please try again later.");
    return "error";
  }

  @ExceptionHandler(ResourceAccessException.class)
  @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
  public String handleResourceAccessError(ResourceAccessException ex, Model model) {
    log.error("Resource access error: {}", ex.getMessage(), ex);
    model.addAttribute("message", "Connection Error");
    model.addAttribute("details", "Unable to connect to the service. Please try again later.");
    return "error";
  }

  @ExceptionHandler(Exception.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public String handleGenericError(Exception ex, Model model) {
    log.error("Unexpected error occurred: {}", ex.getMessage(), ex);
    model.addAttribute("message", "Unexpected Error");
    model.addAttribute("details", "An unexpected error occurred. Please try again or contact support.");
    return "error";
  }
}
