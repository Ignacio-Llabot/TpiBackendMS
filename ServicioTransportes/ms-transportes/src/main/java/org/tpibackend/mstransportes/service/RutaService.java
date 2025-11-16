package org.tpibackend.mstransportes.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.tpibackend.mstransportes.dto.RutaDetalleDTO;
import org.tpibackend.mstransportes.dto.RutaDetalleDTO.CamionDetalleDTO;
import org.tpibackend.mstransportes.dto.RutaDetalleDTO.TipoCamionDetalleDTO;
import org.tpibackend.mstransportes.dto.RutaDetalleDTO.TramoDetalleDTO;
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
        private static final Logger log = LoggerFactory.getLogger(RutaService.class);

    public RutaService(RutaRepository rutaRepository, TramoService tramosService) {
        this.rutaRepository = rutaRepository;
        this.tramosService = tramosService;
    }

    public Ruta getRutaPorId(Integer id) {
        Objects.requireNonNull(id, "la id no puede ser nula");
        return rutaRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Ruta {} no encontrada", id);
                    return new EntityNotFoundException("Ruta no encontrada con id: " + id);
                });
    }

    public RutaDetalleDTO obtenerRutaDetallada(Integer idRuta) {
        log.info("Recuperando detalle para la ruta {}", idRuta);
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
        log.info("Detalle armado para la ruta {} con {} tramos", idRuta, detalles.size());
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
        Ruta guardada = rutaRepository.save(ruta);
        log.info("Ruta {} persistida", guardada.getIdRuta());
        return guardada;
    }

    public void eliminarRutaPorId(Integer id) {
        Objects.requireNonNull(id, "la id no puede ser nula");
        if (!rutaRepository.existsById(id)) {
        throw new EntityNotFoundException("Ruta no encontrada con id: " + id);
        }
        log.info("Eliminando ruta {}", id);
        rutaRepository.deleteById(id);
    }

    public Ruta getRutaPorSolicitudId(Integer solicitudId) {
        Objects.requireNonNull(solicitudId, "la id de la solicitud no puede ser nula");
        return rutaRepository.findByIdSolicitud(solicitudId)
                .orElseThrow(() -> {
                    log.warn("Ruta no encontrada para la solicitud {}", solicitudId);
                    return new EntityNotFoundException("Ruta no encontrada para la solicitud con id: " + solicitudId);
                });
    }

    public Ruta crearRutasParaSolicitud(Integer solicitudId, Ubicacion ubicacionInicial, Ubicacion ubicacionFinal, LocalDateTime fechaHoraInicio) {
        log.info("Creando ruta para solicitud {}", solicitudId);
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
        log.info("Calculando tramos para la ruta de la solicitud {}", ruta.getIdSolicitud());

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
        Ruta actualizada = persistirRuta(ruta);
        log.info("Ruta {} actualizada con {} tramos", actualizada.getIdRuta(), tramos.size());
        return actualizada; 
    }

    public void setStrategyOsrmService(Strategy strategy) {
        log.debug("Actualizando estrategia OSRM a {}", strategy.getClass().getSimpleName());
        tramosService.setStrategyOsrmService(strategy);
    }

}
