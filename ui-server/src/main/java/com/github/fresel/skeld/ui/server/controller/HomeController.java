package com.github.fresel.skeld.ui.server.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

  @GetMapping("/")
  public String home(Model model, @AuthenticationPrincipal OidcUser user,
      @AuthenticationPrincipal(expression = "idToken.tokenValue") String accessToken) {
    if (user != null) {
      model.addAttribute("username", user.getPreferredUsername());
      model.addAttribute("email", user.getEmail());
      model.addAttribute("accessToken", accessToken);
      model.addAttribute("message", "Welcome to Skeld!");
    } else {
      model.addAttribute("message", "Hello, Guest!");
    }
    return "index";
  }

  @GetMapping("/public/logged-out")
  public String logout() {
    return "redirect:/logged-out";
  }
}
