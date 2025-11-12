package org.tpibackend.mstransportes.service;

import java.sql.Date;
import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.tpibackend.mstransportes.entity.Ruta;
import org.tpibackend.mstransportes.entity.Ubicacion;
import org.tpibackend.mstransportes.repository.RutaRepository;
import org.tpibackend.mstransportes.entity.Tramo;
import org.tpibackend.mstransportes.service.TramoService;

import jakarta.persistence.EntityNotFoundException;


@Service
public class RutaService {
    
    @Value("${ms.contenedores.url}")
    private String URL_MS_CONTENEDORES;


    private final RutaRepository rutaRepository;

    private final TramoService tramosService;

    public RutaService(RutaRepository rutaRepository, TramoService tramosService) {
        this.rutaRepository = rutaRepository;
        this.tramosService = tramosService;
    }

    public Ruta getRutaPorId(Integer id) {
        Objects.requireNonNull(id, "la id no puede ser nula");
        return rutaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Ruta no encontrada con id: " + id));
    }

    public Ruta persistirRuta(Ruta ruta) {
        Objects.requireNonNull(ruta, "la ruta no puede ser nula");
        return rutaRepository.save(ruta);
    }

    public void eliminarRutaPorId(Integer id) {
        Objects.requireNonNull(id, "la id no puede ser nula");
        if (!rutaRepository.existsById(id)) {
        throw new EntityNotFoundException("Ruta no encontrada con id: " + id);
        }
        rutaRepository.deleteById(id);
    }

    public Ruta getRutaPorSolicitudId(Integer solicitudId) {
        Objects.requireNonNull(solicitudId, "la id de la solicitud no puede ser nula");
        return rutaRepository.findByIdSolicitud(solicitudId)
                .orElseThrow(() -> new EntityNotFoundException("Ruta no encontrada para la solicitud con id: " + solicitudId));
    }

    public List<Ruta> crearRutasParaSolicitud(Integer solicitudId, Ubicacion ubicacionInicial, Ubicacion ubicacionFinal, Date fechaHoraInicio) {
        Ruta ruta = new Ruta();
        ruta.setIdSolicitud(solicitudId);
        ruta.setCantidadTramos(-1);
        ruta.setCantidadDepositos(-1);
        ruta.setUbicacionInicial(ubicacionInicial);
        ruta.setUbicacionFinal(ubicacionFinal);

        calcularRutas(ruta, ubicacionInicial, ubicacionFinal, fechaHoraInicio);

        return List.of(ruta);
    }

    public Ruta calcularRutas(Ruta ruta, Ubicacion ubicacionInicial, Ubicacion ubicacionFinal, Date fechaHoraInicio) {
        List<Tramo> tramos = tramosService.calcularTramos(
            ruta,
            ubicacionInicial,
            ubicacionFinal,
            fechaHoraInicio
        );


        return ruta; // temporal
    }

}
