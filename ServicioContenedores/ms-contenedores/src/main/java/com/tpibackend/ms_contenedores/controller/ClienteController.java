package com.tpibackend.ms_contenedores.controller;

import com.tpibackend.ms_contenedores.entity.Cliente;
import com.tpibackend.ms_contenedores.service.ClienteService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/clientes")
public class ClienteController {

    private final ClienteService clienteService;

    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    // Definimos endpoints

    @GetMapping("/{dni}")
    public ResponseEntity<Cliente> getClientePorDni(@PathVariable String dni) {
        Cliente cliente = clienteService.getClientePorDni(dni);
        return ResponseEntity.ok(cliente); // 200 OK
    }

    @PostMapping
    public ResponseEntity<Cliente> postCliente(@RequestBody Cliente cliente) {
        Cliente clienteCreado = clienteService.persistirCliente(cliente);
        return ResponseEntity.status(HttpStatus.CREATED).body(clienteCreado); // 201 Created
    }

}