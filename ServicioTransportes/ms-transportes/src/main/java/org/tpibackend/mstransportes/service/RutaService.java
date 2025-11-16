package org.tpibackend.mstransportes.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.tpibackend.mstransportes.dto.RutaDetalleDTO;
import org.tpibackend.mstransportes.dto.RutaDetalleDTO.CamionDetalleDTO;
import org.tpibackend.mstransportes.dto.RutaDetalleDTO.TipoCamionDetalleDTO;
import org.tpibackend.mstransportes.dto.RutaDetalleDTO.TramoDetalleDTO;
import org.springframework.stereotype.Service;
import org.tpibackend.mstransportes.entity.Ruta;
import org.tpibackend.mstransportes.entity.Tramo;
import org.tpibackend.mstransportes.entity.Ubicacion;
import org.tpibackend.mstransportes.repository.RutaRepository;
import org.tpibackend.mstransportes.service.TramoService;
import org.tpibackend.mstransportes.service.osrmstategies.Strategy;

import jakarta.persistence.EntityNotFoundException;


@Service
public class RutaService {
    
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

    public RutaDetalleDTO obtenerRutaDetallada(Integer idRuta) {
        Ruta ruta = getRutaPorId(idRuta);
        List<Tramo> tramos = tramosService.getTramosPorRuta(idRuta);

        RutaDetalleDTO.RutaDetalleDTOBuilder builder = RutaDetalleDTO.builder()
            .id(ruta.getIdRuta())
            .idSolicitud(ruta.getIdSolicitud())
            .cantidadTramos(ruta.getCantidadTramos())
            .cantidadDepositos(ruta.getCantidadDepositos())
            .ubicacionInicial(ruta.getUbicacionInicial())
            .ubicacionFinal(ruta.getUbicacionFinal());

        List<TramoDetalleDTO> detalles = tramos.stream()
            .map(this::mapearTramo)
            .collect(Collectors.toList());

        builder.tramos(detalles);
        return builder.build();
    }

    private TramoDetalleDTO mapearTramo(Tramo tramo) {
        TramoDetalleDTO.TramoDetalleDTOBuilder builder = TramoDetalleDTO.builder()
            .id(tramo.getId())
            .ubicacionOrigen(tramo.getUbicacionOrigen())
            .ubicacionDestino(tramo.getUbicacionDestino())
            .distancia(tramo.getDistancia())
            .tipoTramo(tramo.getTipoTramo())
            .estado(tramo.getEstado())
            .costoAproximado(tramo.getCostoAproximado())
            .costoReal(tramo.getCostoReal())
            .fechaHoraInicioEstimada(tramo.getFechaHoraInicioEstimada())
            .fechaHoraFinEstimada(tramo.getFechaHoraFinEstimada())
            .fechaHoraInicio(tramo.getFechaHoraInicio())
            .fechaHoraFin(tramo.getFechaHoraFin());

        if (tramo.getCamion() != null && tramo.getCamion().getTipoCamion() != null) {
            TipoCamionDetalleDTO tipoCamionDetalle = TipoCamionDetalleDTO.builder()
                .id(tramo.getCamion().getTipoCamion().getIdTipoCamion())
                .nombre(tramo.getCamion().getTipoCamion().getNombre())
                .build();

            CamionDetalleDTO camionDetalle = CamionDetalleDTO.builder()
                .patente(tramo.getCamion().getPatente())
                .tipoCamion(tipoCamionDetalle)
                .build();

            builder.camion(camionDetalle);
        }

        return builder.build();
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

    public Ruta crearRutasParaSolicitud(Integer solicitudId, Ubicacion ubicacionInicial, Ubicacion ubicacionFinal, LocalDateTime fechaHoraInicio) {
        Ruta ruta = new Ruta();
        ruta.setIdSolicitud(solicitudId);
        ruta.setCantidadTramos(-1);
        ruta.setCantidadDepositos(-1);
        ruta.setUbicacionInicial(ubicacionInicial);
        ruta.setUbicacionFinal(ubicacionFinal);


        calcularRuta(ruta, ubicacionInicial, ubicacionFinal, fechaHoraInicio);

        return ruta;
    }

    public Ruta calcularRuta(Ruta ruta, Ubicacion ubicacionInicial, Ubicacion ubicacionFinal, LocalDateTime fechaHoraInicio) {

        List<Tramo> tramos = tramosService.calcularTramos(
            ruta,
            ubicacionInicial,
            ubicacionFinal,
            fechaHoraInicio
        );

        if (ruta.getIdRuta() == null) {
            ruta = persistirRuta(ruta);
        }


        tramosService.guardarTramos(tramos);
        ruta.setCantidadTramos(tramos.size());
        ruta.setCantidadDepositos(tramos.size() - 1);
        return persistirRuta(ruta); 
    }

    public void setStrategyOsrmService(Strategy strategy) {
        tramosService.setStrategyOsrmService(strategy);
    }

}
