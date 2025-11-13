package com.tpibackend.ms_contenedores.service;

import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;
import com.tpibackend.ms_contenedores.entity.Cliente;
import com.tpibackend.ms_contenedores.repository.ClienteRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;

    public ClienteService(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    public List<Cliente> getCamiones() {
        return clienteRepository.findAll();
    }

    public Cliente getClientePorDni(String dni) {
        Objects.requireNonNull(dni, "el DNI no puede ser nulo");
        return clienteRepository.findById(dni)
            .orElseThrow(() -> new EntityNotFoundException("Cliente no encontrado con DNI: " + dni));
    }

    public Cliente persistirCliente(Cliente cliente) {
        Objects.requireNonNull(cliente, "el cliente no puede ser nulo");
        return clienteRepository.save(cliente);
    }  // persistir cliente sirve para el registro y para la modificacion

    public Cliente findOrCreateCliente(Cliente cliente) {
        Objects.requireNonNull(cliente, "el cliente no puede ser nulo");
        final String dni = Objects.requireNonNull(cliente.getDni(), "el DNI del cliente no puede ser nulo");
        return clienteRepository.findById(dni)
            .orElseGet(() -> clienteRepository.save(cliente));
        
        // Metodo que voy a necesitar para el req 1.2, si no existe el cliente lo crea, si no, lo devuelve.
    }

    public void eliminarClientePorDni(String dni) {
        Objects.requireNonNull(dni, "el DNI no puede ser nulo");
        if (!clienteRepository.existsById(dni)) {
            throw new EntityNotFoundException("Cliente no encontrado con DNI: " + dni);
        }
        clienteRepository.deleteById(dni);
    }

}