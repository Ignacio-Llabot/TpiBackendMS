package org.tpibackend.mstransportes.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.tpibackend.mstransportes.dto.TramoDTO;
import org.tpibackend.mstransportes.dto.SolicitudRemotaDTO;
import org.tpibackend.mstransportes.entity.Camion;
import org.tpibackend.mstransportes.entity.Deposito;
import org.tpibackend.mstransportes.entity.Estado;
import org.tpibackend.mstransportes.entity.Ruta;
import org.tpibackend.mstransportes.entity.TipoTramo;
import org.tpibackend.mstransportes.entity.Tramo;
import org.tpibackend.mstransportes.entity.Ubicacion;
import org.tpibackend.mstransportes.repository.TramoRepository;
import org.tpibackend.mstransportes.repository.UbicacionRepository;
import org.tpibackend.mstransportes.service.osrmstategies.Strategy;
import org.tpibackend.mstransportes.service.osrmstategies.UrgenteStrategy;

import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import jakarta.persistence.EntityNotFoundException;

@Service
public class TramoService {
    
    private final TramoRepository tramoRepository;

    private final OsrmService osrmService;

    private final EstadoService estadoService;

    private final TipoTramoService tipoTramoService;

    private final DepositoService depositoService;

    private final UbicacionRepository ubicacionRepository;

    private final CamionService camionService;

    private final RestTemplate restTemplate;

    private final String contenedoresServiceBaseUrl;

    public TramoService(
        TramoRepository tramoRepository,
        OsrmService osrmService,
        EstadoService estadoService,
        TipoTramoService tipoTramoService,
        DepositoService depositoService,
        UbicacionRepository ubicacionRepository,
        CamionService camionService,
        @Value("${ms.contenedores.url}") String contenedoresServiceBaseUrl
    ) {
        this.tramoRepository = tramoRepository;
        this.osrmService = osrmService;
        this.estadoService = estadoService;
        this.tipoTramoService = tipoTramoService;
        this.depositoService = depositoService;
        this.ubicacionRepository = ubicacionRepository;
        this.camionService = camionService;
        this.restTemplate = new RestTemplate();
        this.contenedoresServiceBaseUrl = contenedoresServiceBaseUrl;
    }

    public Tramo getTramoPorId(Integer tramoId) {
        Objects.requireNonNull(tramoId, "la id del tramo no puede ser nula");
        return tramoRepository.findById(tramoId)
                .orElseThrow(() -> new EntityNotFoundException("Tramo no encontrado con id: " + tramoId));
    }

    public Tramo persistirTramo(Tramo tramo) {
        Objects.requireNonNull(tramo, "el tramo no puede ser nulo");
        return tramoRepository.save(tramo);
    }

    public void eliminarTramoById(Integer tramoId) {
        Objects.requireNonNull(tramoId, "la id del tramo no puede ser nula");
        if (!tramoRepository.existsById(tramoId)) {
            throw new EntityNotFoundException("Tramo no encontrado con id: " + tramoId);
        }
        tramoRepository.deleteById(tramoId);
    }

    public List<Tramo> guardarTramos(List<Tramo> tramos) {
        Objects.requireNonNull(tramos, "la lista de tramos no puede ser nula");
        return tramoRepository.saveAll(tramos);
    }

    public List<Tramo> getTramosPorRuta(Integer rutaId) {
        Objects.requireNonNull(rutaId, "la id de la ruta no puede ser nula");
        return tramoRepository.findByRuta_IdRuta(rutaId);
    }

    public List<Tramo> calcularTramos(
        Ruta ruta,
        Ubicacion ubicacionInicio,
        Ubicacion ubicacionFin,
        LocalDateTime fechaHoraInicio
    ) {
        Ubicacion origenPersistido = asegurarUbicacion(ubicacionInicio);
        Ubicacion destinoPersistido = asegurarUbicacion(ubicacionFin);

        ruta.setUbicacionInicial(origenPersistido);
        ruta.setUbicacionFinal(destinoPersistido);

        List<Deposito> depositosDisponibles = depositoService.getDepositos();

        List<TramoDTO> tramoDTOs = osrmService.calcularTramosDTO(
            origenPersistido,
            destinoPersistido,
            depositosDisponibles
        );
        if (tramoDTOs.isEmpty()) {
            return List.of();
        }

        Strategy estrategiaActual = osrmService.getStrategy();
        boolean esRutaDirecta = estrategiaActual instanceof UrgenteStrategy;

        Estado estadoPendiente = estadoService.getEstadoPorNombre("pendiente");

        Map<Integer, Deposito> depositosPorUbicacion = construirIndiceDepositos(depositosDisponibles);

        List<Tramo> tramos = new ArrayList<>();
        LocalDateTime inicioTramo = fechaHoraInicio;

        for (int i = 0; i < tramoDTOs.size(); i++) {
            TramoDTO dto = tramoDTOs.get(i);

            Tramo tramo = new Tramo();
            tramo.setRuta(ruta);
            Ubicacion ubicacionOrigenPersistida = asegurarUbicacion(dto.getUbicacionOrigen());
            Ubicacion ubicacionDestinoPersistida = asegurarUbicacion(dto.getUbicacionDestino());

            tramo.setUbicacionOrigen(ubicacionOrigenPersistida);
            tramo.setUbicacionDestino(ubicacionDestinoPersistida);
            tramo.setDistancia(dto.getDistancia());
            tramo.setEstado(estadoPendiente);

            tramo.setTipoTramo(resolverTipoTramo(i, tramoDTOs.size(), esRutaDirecta));

            tramo.setFechaHoraInicioEstimada(inicioTramo);

            long duracionSegundos = dto.getDuracionEstimada() != null
                ? Math.round(dto.getDuracionEstimada())
                : 0L;
            LocalDateTime finTramo = inicioTramo.plusSeconds(duracionSegundos);
            tramo.setFechaHoraFinEstimada(finTramo);

            Deposito depositoDestino = obtenerDepositoPorUbicacion(ubicacionDestinoPersistida, depositosPorUbicacion);
            if (depositoDestino != null) {
                tramo.setCostoAproximado(depositoDestino.getCostoEstadia());
            } else {
                tramo.setCostoAproximado(0.0);
            }

            tramos.add(tramo);

            boolean haySiguienteTramo = i < tramoDTOs.size() - 1;
            inicioTramo = finTramo;
            if (haySiguienteTramo && !esRutaDirecta && depositoDestino != null) {
                inicioTramo = inicioTramo.plusDays(1);
            }
        }

        return tramos;
    }

    public void setStrategyOsrmService(Strategy strategy) {
        osrmService.setStrategy(strategy);
    }

    public Tramo asignarCamionATramo(Integer solicitudId, Integer tramoId, String patenteCamion) {
        Objects.requireNonNull(solicitudId, "la id de la solicitud no puede ser nula");
        Objects.requireNonNull(tramoId, "la id del tramo no puede ser nula");
        Objects.requireNonNull(patenteCamion, "la patente del camión no puede ser nula");

        Tramo tramo = obtenerTramoDeSolicitud(solicitudId, tramoId);

        if (tramo.getCamion() != null) {
            throw new IllegalStateException("El tramo ya tiene un camión asignado");
        }

        Camion camion = camionService.getCamionPorPatente(patenteCamion);

        if (Boolean.FALSE.equals(camion.getDisponibilidad())) {
            throw new IllegalStateException("El camión no está disponible para asignarse");
        }

        validarCapacidadesCamion(solicitudId, camion);

        tramo.setCamion(camion);
        camion.setDisponibilidad(false);
        camionService.persistirCamion(camion);
        return persistirTramo(tramo);
    }

    public Tramo marcarTramoEnCamino(Integer solicitudId, Integer tramoId) {
        Objects.requireNonNull(solicitudId, "la id de la solicitud no puede ser nula");
        Objects.requireNonNull(tramoId, "la id del tramo no puede ser nula");

        Tramo tramo = obtenerTramoDeSolicitud(solicitudId, tramoId);

        Estado estadoActual = tramo.getEstado();
        if (estadoActual == null || !"pendiente".equalsIgnoreCase(estadoActual.getNombre())) {
            throw new IllegalStateException("El tramo debe estar en estado pendiente para marcarlo en camino");
        }

        if (tramo.getCamion() == null) {
            throw new IllegalStateException("No se puede marcar en camino un tramo sin camión asignado");
        }

        Estado estadoEnCamino = estadoService.getEstadoPorNombre("en camino");
        tramo.setEstado(estadoEnCamino);
        return persistirTramo(tramo);
    }

    public Tramo marcarTramoFinalizado(Integer solicitudId, Integer tramoId) {
        Objects.requireNonNull(solicitudId, "la id de la solicitud no puede ser nula");
        Objects.requireNonNull(tramoId, "la id del tramo no puede ser nula");

        Tramo tramo = obtenerTramoDeSolicitud(solicitudId, tramoId);

        Estado estadoActual = tramo.getEstado();
        if (estadoActual == null || !"en camino".equalsIgnoreCase(estadoActual.getNombre())) {
            throw new IllegalStateException("El tramo debe estar en estado en camino para marcarlo finalizado");
        }

        Estado estadoFinalizado = estadoService.getEstadoPorNombre("finalizado");
        tramo.setEstado(estadoFinalizado);
        return persistirTramo(tramo);
    }

    private void validarCapacidadesCamion(Integer solicitudId, Camion camion) {
        SolicitudRemotaDTO solicitud = obtenerSolicitudRemota(solicitudId);
        if (solicitud == null || solicitud.getContenedor() == null) {
            throw new EntityNotFoundException("No se encontró un contenedor asociado a la solicitud: " + solicitudId);
        }

        SolicitudRemotaDTO.ContenedorRemotoDTO contenedor = solicitud.getContenedor();
        Double pesoContenedor = contenedor.getPeso();
        Double volumenContenedor = contenedor.getVolumen();

        Double capacidadPesoCamion = camion.getCapacidadPeso();
        Double capacidadVolumenCamion = camion.getCapacidadVolumen();

        if (pesoContenedor != null && capacidadPesoCamion != null && pesoContenedor > capacidadPesoCamion) {
            throw new IllegalStateException("El peso del contenedor excede la capacidad del camión");
        }

        if (volumenContenedor != null && capacidadVolumenCamion != null && volumenContenedor > capacidadVolumenCamion) {
            throw new IllegalStateException("El volumen del contenedor excede la capacidad del camión");
        }
    }

    private SolicitudRemotaDTO obtenerSolicitudRemota(Integer solicitudId) {
        String baseUrl = Objects.requireNonNull(contenedoresServiceBaseUrl, "ms.contenedores.url no configurada");

        String url = UriComponentsBuilder
            .fromUriString(baseUrl)
            .path("/api/v1/solicitudes/{id}")
            .buildAndExpand(solicitudId)
            .toUriString();

        try {
            return restTemplate.getForObject(url, SolicitudRemotaDTO.class);
        } catch (HttpClientErrorException ex) {
            if (ex.getStatusCode().value() == HttpStatus.NOT_FOUND.value()) {
                throw new EntityNotFoundException("Solicitud no encontrada con id: " + solicitudId);
            }
            throw new IllegalStateException("Error al consultar la solicitud: " + solicitudId, ex);
        } catch (RestClientException ex) {
            throw new IllegalStateException("No se pudo obtener la solicitud: " + solicitudId, ex);
        }
    }

    private Tramo obtenerTramoDeSolicitud(Integer solicitudId, Integer tramoId) {
        Tramo tramo = getTramoPorId(tramoId);
        Ruta ruta = tramo.getRuta();
        if (ruta == null || !Objects.equals(ruta.getIdSolicitud(), solicitudId)) {
            throw new EntityNotFoundException("El tramo no pertenece a la solicitud indicada");
        }
        return tramo;
    }

    private Map<Integer, Deposito> construirIndiceDepositos(List<Deposito> depositos) {
        Map<Integer, Deposito> indice = new HashMap<>();
        for (Deposito deposito : depositos) {
            if (deposito == null || deposito.getUbicacion() == null) {
                continue;
            }
            Integer idUbicacion = deposito.getUbicacion().getIdUbicacion();
            if (idUbicacion != null) {
                indice.putIfAbsent(idUbicacion, deposito);
            }
        }
        return indice;
    }

    private Deposito obtenerDepositoPorUbicacion(Ubicacion ubicacion, Map<Integer, Deposito> depositosPorUbicacion) {
        if (ubicacion == null || ubicacion.getIdUbicacion() == null) {
            return null;
        }
        return depositosPorUbicacion.get(ubicacion.getIdUbicacion());
    }

    private TipoTramo resolverTipoTramo(int indice, int total, boolean esRutaDirecta) {
        if (esRutaDirecta) {
            return tipoTramoService.getTipoTramoPorNombre("origen-origen");
        }
        if (indice == 0) {
            return tipoTramoService.getTipoTramoPorNombre("origen-deposito");
        }
        if (indice == total - 1) {
            return tipoTramoService.getTipoTramoPorNombre("deposito-destino");
        }
        return tipoTramoService.getTipoTramoPorNombre("deposito-deposito");
    }

    @SuppressWarnings("null")
    private Ubicacion asegurarUbicacion(Ubicacion ubicacion) {
        Objects.requireNonNull(ubicacion, "la ubicacion no puede ser nula");

        if (ubicacion.getIdUbicacion() != null) {
            return ubicacionRepository.findById(ubicacion.getIdUbicacion())
                .orElseThrow(() -> new EntityNotFoundException("Ubicacion no encontrada con id: " + ubicacion.getIdUbicacion()));
        }

        if (ubicacion.getLatitud() == null || ubicacion.getLongitud() == null) {
            throw new IllegalArgumentException("La ubicacion debe tener latitud y longitud definidas");
        }

        Optional<Ubicacion> existente = ubicacionRepository.findByLatitudAndLongitud(
            ubicacion.getLatitud(),
            ubicacion.getLongitud()
        );

        return existente.orElseGet(() -> ubicacionRepository.save(ubicacion));
    }
}
