package com.tpibackend.ms_tarifas.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.tpibackend.ms_tarifas.dto.PromedioAtributosDTO;
import com.tpibackend.ms_tarifas.dto.TarifaAproximadaResponseDTO;
import com.tpibackend.ms_tarifas.dto.TarifaRealResponseDTO;
import com.tpibackend.ms_tarifas.dto.TarifaTramoCostoDTO;
import com.tpibackend.ms_tarifas.entity.Tarifa;
import com.tpibackend.ms_tarifas.external.CamionClient;
import com.tpibackend.ms_tarifas.external.RutaClient;
import com.tpibackend.ms_tarifas.external.SolicitudClient;
import com.tpibackend.ms_tarifas.external.TramoClient;
import com.tpibackend.ms_tarifas.external.dto.CamionRemotoDTO;
import com.tpibackend.ms_tarifas.external.dto.CamionResumenRemotoDTO;
import com.tpibackend.ms_tarifas.external.dto.RutaRemotaDTO;
import com.tpibackend.ms_tarifas.external.dto.SolicitudRemotaDTO;
import com.tpibackend.ms_tarifas.external.dto.TramoRemotoDTO;
import com.tpibackend.ms_tarifas.repository.TarifaRepository;
import com.tpibackend.ms_tarifas.repository.projection.TarifaPromedioProjection;

import jakarta.persistence.EntityNotFoundException;


@Service
public class TarifaService {
    
    private final TarifaRepository tarifaRepository;
    private final CamionClient camionClient;
    private final RutaClient rutaClient;
    private final SolicitudClient solicitudClient;
    private final TramoClient tramoClient;
    private final CostoBasePorVolumenManager costoBasePorVolumenManager;
    private static final Logger log = LoggerFactory.getLogger(TarifaService.class);

    public TarifaService(
        TarifaRepository tarifaRepository,
        CamionClient camionClient,
        RutaClient rutaClient,
        SolicitudClient solicitudClient,
        TramoClient tramoClient,
        CostoBasePorVolumenManager costoBasePorVolumenManager
    ) {
        this.tarifaRepository = tarifaRepository;
        this.camionClient = camionClient;
        this.rutaClient = rutaClient;
        this.solicitudClient = solicitudClient;
        this.tramoClient = tramoClient;
        this.costoBasePorVolumenManager = costoBasePorVolumenManager;
    }


    public Tarifa getTarifaPorId(Integer id) {
        Objects.requireNonNull(id, "El id de la tarifa no puede ser nulo");
        log.info("Recuperando tarifa {}", id);
        Tarifa tarifa = tarifaRepository.findById(id).orElse(null);
        if (tarifa == null) {
            log.warn("Tarifa {} no encontrada en repositorio", id);
        }
        return tarifa;
    }

    public List<Tarifa> getTarifas() {
        Objects.requireNonNull(tarifaRepository, "El repositorio de tarifas no puede ser nulo");
        log.info("Recuperando listado completo de tarifas");
        return tarifaRepository.findAll();
    }

    public Tarifa persistirTarifa(Tarifa tarifa) {
        Objects.requireNonNull(tarifa, "La tarifa no puede ser nula");
        Tarifa guardada = tarifaRepository.save(tarifa);
        log.info("Tarifa persistida con id {}", guardada.getIdTarifa());
        return guardada;
    }  // persistir tarifa sirve para el registro y para la modificacion

    public void eliminarTarifaPorId(Integer id) {
        Objects.requireNonNull(id, "El id de la tarifa no puede ser nulo");
        if (!tarifaRepository.existsById(id)) {
            log.warn("No se encontro la tarifa {} para eliminar", id);
            throw new EntityNotFoundException("Tarifa no encontrada con id: " + id);
        }
        log.info("Eliminando tarifa {}", id);
        tarifaRepository.deleteById(id);
    }

    public PromedioAtributosDTO obtenerPromedioAtributos() {
        log.info("Calculando promedio de atributos de tarifas");
        List<Tarifa> tarifas = tarifaRepository.findAll();
        
        if (tarifas.isEmpty()) {
            log.warn("No hay tarifas registradas para calcular promedios");
            return new PromedioAtributosDTO(0.0, 0.0, 0.0);
        }
        
        double promedioCostoBaseXKm = tarifas.stream()
                .mapToDouble(Tarifa::getCostoBaseXKm)
                .average()
                .orElse(0.0);
        
        double promedioValorLitroCombustible = tarifas.stream()
                .mapToDouble(Tarifa::getValorLitroCombustible)
                .average()
                .orElse(0.0);
        
        double promedioConsumoCombustibleGeneral = tarifas.stream()
                .mapToDouble(Tarifa::getConsumoCombustibleGeneral)
                .average()
                .orElse(0.0);
        
        return new PromedioAtributosDTO(
                promedioCostoBaseXKm,
                promedioValorLitroCombustible,
                promedioConsumoCombustibleGeneral
        );
    }

    public TarifaAproximadaResponseDTO calcularTarifaAproximada(Integer rutaId) {
        Objects.requireNonNull(rutaId, "La id de la ruta no puede ser nula");

        log.info("Calculando tarifa aproximada para ruta {}", rutaId);
        RutaRemotaDTO ruta = rutaClient.obtenerRuta(rutaId);
        if (ruta == null) {
            log.warn("Ruta {} no encontrada al calcular tarifa aproximada", rutaId);
            throw new EntityNotFoundException("Ruta no encontrada con id: " + rutaId);
        }

        Integer solicitudId = ruta.getIdSolicitud();
        if (solicitudId == null) {
            log.warn("Ruta {} no tiene solicitud asociada", rutaId);
            throw new IllegalStateException("La ruta no posee una solicitud asociada");
        }

        SolicitudRemotaDTO solicitud = solicitudClient.obtenerSolicitud(solicitudId);
        if (solicitud == null || solicitud.getContenedor() == null) {
            log.warn("Solicitud {} sin contenedor al calcular tarifa aproximada", solicitudId);
            throw new EntityNotFoundException("No se encontró el contenedor para la solicitud: " + solicitudId);
        }

        Double volumenContenedor = solicitud.getContenedor().getVolumen();
        if (volumenContenedor == null) {
            log.warn("Solicitud {} no posee volumen de contenedor informado", solicitudId);
            throw new IllegalStateException("El contenedor no posee volumen informado");
        }

        List<TramoRemotoDTO> tramos = ruta.getTramos() != null ? ruta.getTramos() : Collections.emptyList();
        if (tramos.isEmpty()) {
            log.warn("Ruta {} no tiene tramos para tarifa aproximada", rutaId);
            throw new IllegalStateException("La ruta no posee tramos para calcular la tarifa aproximada");
        }

        int cantidadDepositos = ruta.getCantidadDepositos() != null
            ? ruta.getCantidadDepositos()
            : estimarDepositos(tramos);

        log.debug("Ruta {} considera {} depositos", rutaId, cantidadDepositos);

        double incrementoPorVolumen = costoBasePorVolumenManager.calcularCostoBasePorKilometro(volumenContenedor);
        log.debug("Incremento por volumen para solicitud {}: {}", solicitudId, incrementoPorVolumen);

        List<CamionResumenRemotoDTO> camiones = camionClient.obtenerCamiones();
        double pesoRequerido = nullSafe(solicitud.getContenedor().getPeso());

        // Incluye todos los tipos de camión capaces de transportar el contenedor, sin discriminar por asignación previa
        Set<Integer> tiposCamionAptos = camiones.stream()
            .filter(camion -> nullSafe(camion.getCapacidadPeso()) >= pesoRequerido)
            .filter(camion -> nullSafe(camion.getCapacidadVolumen()) >= volumenContenedor)
            .map(CamionResumenRemotoDTO::getTipoCamionId)
            .filter(Objects::nonNull)
            .collect(Collectors.toSet());

        if (tiposCamionAptos.isEmpty()) {
            log.warn("No existen camiones aptos para transportar el contenedor de la solicitud {}", solicitudId);
            throw new IllegalStateException("No existen camiones aptos para transportar el contenedor solicitado");
        }

        List<TarifaPromedioProjection> promedios = tarifaRepository.obtenerPromediosPorTipoCamion();
        List<TarifaPromedioProjection> promediosAptos = promedios.stream()
            .filter(promedio -> tiposCamionAptos.contains(promedio.getTipoCamionId()))
            .collect(Collectors.toList());

        if (promediosAptos.isEmpty()) {
            log.warn("No hay tarifas configuradas para los tipos de camion aptos de la solicitud {}", solicitudId);
            throw new EntityNotFoundException("No hay tarifas configuradas para los tipos de camión aptos");
        }

        double promedioCostoBase = promedio(promediosAptos, TarifaPromedioProjection::getCostoBaseXKmPromedio);
        double promedioConsumo = promedio(promediosAptos, TarifaPromedioProjection::getConsumoCombustibleGeneralPromedio);
        double promedioValorLitro = promedio(promediosAptos, TarifaPromedioProjection::getValorLitroCombustiblePromedio);

        double costoBasePorKilometro = incrementoPorVolumen + promedioCostoBase;
        double costoCombustibleUnitario = promedioConsumo * promedioValorLitro;

        double distanciaTotal = 0.0d;
        double costoEstadiaTotal = 0.0d;
        double costoTotalRuta = 0.0d;
        List<TarifaTramoCostoDTO> costosTramos = new ArrayList<>();

        for (TramoRemotoDTO tramo : tramos) {
            double distanciaKm = convertirMetrosAKilometros(tramo.getDistancia());
            double costoActualTramo = nullSafe(tramo.getCostoAproximado());

            double costoBaseTramo = distanciaKm * costoBasePorKilometro;
            double costoCombustibleTramo = distanciaKm * costoCombustibleUnitario;
            double costoTramo = costoBaseTramo + costoCombustibleTramo + costoActualTramo;

            distanciaTotal += distanciaKm;
            costoEstadiaTotal += costoActualTramo;
            costoTotalRuta += costoTramo;

            costosTramos.add(TarifaTramoCostoDTO.builder()
                .tramoId(tramo.getId())
                .costoTramo(costoTramo)
                .build());

            double incrementoCosto = costoTramo - costoActualTramo;
            if (tramo.getId() != null && incrementoCosto != 0.0d) {
                tramoClient.actualizarCostoAproximado(tramo.getId(), incrementoCosto);
                log.debug("Actualizado costo aproximado del tramo {} con incremento {}", tramo.getId(), incrementoCosto);
            }
        }

        solicitudClient.actualizarCostoEstimado(solicitudId, costoTotalRuta);
        log.info("Tarifa aproximada calculada para ruta {} y solicitud {} con costo total {}", rutaId, solicitudId, costoTotalRuta);

        return TarifaAproximadaResponseDTO.builder()
            .rutaId(ruta.resolveRutaId())
            .solicitudId(solicitudId)
            .volumenContenedor(volumenContenedor)
            .pesoContenedor(solicitud.getContenedor().getPeso())
            .distanciaTotal(distanciaTotal)
            .cantidadDepositos(cantidadDepositos)
            .costoEstadiaAcumulado(costoEstadiaTotal)
            .costoTotalRuta(costoTotalRuta)
            .costosTramos(costosTramos)
            .build();
    }

    public TarifaRealResponseDTO calcularTarifaReal(Integer rutaId) {
        Objects.requireNonNull(rutaId, "La id de la ruta no puede ser nula");

        log.info("Calculando tarifa real para ruta {}", rutaId);
        RutaRemotaDTO ruta = rutaClient.obtenerRuta(rutaId);
        if (ruta == null) {
            log.warn("Ruta {} no encontrada al calcular tarifa real", rutaId);
            throw new EntityNotFoundException("Ruta no encontrada con id: " + rutaId);
        }

        Integer solicitudId = ruta.getIdSolicitud();
        if (solicitudId == null) {
            log.warn("Ruta {} no tiene solicitud asociada", rutaId);
            throw new IllegalStateException("La ruta no posee una solicitud asociada");
        }

        SolicitudRemotaDTO solicitud = solicitudClient.obtenerSolicitud(solicitudId);
        if (solicitud == null || solicitud.getContenedor() == null) {
            log.warn("Solicitud {} sin contenedor al calcular tarifa real", solicitudId);
            throw new EntityNotFoundException("No se encontró el contenedor para la solicitud: " + solicitudId);
        }

        Double volumenContenedor = solicitud.getContenedor().getVolumen();
        if (volumenContenedor == null) {
            log.warn("Solicitud {} no posee volumen de contenedor informado", solicitudId);
            throw new IllegalStateException("El contenedor no posee volumen informado");
        }

        List<TramoRemotoDTO> tramos = ruta.getTramos() != null ? ruta.getTramos() : Collections.emptyList();
        if (tramos.isEmpty()) {
            log.warn("Ruta {} no tiene tramos para tarifa real", rutaId);
            throw new IllegalStateException("La ruta no posee tramos para calcular la tarifa real");
        }

        int cantidadDepositos = ruta.getCantidadDepositos() != null
            ? ruta.getCantidadDepositos()
            : estimarDepositos(tramos);

        double incrementoPorVolumen = costoBasePorVolumenManager.calcularCostoBasePorKilometro(volumenContenedor);

        double distanciaTotal = 0.0d;
        double costoEstadiaTotal = 0.0d;
        double costoTotalRuta = 0.0d;
        List<TarifaTramoCostoDTO> costosTramos = new ArrayList<>();

        for (TramoRemotoDTO tramo : tramos) {
            if (!tramo.isFinalizado()) {
                continue;
            }

            CamionRemotoDTO camion = tramo.getCamion();
            if (camion == null || camion.getTipoCamion() == null) {
                log.warn("Tramo {} no posee camion asignado para calcular costo real", tramo.getId());
                throw new IllegalStateException("El tramo " + tramo.getId() + " no posee un camión asignado para calcular el costo real");
            }

            Integer tipoCamionId = camion.getTipoCamion().resolveTipoCamionId();
            if (tipoCamionId == null) {
                log.warn("Tramo {} no posee un tipo de camion identificable", tramo.getId());
                throw new IllegalStateException("El tramo " + tramo.getId() + " tiene un tipo de camión no identificado");
            }

            Tarifa tarifa = tarifaRepository.findTopByTipoCamionOrderByIdTarifaDesc(tipoCamionId)
                .orElseThrow(() -> new EntityNotFoundException("No hay tarifas configuradas para el tipo de camión " + tipoCamionId));

            double distanciaKm = convertirMetrosAKilometros(tramo.getDistancia());
            double costoEstadia = nullSafe(tramo.getCostoAproximado());
            double costoBasePorKilometro = incrementoPorVolumen + nullSafe(tarifa.getCostoBaseXKm());
            double costoCombustibleUnitario = nullSafe(tarifa.getConsumoCombustibleGeneral()) * nullSafe(tarifa.getValorLitroCombustible());
            double costoTramo = distanciaKm * (costoBasePorKilometro + costoCombustibleUnitario) + costoEstadia;

            distanciaTotal += distanciaKm;
            costoEstadiaTotal += costoEstadia;
            costoTotalRuta += costoTramo;

            costosTramos.add(TarifaTramoCostoDTO.builder()
                .tramoId(tramo.getId())
                .costoTramo(costoTramo)
                .build());

            if (tramo.getId() != null && !sonIguales(tramo.getCostoReal(), costoTramo)) {
                tramoClient.actualizarCostoReal(tramo.getId(), costoTramo);
                log.debug("Actualizado costo real del tramo {} a {}", tramo.getId(), costoTramo);
            }
        }

        if (costosTramos.isEmpty()) {
            log.warn("No hay tramos finalizados para la ruta {}", rutaId);
            throw new IllegalStateException("No hay tramos finalizados para calcular la tarifa real");
        }

        solicitudClient.actualizarCostoFinal(solicitudId, costoTotalRuta);
        log.info("Tarifa real calculada para ruta {} y solicitud {} con costo total {}", rutaId, solicitudId, costoTotalRuta);

        return TarifaRealResponseDTO.builder()
            .rutaId(ruta.resolveRutaId())
            .solicitudId(solicitudId)
            .volumenContenedor(volumenContenedor)
            .pesoContenedor(solicitud.getContenedor().getPeso())
            .distanciaTotal(distanciaTotal)
            .cantidadDepositos(cantidadDepositos)
            .costoEstadiaAcumulado(costoEstadiaTotal)
            .costoTotalRuta(costoTotalRuta)
            .costosTramos(costosTramos)
            .build();
    }

    private int estimarDepositos(List<TramoRemotoDTO> tramos) {
        long tramosConEstadia = tramos.stream()
            .map(TramoRemotoDTO::getCostoAproximado)
            .filter(Objects::nonNull)
            .filter(costo -> costo > 0)
            .count();
        return (int) tramosConEstadia;
    }

    private double nullSafe(Double value) {
        return value != null ? value : 0.0;
    }

    private double convertirMetrosAKilometros(Double distanciaMetros) {
        return distanciaMetros != null ? distanciaMetros / 1_000d : 0.0d;
    }

    private double promedio(List<TarifaPromedioProjection> promedios, Function<TarifaPromedioProjection, Double> extractor) {
        return promedios.stream()
            .map(extractor)
            .filter(Objects::nonNull)
            .mapToDouble(Double::doubleValue)
            .average()
            .orElse(0.0d);
    }

    private boolean sonIguales(Double valorOriginal, double valorCalculado) {
        if (valorOriginal == null) {
            return false;
        }
        return Math.abs(valorOriginal - valorCalculado) < 0.01d;
    }
}
