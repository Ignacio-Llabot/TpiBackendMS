package com.tpibackend.ms_contenedores.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tpibackend.ms_contenedores.entity.Cliente;
import com.tpibackend.ms_contenedores.service.ClienteService;

import jakarta.persistence.EntityNotFoundException;

@RestController
@RequestMapping("/api/v1/clientes")
public class ClienteController {

    private final ClienteService clienteService;
    private static final Logger log = LoggerFactory.getLogger(ClienteController.class);

    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    // Definimos endpoints

    @GetMapping("/{dni}")
    public ResponseEntity<Cliente> getClientePorDni(@PathVariable String dni) {
        try {
            log.info("Buscando cliente por DNI");
            Cliente cliente = clienteService.getClientePorDni(dni);
            log.info("Cliente encontrado");
            return ResponseEntity.ok(cliente); // 200 OK
        } catch (EntityNotFoundException e) {
            log.warn("Cliente no encontrado: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<Cliente> postCliente(@RequestBody Cliente cliente) {
        log.info("Creando cliente");
        Cliente clienteCreado = clienteService.persistirCliente(cliente);
        log.info("Cliente creado");
        return ResponseEntity.status(HttpStatus.CREATED).body(clienteCreado); // 201 Created
    }

}