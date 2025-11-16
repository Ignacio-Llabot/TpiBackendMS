package com.tpibackend.ms_contenedores.service;

import java.util.List;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.tpibackend.ms_contenedores.entity.Cliente;
import com.tpibackend.ms_contenedores.entity.Contenedor;
import com.tpibackend.ms_contenedores.entity.Estado;
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

    private static final Logger log = LoggerFactory.getLogger(SolicitudService.class);


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
        log.info("Recuperando todas las solicitudes");
        return solicitudRepository.findAll();
    }

    public Solicitud getSolicitudPorId(Integer id) {
        Objects.requireNonNull(id, "El id no puede ser nulo");
        return solicitudRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Solicitud {} no encontrada", id);
                    return new EntityNotFoundException("Solicitud no encontrada con id: " + id);
                });
    }

    // Logica necesaria para el requerimiento 1, no tengo ni puta idea si anda
    public Solicitud persistirSolicitud(Solicitud solicitud) {
        Objects.requireNonNull(solicitud, "la solicitud no puede ser nula");
        Solicitud guardada = solicitudRepository.save(solicitud);
        log.info("Solicitud {} persistida", guardada.getIdSolicitud());
        return guardada;
        }

    public void eliminarSolicitudPorId(Integer id) {
        Objects.requireNonNull(id, "El id no puede ser nulo");
        if (!solicitudRepository.existsById(id)) {
            throw new EntityNotFoundException("Solicitud no encontrada con id: " + id);
        }
        log.info("Eliminando solicitud {}", id);
        solicitudRepository.deleteById(id);
    }

    public Solicitud crearNuevaSolicitud(Solicitud solicitud) {
        Objects.requireNonNull(solicitud, "la solicitud no puede ser nula");
        log.info("Iniciando creación de solicitud");

        // --- CORRECCIÓN ---
        // Asignar un estado inicial al contenedor antes de persistirlo.
        Estado estadoContenedor = estadoService.getEstadoPorNombre("por retirar"); // O el estado inicial que corresponda
        solicitud.getContenedor().setEstado(estadoContenedor);
        log.debug("Estado inicial del contenedor asignado");

        // Creo el contenedor
        Contenedor contenedor = contenedorService.persistirContenedor(solicitud.getContenedor());
        solicitud.setContenedor(contenedor);
        log.info("Contenedor {} asociado a la nueva solicitud", contenedor.getIdContenedor());

        // Me fijo si el cliente ya existe, sino lo creo
        Cliente cliente = clienteService.findOrCreateCliente(solicitud.getCliente());
        solicitud.setCliente(cliente);
        log.info("Cliente asociado a la solicitud");

        // Le agrego un estado, pero no se bien como asignarle el default
        Estado estado = estadoService.getEstadoPorNombre("borrador");
        solicitud.setEstado(estado);

        solicitud = persistirSolicitud(solicitud);

        log.info("Solicitud {} creada correctamente", solicitud.getIdSolicitud());


        return solicitud;
    }

    public void actualizarCostoEstimado(Integer solicitudId, Double costoEstimado) {
        Objects.requireNonNull(solicitudId, "el id de la solicitud no puede ser nulo");
        Objects.requireNonNull(costoEstimado, "el costo estimado no puede ser nulo");
        log.info("Actualizando costo estimado para la solicitud {}", solicitudId);

        Solicitud solicitud = getSolicitudPorId(solicitudId);
        solicitud.setCostoEstimado(costoEstimado);
        persistirSolicitud(solicitud);
    }

    public void actualizarCostoFinal(Integer solicitudId, Double costoFinal) {
        Objects.requireNonNull(solicitudId, "el id de la solicitud no puede ser nulo");
        Objects.requireNonNull(costoFinal, "el costo final no puede ser nulo");
        log.info("Actualizando costo final para la solicitud {}", solicitudId);

        Solicitud solicitud = getSolicitudPorId(solicitudId);
        solicitud.setCostoFinal(costoFinal);
        persistirSolicitud(solicitud);
    }
}
