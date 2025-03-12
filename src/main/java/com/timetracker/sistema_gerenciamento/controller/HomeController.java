package com.timetracker.sistema_gerenciamento.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.view.RedirectView;

@Controller
public class HomeController {

    @GetMapping("/")
    public RedirectView redirectToSwaggerUi() {
        return new RedirectView("/swagger-ui.html");
    }

    @GetMapping("/health")
    @ResponseBody
    public String health() {
        return "Application is running!";
    }
}