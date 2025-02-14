package com.timetracker.sistema_gerenciamento.model;

import jakarta.persistence.*;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "clientes")
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nome", nullable = false)
    @NotNull
    @NotEmpty(message = "O nome do cliente não pode ser nulo ou vazio")
    private String nome;

    @Column(name = "email", nullable = false, unique = true)
    @NotNull
    @NotEmpty(message = "O email do cliente não pode ser nulo ou vazio")
    private String email;

    @Column(name = "status", nullable = false, columnDefinition = "ENUM('ATIVO', 'INATIVO') DEFAULT 'ATIVO'")
    private String status = "ATIVO";

    // Getters e setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}