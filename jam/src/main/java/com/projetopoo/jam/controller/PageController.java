package com.projetopoo.jam.controller;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class PageController {

    @GetMapping({"/", "/home"})
    public String home() {
        return "pages/home";
    }

    @GetMapping("/registerJam")
    public String registerJam() {return "pages/jam/registerJam";}

    @GetMapping("/jams/{id}")
    public String viewJam(@PathVariable Long id) {
        return "pages/jam/viewJam";
    }

    @GetMapping("/jams/registerGame/{id}")
    public String registerGame(@PathVariable Long id) {
        return "pages/game/registerGame";
    }

    @GetMapping("/viewGame/{id}")
    public String viewGame(@PathVariable Long id) {
        return "pages/game/viewGame";
    }

    @GetMapping("/updateGame/{id}")
    public String updateGame(@PathVariable Long id) {
        return "pages/game/updateGame";
    }

    @GetMapping("/updateJam/{id}")
    public String updateJam(@PathVariable Long id) {
        return "pages/jam/updateJam";
    }

    @GetMapping("/404")
    public String error() {
        return "pages/404";
    }

    @GetMapping("/login")
    public String login() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !(auth instanceof AnonymousAuthenticationToken)) {
            return "redirect:/home";
        }
        return "auth/login";
    }

    @GetMapping("/signup")
    public String signup() {
        return "auth/signup";
    }

}

