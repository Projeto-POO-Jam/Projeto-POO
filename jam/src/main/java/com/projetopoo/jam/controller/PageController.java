package com.projetopoo.jam.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    @GetMapping({"/", "/home"})
    public String home() {
        return "Home";
    }

    @GetMapping("/login")
    public String login() {
        return "auth/Login";
    }

    @GetMapping("/signup")
    public String signup() {
        return "auth/Signup";
    }

}

