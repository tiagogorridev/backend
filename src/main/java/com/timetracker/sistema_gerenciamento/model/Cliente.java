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

    @Column(name = "nome_cliente", nullable = false)
    @NotNull
    @NotEmpty(message = "O nome do cliente não pode ser nulo ou vazio")
    private String nomeCliente;

    @Column(name = "email_cliente", nullable = false, unique = true)
    @NotNull
    @NotEmpty(message = "O email do cliente não pode ser nulo ou vazio")
    private String emailCliente;

    // Getters e setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNomeCliente() {
        return nomeCliente;
    }

    public void setNomeCliente(String nomeCliente) {
        this.nomeCliente = nomeCliente;
    }

    public String getEmailCliente() {
        return emailCliente;
    }

    public void setEmailCliente(String emailCliente) {
        this.emailCliente = emailCliente;
    }
}