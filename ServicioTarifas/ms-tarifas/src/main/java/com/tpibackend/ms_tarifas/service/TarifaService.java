package com.tpibackend.ms_tarifas.service;

import com.tpibackend.ms_tarifas.dto.PromedioAtributosDTO;
import com.tpibackend.ms_tarifas.dto.TarifaAproximadaResponseDTO;
import com.tpibackend.ms_tarifas.dto.TarifaTramoCostoDTO;
import com.tpibackend.ms_tarifas.entity.Tarifa;
import com.tpibackend.ms_tarifas.external.CamionClient;
import com.tpibackend.ms_tarifas.external.RutaClient;
import com.tpibackend.ms_tarifas.external.SolicitudClient;
import com.tpibackend.ms_tarifas.external.dto.CamionResumenRemotoDTO;
import com.tpibackend.ms_tarifas.external.dto.RutaRemotaDTO;
import com.tpibackend.ms_tarifas.external.dto.SolicitudRemotaDTO;
import com.tpibackend.ms_tarifas.external.dto.TramoRemotoDTO;
import com.tpibackend.ms_tarifas.repository.TarifaRepository;
import com.tpibackend.ms_tarifas.repository.projection.TarifaPromedioProjection;
import jakarta.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;


@Service
public class TarifaService {
    
    private final TarifaRepository tarifaRepository;
    private final CamionClient camionClient;
    private final RutaClient rutaClient;
    private final SolicitudClient solicitudClient;
    private final CostoBasePorVolumenManager costoBasePorVolumenManager;

    public TarifaService(
        TarifaRepository tarifaRepository,
        CamionClient camionClient,
        RutaClient rutaClient,
        SolicitudClient solicitudClient,
        CostoBasePorVolumenManager costoBasePorVolumenManager
    ) {
        this.tarifaRepository = tarifaRepository;
        this.camionClient = camionClient;
        this.rutaClient = rutaClient;
        this.solicitudClient = solicitudClient;
        this.costoBasePorVolumenManager = costoBasePorVolumenManager;
    }


    public Tarifa getTarifaPorId(Integer id) {
        Objects.requireNonNull(id, "El id de la tarifa no puede ser nulo");
        return tarifaRepository.findById(id).orElse(null);
    }

    public List<Tarifa> getTarifas() {
        Objects.requireNonNull(tarifaRepository, "El repositorio de tarifas no puede ser nulo");
        return tarifaRepository.findAll();
    }

    public Tarifa persistirTarifa(Tarifa tarifa) {
        Objects.requireNonNull(tarifa, "La tarifa no puede ser nula");
        return tarifaRepository.save(tarifa);
    }  // persistir tarifa sirve para el registro y para la modificacion

    public void eliminarTarifaPorId(Integer id) {
        Objects.requireNonNull(id, "El id de la tarifa no puede ser nulo");
        if (!tarifaRepository.existsById(id)) {
            throw new EntityNotFoundException("Tarifa no encontrada con id: " + id);
        }
        tarifaRepository.deleteById(id);
    }

    public PromedioAtributosDTO obtenerPromedioAtributos() {
        List<Tarifa> tarifas = tarifaRepository.findAll();
        
        if (tarifas.isEmpty()) {
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

        RutaRemotaDTO ruta = rutaClient.obtenerRuta(rutaId);
        if (ruta == null) {
            throw new EntityNotFoundException("Ruta no encontrada con id: " + rutaId);
        }

        Integer solicitudId = ruta.getIdSolicitud();
        if (solicitudId == null) {
            throw new IllegalStateException("La ruta no posee una solicitud asociada");
        }

        SolicitudRemotaDTO solicitud = solicitudClient.obtenerSolicitud(solicitudId);
        if (solicitud == null || solicitud.getContenedor() == null) {
            throw new EntityNotFoundException("No se encontró el contenedor para la solicitud: " + solicitudId);
        }

        Double volumenContenedor = solicitud.getContenedor().getVolumen();
        if (volumenContenedor == null) {
            throw new IllegalStateException("El contenedor no posee volumen informado");
        }

        List<TramoRemotoDTO> tramos = ruta.getTramos() != null ? ruta.getTramos() : Collections.emptyList();
        if (tramos.isEmpty()) {
            throw new IllegalStateException("La ruta no posee tramos para calcular la tarifa aproximada");
        }

        int cantidadDepositos = ruta.getCantidadDepositos() != null
            ? ruta.getCantidadDepositos()
            : estimarDepositos(tramos);

        double incrementoPorVolumen = costoBasePorVolumenManager.calcularCostoBasePorKilometro(volumenContenedor);

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
            throw new IllegalStateException("No existen camiones aptos para transportar el contenedor solicitado");
        }

        List<TarifaPromedioProjection> promedios = tarifaRepository.obtenerPromediosPorTipoCamion();
        List<TarifaPromedioProjection> promediosAptos = promedios.stream()
            .filter(promedio -> tiposCamionAptos.contains(promedio.getTipoCamionId()))
            .collect(Collectors.toList());

        if (promediosAptos.isEmpty()) {
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
            double costoEstadiaTramo = nullSafe(tramo.getCostoAproximado());

            double costoBaseTramo = distanciaKm * costoBasePorKilometro;
            double costoCombustibleTramo = distanciaKm * costoCombustibleUnitario;
            double costoTramo = costoBaseTramo + costoCombustibleTramo + costoEstadiaTramo;

            distanciaTotal += distanciaKm;
            costoEstadiaTotal += costoEstadiaTramo;
            costoTotalRuta += costoTramo;

            costosTramos.add(TarifaTramoCostoDTO.builder()
                .tramoId(tramo.getId())
                .costoTramo(costoTramo)
                .build());
        }

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

}
