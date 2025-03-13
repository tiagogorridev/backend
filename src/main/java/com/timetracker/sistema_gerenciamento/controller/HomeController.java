package com.timetracker.sistema_gerenciamento.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class HomeController {

    @GetMapping("/")
    @ResponseBody
    public String home() {
        return "Sistema de gerenciamento de horas está em execução!";
    }

    @GetMapping("/health")
    @ResponseBody
    public String health() {
        return "Application is running!";
    }
}