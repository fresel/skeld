package com.github.fresel.skeld.ui.server.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

  @GetMapping("/")
  public String home(Model model) {
    model.addAttribute("title", "Skeld UI Server");
    model.addAttribute("message", "Welcome to Skeld UI Server!");
    return "index";
  }

  @GetMapping("/public/logged-out")
  public String logout() {
    return "redirect:/logged-out";
  }
}
