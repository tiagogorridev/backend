package com.timetracker.sistema_gerenciamento.service;

import com.timetracker.sistema_gerenciamento.model.Cliente;
import com.timetracker.sistema_gerenciamento.repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;

    public Cliente adicionarCliente(Cliente cliente) {
        if (cliente.getEmail() == null || cliente.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("O email do cliente não pode ser nulo ou vazio");
        }

        if (cliente.getNome() == null || cliente.getNome().trim().isEmpty()) {
            throw new IllegalArgumentException("O nome do cliente não pode ser nulo ou vazio");
        }

        if (!cliente.getEmail().matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")) {
            throw new IllegalArgumentException("O email do cliente não é válido");
        }

        if (cliente.getStatus() == null || cliente.getStatus().isEmpty()) {
            cliente.setStatus("ATIVO");
        }

        return clienteRepository.save(cliente);
    }

    public Cliente buscarPorEmail(String email) {
        return clienteRepository.findByEmail(email);
    }

    public List<Cliente> listarTodosClientes() {
        return clienteRepository.findAll();
    }
}