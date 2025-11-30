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
}
