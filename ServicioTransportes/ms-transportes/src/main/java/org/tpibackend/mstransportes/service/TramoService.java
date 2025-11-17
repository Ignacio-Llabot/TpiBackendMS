package org.tpibackend.mstransportes.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import org.tpibackend.mstransportes.dto.SolicitudRemotaDTO;
import org.tpibackend.mstransportes.dto.TramoDTO;
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

    private final String tarifasServiceBaseUrl;

    private static final Logger log = LoggerFactory.getLogger(TramoService.class);

    public TramoService(
        TramoRepository tramoRepository,
        OsrmService osrmService,
        EstadoService estadoService,
        TipoTramoService tipoTramoService,
        DepositoService depositoService,
        UbicacionRepository ubicacionRepository,
        CamionService camionService,
        RestTemplate restTemplate,
        @Value("${ms.contenedores.url}") String contenedoresServiceBaseUrl,
        @Value("${ms.tarifas.url}") String tarifasServiceBaseUrl
    ) {
        this.tramoRepository = tramoRepository;
        this.osrmService = osrmService;
        this.estadoService = estadoService;
        this.tipoTramoService = tipoTramoService;
        this.depositoService = depositoService;
        this.ubicacionRepository = ubicacionRepository;
        this.camionService = camionService;
        this.restTemplate = restTemplate;
        this.contenedoresServiceBaseUrl = contenedoresServiceBaseUrl;
        this.tarifasServiceBaseUrl = tarifasServiceBaseUrl;
    }

    public Tramo getTramoPorId(Integer tramoId) {
        Objects.requireNonNull(tramoId, "la id del tramo no puede ser nula");
        return tramoRepository.findById(tramoId)
                .orElseThrow(() -> {
                    log.warn("Tramo {} no encontrado", tramoId);
                    return new EntityNotFoundException("Tramo no encontrado con id: " + tramoId);
                });
    }

    public Tramo persistirTramo(Tramo tramo) {
        Objects.requireNonNull(tramo, "el tramo no puede ser nulo");
        Tramo guardado = tramoRepository.save(tramo);
        log.info("Tramo {} persistido", guardado.getId());
        return guardado;
    }

    public void eliminarTramoById(Integer tramoId) {
        Objects.requireNonNull(tramoId, "la id del tramo no puede ser nula");
        if (!tramoRepository.existsById(tramoId)) {
            throw new EntityNotFoundException("Tramo no encontrado con id: " + tramoId);
        }
        log.info("Eliminando tramo {}", tramoId);
        tramoRepository.deleteById(tramoId);
    }

    public List<Tramo> guardarTramos(List<Tramo> tramos) {
        Objects.requireNonNull(tramos, "la lista de tramos no puede ser nula");
        List<Tramo> guardados = tramoRepository.saveAll(tramos);
        log.info("Persistidos {} tramos", guardados.size());
        return guardados;
    }

    public List<Tramo> getTramosPorRuta(Integer rutaId) {
        Objects.requireNonNull(rutaId, "la id de la ruta no puede ser nula");
        List<Tramo> tramos = tramoRepository.findByRuta_IdRuta(rutaId);
        log.info("Recuperados {} tramos para la ruta {}", tramos.size(), rutaId);
        return tramos;
    }

    public void actualizarCostoAproximado(Integer tramoId, Double incrementoCosto) {
        Objects.requireNonNull(tramoId, "la id del tramo no puede ser nula");
        Objects.requireNonNull(incrementoCosto, "el incremento de costo no puede ser nulo");
        log.info("Incrementando costo aproximado del tramo {} en {}", tramoId, incrementoCosto);

        Tramo tramo = getTramoPorId(tramoId);
        double costoActual = tramo.getCostoAproximado() != null ? tramo.getCostoAproximado() : 0.0d;
        tramo.setCostoAproximado(costoActual + incrementoCosto);
        persistirTramo(tramo);
    }

    public void actualizarCostoReal(Integer tramoId, Double costoReal) {
        Objects.requireNonNull(tramoId, "la id del tramo no puede ser nula");
        Objects.requireNonNull(costoReal, "el costo real no puede ser nulo");
        log.info("Actualizando costo real del tramo {} a {}", tramoId, costoReal);

        Tramo tramo = getTramoPorId(tramoId);
        tramo.setCostoReal(costoReal);
        persistirTramo(tramo);
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

        log.info(
            "Calculando tramos para solicitud {} entre {} y {}",
            ruta.getIdSolicitud(),
            describirUbicacion(origenPersistido),
            describirUbicacion(destinoPersistido)
        );

        List<Deposito> depositosDisponibles = depositoService.getDepositos();
        log.debug("Se recuperaron {} depósitos para evaluar", depositosDisponibles.size());

        List<TramoDTO> tramoDTOs = osrmService.calcularTramosDTO(
            origenPersistido,
            destinoPersistido,
            depositosDisponibles
        );
        if (tramoDTOs.isEmpty()) {
            log.warn("No se obtuvieron tramos desde OSRM para la solicitud {}", ruta.getIdSolicitud());
            return List.of();
        }

        Strategy estrategiaActual = osrmService.getStrategy();
        boolean esRutaDirecta = estrategiaActual instanceof UrgenteStrategy;
        log.info(
            "OSRM devolvió {} tramos utilizando la estrategia {}",
            tramoDTOs.size(),
            estrategiaActual != null ? estrategiaActual.getClass().getSimpleName() : "desconocida"
        );

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

        log.info("Se calcularon {} tramos para la solicitud {}", tramos.size(), ruta.getIdSolicitud());
        return tramos;
    }

    private String describirUbicacion(Ubicacion ubicacion) {
        if (ubicacion == null) {
            return "ubicación desconocida";
        }
        Integer id = ubicacion.getIdUbicacion();
        Double lat = ubicacion.getLatitud();
        Double lon = ubicacion.getLongitud();
        String coordenadas = (lat != null && lon != null)
            ? String.format("(%.6f, %.6f)", lat, lon)
            : "(sin coordenadas)";
        if (id != null) {
            return String.format("id=%d %s", id, coordenadas);
        }
        if (lat != null && lon != null) {
            return coordenadas;
        }
        return "ubicación sin coordenadas";
    }

    public void setStrategyOsrmService(Strategy strategy) {
        log.debug("Estrategia OSRM establecida en {}", strategy.getClass().getSimpleName());
        osrmService.setStrategy(strategy);
    }

    public Tramo asignarCamionATramo(Integer solicitudId, Integer tramoId, String patenteCamion) {
        Objects.requireNonNull(solicitudId, "la id de la solicitud no puede ser nula");
        Objects.requireNonNull(tramoId, "la id del tramo no puede ser nula");
        Objects.requireNonNull(patenteCamion, "la patente del camión no puede ser nula");

        log.info("Asignando camión {} al tramo {} de la solicitud {}", patenteCamion, tramoId, solicitudId);

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
        Tramo actualizado = persistirTramo(tramo);
        log.info("Camión {} asignado al tramo {} de la solicitud {}", patenteCamion, tramoId, solicitudId);
        return actualizado;
    }

    public Tramo marcarTramoEnCamino(Integer solicitudId, Integer tramoId) {
        Objects.requireNonNull(solicitudId, "la id de la solicitud no puede ser nula");
        Objects.requireNonNull(tramoId, "la id del tramo no puede ser nula");

        log.info("Marcando tramo {} de la solicitud {} como en camino", tramoId, solicitudId);

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
        Tramo actualizado = persistirTramo(tramo);
        log.info("Tramo {} de la solicitud {} marcado como en camino", tramoId, solicitudId);
        return actualizado;
    }

    public Tramo marcarTramoFinalizado(Integer solicitudId, Integer tramoId) {
        Objects.requireNonNull(solicitudId, "la id de la solicitud no puede ser nula");
        Objects.requireNonNull(tramoId, "la id del tramo no puede ser nula");

        log.info("Marcando tramo {} de la solicitud {} como finalizado", tramoId, solicitudId);

        Tramo tramo = obtenerTramoDeSolicitud(solicitudId, tramoId);

        Estado estadoActual = tramo.getEstado();
        if (estadoActual == null || !"en camino".equalsIgnoreCase(estadoActual.getNombre())) {
            throw new IllegalStateException("El tramo debe estar en estado en camino para marcarlo finalizado");
        }

        Estado estadoFinalizado = estadoService.getEstadoPorNombre("finalizado");
        tramo.setEstado(estadoFinalizado);
        Tramo tramoActualizado = persistirTramo(tramo);

        Integer rutaId = tramoActualizado.getRuta() != null ? tramoActualizado.getRuta().getIdRuta() : null;
        if (rutaId != null) {
            log.info("Solicitando recálculo de tarifa real para la ruta {}", rutaId);
            solicitarRecalculoTarifaReal(rutaId);
        }

        log.info("Tramo {} de la solicitud {} marcado como finalizado", tramoId, solicitudId);
        return tramoActualizado;
    }

    public void actualizarSolicitudDesdeRuta(Integer solicitudId, Double tiempoEstimadoHoras) {
        Objects.requireNonNull(solicitudId, "la id de la solicitud no puede ser nula");
        log.info("Actualizando solicitud {} desde ms-transportes", solicitudId);

        try {
            Map<String, Object> solicitud = obtenerSolicitudParaActualizacion(solicitudId);
            if (solicitud == null) {
                log.warn("No se encontró la solicitud {} para confirmar desde ms-transportes", solicitudId);
                return;
            }

            if (tiempoEstimadoHoras != null) {
                solicitud.put("tiempoEstimado", tiempoEstimadoHoras);
                log.info("Tiempo estimado calculado para la solicitud {}: {} horas", solicitudId, tiempoEstimadoHoras);
            } else {
                log.info("Tiempo estimado no disponible para la solicitud {}", solicitudId);
            }

            Map<String, Object> estado = obtenerEstadoActual(solicitud);
            estado.put("idEstado", 5);
            estado.put("nombre", "confirmada");
            solicitud.put("estado", estado);

            String url = UriComponentsBuilder
                .fromUriString(Objects.requireNonNull(contenedoresServiceBaseUrl, "ms.contenedores.url no configurada"))
                .path("/api/v1/solicitudes/{id}")
                .buildAndExpand(solicitudId)
                .toUriString();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(solicitud, headers);

            HttpMethod method = Objects.requireNonNull(HttpMethod.PUT);
            restTemplate.exchange(url, method, entity, Void.class);
            log.info("Solicitud {} confirmada en ms-contenedores", solicitudId);
        } catch (IllegalStateException | RestClientException ex) {
            log.error("No se pudo confirmar la solicitud {}", solicitudId, ex);
        }
    }

    private void validarCapacidadesCamion(Integer solicitudId, Camion camion) {
        log.debug("Validando capacidades del camión {} para la solicitud {}", camion.getPatente(), solicitudId);
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

        log.debug("Capacidades del camión {} validadas para la solicitud {}", camion.getPatente(), solicitudId);
    }

    private SolicitudRemotaDTO obtenerSolicitudRemota(Integer solicitudId) {
        String baseUrl = Objects.requireNonNull(contenedoresServiceBaseUrl, "ms.contenedores.url no configurada");

        String url = UriComponentsBuilder
            .fromUriString(baseUrl)
            .path("/api/v1/solicitudes/{id}")
            .buildAndExpand(solicitudId)
            .toUriString();

        try {
            SolicitudRemotaDTO respuesta = restTemplate.getForObject(url, SolicitudRemotaDTO.class);
            if (respuesta != null) {
                log.info("Solicitud remota {} recuperada desde ms-contenedores", solicitudId);
            } else {
                log.warn("Solicitud remota {} no encontrada en ms-contenedores", solicitudId);
            }
            return respuesta;
        } catch (HttpClientErrorException ex) {
            if (ex.getStatusCode().value() == HttpStatus.NOT_FOUND.value()) {
                log.warn("Solicitud remota {} no encontrada (404)", solicitudId);
                throw new EntityNotFoundException("Solicitud no encontrada con id: " + solicitudId);
            }
            log.error("Error http al consultar la solicitud {}", solicitudId, ex);
            throw new IllegalStateException("Error al consultar la solicitud: " + solicitudId, ex);
        } catch (RestClientException ex) {
            log.error("Fallo al consultar la solicitud {}", solicitudId, ex);
            throw new IllegalStateException("No se pudo obtener la solicitud: " + solicitudId, ex);
        }
    }

    private Map<String, Object> obtenerSolicitudParaActualizacion(Integer solicitudId) {
        String baseUrl = Objects.requireNonNull(contenedoresServiceBaseUrl, "ms.contenedores.url no configurada");

        String url = UriComponentsBuilder
            .fromUriString(baseUrl)
            .path("/api/v1/solicitudes/{id}")
            .buildAndExpand(solicitudId)
            .toUriString();

        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> solicitud = restTemplate.getForObject(url, Map.class);
            return solicitud;
        } catch (HttpClientErrorException ex) {
            if (ex.getStatusCode().value() == HttpStatus.NOT_FOUND.value()) {
                log.warn("Solicitud {} no encontrada al preparar la actualización de tiempo estimado", solicitudId);
                return null;
            }
            log.error("Error http al buscar la solicitud {} para actualizar tiempo estimado", solicitudId, ex);
            throw new IllegalStateException("No se pudo obtener la solicitud para actualizar el tiempo estimado", ex);
        } catch (RestClientException ex) {
            log.error("Fallo al obtener la solicitud {} para actualizar tiempo estimado", solicitudId, ex);
            throw new IllegalStateException("No se pudo obtener la solicitud para actualizar el tiempo estimado", ex);
        }
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> obtenerEstadoActual(Map<String, Object> solicitud) {
        Object estadoObj = solicitud.get("estado");
        if (estadoObj instanceof Map<?, ?> estadoMap) {
            return (Map<String, Object>) estadoMap;
        }
        return new HashMap<>();
    }

    private void solicitarRecalculoTarifaReal(Integer rutaId) {
        String baseUrl = Objects.requireNonNull(tarifasServiceBaseUrl, "ms.tarifas.url no configurada");

        String url = UriComponentsBuilder
            .fromUriString(baseUrl)
            .path("/api/v1/tarifas/tarifasReales/{rutaId}")
            .buildAndExpand(rutaId)
            .toUriString();

        try {
            restTemplate.postForEntity(url, null, Void.class);
            log.info("Recalculo de tarifa real solicitado para la ruta {}", rutaId);
        } catch (RestClientException ex) {
            log.error("No se pudo recalcular la tarifa real para la ruta {}", rutaId, ex);
            throw new IllegalStateException("No se pudo recalcular la tarifa real para la ruta: " + rutaId, ex);
        }
    }

    private Tramo obtenerTramoDeSolicitud(Integer solicitudId, Integer tramoId) {
        Tramo tramo = getTramoPorId(tramoId);
        Ruta ruta = tramo.getRuta();
        if (ruta == null || !Objects.equals(ruta.getIdSolicitud(), solicitudId)) {
            log.warn("El tramo {} no pertenece a la solicitud {}", tramoId, solicitudId);
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
        if (existente.isPresent()) {
            log.debug("Ubicación existente reutilizada ({}, {})", ubicacion.getLatitud(), ubicacion.getLongitud());
            return existente.get();
        }

        Ubicacion guardada = ubicacionRepository.save(ubicacion);
        log.info("Ubicación {} creada", guardada.getIdUbicacion());
        return guardada;
    }
}
