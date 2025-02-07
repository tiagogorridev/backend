package com.timetracker.sistema_gerenciamento.controller;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/testee")

public class teste {

    @GetMapping
    public String getOi() {
        return "oi";
    }
}
