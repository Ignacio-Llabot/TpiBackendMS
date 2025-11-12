package com.tpibackend.ms_contenedores.service;

import java.util.Date;
import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tpibackend.ms_contenedores.entity.*;
import com.tpibackend.ms_contenedores.repository.SolicitudRepository;

import io.swagger.v3.core.util.Json;
import jakarta.persistence.EntityNotFoundException;

@Service

// Este el servicio que mas logica tiene, ya que va a orquestar a los otros por asi decirlo
public class SolicitudService {

    @Value("${ms.transportes.url}")
    private String MS_TRANSPORTES_URL;

    private final SolicitudRepository solicitudRepository;

    private final ContenedorService contenedorService;

    private final EstadoService estadoService;

    private final ClienteService clienteService;

    private final ObjectMapper objectMapper;

    public SolicitudService(SolicitudRepository solicitudRepository,
                            ContenedorService contenedorService,
                            EstadoService estadoService,
                            ClienteService clienteService,
                            ObjectMapper objectMapper) {
        
        this.solicitudRepository = solicitudRepository;
        this.contenedorService = contenedorService;
        this.estadoService = estadoService;
        this.clienteService = clienteService;
        this.objectMapper = objectMapper;
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

    public Solicitud crearNuevaSolicitud(String solicitudJson) {

        //llega json y lo separo

        Solicitud solicitud = null;
        String ubicacionInicial = null;
        String ubicacionFinal = null;
        Date fechaHoraInicio = null;

        try {
            JsonNode rootNode = objectMapper.readTree(solicitudJson);

            JsonNode solicitudNode = rootNode.path("solicitud");
            solicitud = objectMapper.treeToValue(solicitudNode, Solicitud.class);

            JsonNode ubicacionInicialNode = rootNode.path("ubicacionInicial");
            JsonNode ubicacionFinalNode = rootNode.path("ubicacionFinal");

            ubicacionInicial = objectMapper.writeValueAsString(ubicacionInicialNode);
            ubicacionFinal = objectMapper.writeValueAsString(ubicacionFinalNode);

            JsonNode fechaHoraInicioNode = rootNode.path("fechaHoraInicio");
            fechaHoraInicio = objectMapper.treeToValue(fechaHoraInicioNode, Date.class);

        } catch (Exception e) {
            throw new RuntimeException("Error al parsear el JSON de la solicitud", e);
        }

        // --- CORRECCIÓN ---
        // Asignar un estado inicial al contenedor antes de persistirlo.
        Estado estadoContenedor = estadoService.getEstadoPorNombre("disponible"); // O el estado inicial que corresponda
        solicitud.getContenedor().setEstado(estadoContenedor);

        // Creo el contenedor
        Contenedor contenedor = contenedorService.persistirContenedor(solicitud.getContenedor());
        solicitud.setContenedor(contenedor);

        // Me fijo si el cliente ya existe, sino lo creo
        Cliente cliente = clienteService.findOrCreateCliente(solicitud.getCliente());
        solicitud.setCliente(cliente);

        // Le agrego un estado, pero no se bien como asignarle el default
        Estado estado = estadoService.getEstadoPorNombre("borrador");
        solicitud.setEstado(estado);

        solicitud = persistirSolicitud(solicitud);

        // Llamar para crear una nueva ruta
        RestTemplate restTemplate = new RestTemplate();
        String url = MS_TRANSPORTES_URL + "/api/v1/rutas/" + solicitud.getIdSolicitud();

        // Crear el JSON para enviar
        try {
            // Parsear las ubicaciones (ya están en formato JSON string)
            JsonNode ubicacionInicialJson = objectMapper.readTree(ubicacionInicial);
            JsonNode ubicacionFinalJson = objectMapper.readTree(ubicacionFinal);
            JsonNode fechaHoraInicioJson = objectMapper.valueToTree(fechaHoraInicio);
            
            // Crear el objeto con la estructura que necesitas
            var requestBody = objectMapper.createObjectNode();
            requestBody.set("ubicacionInicial", ubicacionInicialJson);
            requestBody.set("ubicacionFinal", ubicacionFinalJson);
            requestBody.set("fechaHoraInicio", fechaHoraInicioJson); // Enviar como Date
            
            // Hacer el POST
            restTemplate.postForObject(url, requestBody, String.class);
            
        } catch (Exception e) {
            throw new RuntimeException("Error al crear la ruta en el servicio de transportes", e);
        }

        return solicitud;
    }
}
