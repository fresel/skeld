package com.github.fresel.skeld.ui.server.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ErrorController {

  @GetMapping("/access-denied")
  public String accessDenied(Model model) {
    model.addAttribute("message", "You don't have permission to access this resource.");
    model.addAttribute("details", "Please contact your administrator to request the necessary permissions.");
    return "access-denied";
  }

  @GetMapping("/logged-out")
  public String loggedOut(Model model) {
    model.addAttribute("message", "You have been successfully logged out.");
    return "logged-out";
  }

  @GetMapping("/auth-error")
  public String authError(Model model) {
    model.addAttribute("message", "Authentication failed.");
    model.addAttribute("details", "There was a problem logging you in. Please try again or contact support.");
    return "auth-error";
  }
}
