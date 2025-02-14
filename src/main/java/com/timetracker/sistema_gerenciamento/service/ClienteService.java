package com.timetracker.sistema_gerenciamento.service;

import com.timetracker.sistema_gerenciamento.model.Cliente;
import com.timetracker.sistema_gerenciamento.repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;

    public Cliente adicionarCliente(Cliente cliente) {
        if (cliente.getEmailCliente() == null || cliente.getEmailCliente().trim().isEmpty()) {
            throw new IllegalArgumentException("O email do cliente não pode ser nulo ou vazio");
        }

        if (cliente.getNomeCliente() == null || cliente.getNomeCliente().trim().isEmpty()) {
            throw new IllegalArgumentException("O nome do cliente não pode ser nulo ou vazio");
        }

        if (!cliente.getEmailCliente().matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")) {
            throw new IllegalArgumentException("O email do cliente não é válido");
        }

        return clienteRepository.save(cliente);
    }

    public Cliente buscarPorEmail(String email) {
        return clienteRepository.findByEmailCliente(email);
    }
}