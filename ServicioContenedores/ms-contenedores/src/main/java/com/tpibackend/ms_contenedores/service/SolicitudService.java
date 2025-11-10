package com.tpibackend.ms_contenedores.service;

import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;

import com.tpibackend.ms_contenedores.entity.*;
import com.tpibackend.ms_contenedores.entity.Solicitud;
import com.tpibackend.ms_contenedores.repository.SolicitudRepository;

import jakarta.persistence.EntityNotFoundException;

@Service

// Este el servicio que mas logica tiene, ya que va a orquestar a los otros por asi decirlo
public class SolicitudService {

    private final SolicitudRepository solicitudRepository;

    private final ContenedorService contenedorService;

    private final EstadoService estadoService;

    private final ClienteService clienteService;

    public SolicitudService(SolicitudRepository solicitudRepository,
                            ContenedorService contenedorService,
                            EstadoService estadoService,
                            ClienteService clienteService) {
        
        this.solicitudRepository = solicitudRepository;
        this.contenedorService = contenedorService;
        this.estadoService = estadoService;
        this.clienteService = clienteService;
    }

    public List<Solicitud> getSolicitudes() {
        return solicitudRepository.findAll();
    }

    public Solicitud getSolicitudPorId(Integer id) {
        Objects.requireNonNull(id, "El id no puede ser nulo");
        return solicitudRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Solicitud no encontrada con id: " + id));
    }

    // Logica necesaria para el requerimiento 1, no tengo ni puta idea si anda
    public Solicitud persistirSolicitud(Solicitud solicitud) {
        Objects.requireNonNull(solicitud, "la solicitud no puede ser nula");
        return solicitudRepository.save(solicitud);
        }

    public void eliminarSolicitudPorId(Integer id) {
        Objects.requireNonNull(id, "El id no puede ser nulo");
        if (!solicitudRepository.existsById(id)) {
            throw new EntityNotFoundException("Solicitud no encontrada con id: " + id);
        }
        solicitudRepository.deleteById(id);
    }

    public Solicitud crearNuevaSolicitud(Solicitud solicitud) {

        // Creo el contenedor
        Contenedor contenedor = contenedorService.persistirContenedor(solicitud.getContenedor());
        solicitud.setContenedor(contenedor);

        // Me fijo si el cliente ya existe, sino lo creo
        Cliente cliente = clienteService.findOrCreateCliente(solicitud.getCliente());
        solicitud.setCliente(cliente);

        // Le agrego un estado, pero no se bien como asignarle el default
        Estado estado = estadoService.getEstadoPorNombre("borrador");
        solicitud.setEstado(estado);

        return persistirSolicitud(solicitud);
    }
}
