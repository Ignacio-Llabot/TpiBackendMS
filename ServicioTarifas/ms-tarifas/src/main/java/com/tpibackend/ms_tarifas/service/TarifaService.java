package com.tpibackend.ms_tarifas.service;

import com.tpibackend.ms_tarifas.dto.PromedioAtributosDTO;
import com.tpibackend.ms_tarifas.dto.TarifaAproximadaDetalleDTO;
import com.tpibackend.ms_tarifas.dto.TarifaAproximadaResponseDTO;
import com.tpibackend.ms_tarifas.entity.Tarifa;
import com.tpibackend.ms_tarifas.external.RutaClient;
import com.tpibackend.ms_tarifas.external.SolicitudClient;
import com.tpibackend.ms_tarifas.external.dto.CamionRemotoDTO;
import com.tpibackend.ms_tarifas.external.dto.RutaRemotaDTO;
import com.tpibackend.ms_tarifas.external.dto.SolicitudRemotaDTO;
import com.tpibackend.ms_tarifas.external.dto.TipoCamionRemotoDTO;
import com.tpibackend.ms_tarifas.external.dto.TramoRemotoDTO;
import com.tpibackend.ms_tarifas.repository.TarifaRepository;
import com.tpibackend.ms_tarifas.repository.projection.TarifaPromedioProjection;
import jakarta.persistence.EntityNotFoundException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;


@Service
public class TarifaService {
    
    private final TarifaRepository tarifaRepository;
    private final RutaClient rutaClient;
    private final SolicitudClient solicitudClient;
    private final CostoBasePorVolumenManager costoBasePorVolumenManager;

    public TarifaService(
        TarifaRepository tarifaRepository,
        RutaClient rutaClient,
        SolicitudClient solicitudClient,
        CostoBasePorVolumenManager costoBasePorVolumenManager
    ) {
        this.tarifaRepository = tarifaRepository;
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

        double distanciaTotal = tramos.stream()
            .map(TramoRemotoDTO::getDistancia)
            .filter(Objects::nonNull)
            .mapToDouble(Double::doubleValue)
            .sum();

        double costoEstadiaTotal = tramos.stream()
            .map(TramoRemotoDTO::getCostoAproximado)
            .filter(Objects::nonNull)
            .mapToDouble(Double::doubleValue)
            .sum();

        int cantidadDepositos = ruta.getCantidadDepositos() != null
            ? ruta.getCantidadDepositos()
            : estimarDepositos(tramos);

        double incrementoPorVolumen = costoBasePorVolumenManager.calcularCostoBasePorKilometro(volumenContenedor);

        List<TarifaPromedioProjection> promedios = tarifaRepository.obtenerPromediosPorTipoCamion();
        if (promedios.isEmpty()) {
            throw new EntityNotFoundException("No existen tarifas configuradas para calcular promedios");
        }

        Map<Integer, String> descripcionPorTipo = extraerDescripcionTipoCamion(tramos);

        List<TarifaAproximadaDetalleDTO> detalles = promedios.stream()
            .map(promedio -> construirDetalle(
                promedio,
                incrementoPorVolumen,
                distanciaTotal,
                costoEstadiaTotal,
                descripcionPorTipo.get(promedio.getTipoCamionId())
            ))
            .collect(Collectors.toList());

        return TarifaAproximadaResponseDTO.builder()
            .rutaId(ruta.resolveRutaId())
            .solicitudId(solicitudId)
            .volumenContenedor(volumenContenedor)
            .distanciaTotal(distanciaTotal)
            .cantidadDepositos(cantidadDepositos)
            .costoEstadiaAcumulado(costoEstadiaTotal)
            .detalles(detalles)
            .build();
    }

    private TarifaAproximadaDetalleDTO construirDetalle(
        TarifaPromedioProjection promedio,
        double incrementoPorVolumen,
        double distanciaTotal,
        double costoEstadiaTotal,
        String descripcionTipoCamion
    ) {
        double costoBasePorKilometro = incrementoPorVolumen + nullSafe(promedio.getCostoBaseXKmPromedio());
        double consumoPromedio = nullSafe(promedio.getConsumoCombustibleGeneralPromedio());
        double valorLitroPromedio = nullSafe(promedio.getValorLitroCombustiblePromedio());

        double costoBaseTotal = distanciaTotal * costoBasePorKilometro;

        double costoCombustibleUnitario = consumoPromedio * valorLitroPromedio;
        double costoCombustibleTotal = distanciaTotal * costoCombustibleUnitario;

        double costoTotal = costoBaseTotal + costoEstadiaTotal + costoCombustibleTotal;

        return TarifaAproximadaDetalleDTO.builder()
            .tipoCamionId(promedio.getTipoCamionId())
            .tipoCamionDescripcion(descripcionTipoCamion)
            .costoBasePorKilometro(costoBasePorKilometro)
            .valorLitroCombustiblePromedio(valorLitroPromedio)
            .consumoCombustiblePromedio(consumoPromedio)
            .costoBaseTotal(costoBaseTotal)
            .costoCombustibleTotal(costoCombustibleTotal)
            .costoEstadiaTotal(costoEstadiaTotal)
            .costoTotal(costoTotal)
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

    private Map<Integer, String> extraerDescripcionTipoCamion(List<TramoRemotoDTO> tramos) {
        Map<Integer, String> descripcionPorTipo = new HashMap<>();
        for (TramoRemotoDTO tramo : tramos) {
            CamionRemotoDTO camion = tramo.getCamion();
            if (camion == null) {
                continue;
            }
            TipoCamionRemotoDTO tipoCamion = camion.getTipoCamion();
            if (tipoCamion == null) {
                continue;
            }
            Integer tipoId = tipoCamion.resolveTipoCamionId();
            if (tipoId != null && !descripcionPorTipo.containsKey(tipoId)) {
                descripcionPorTipo.put(tipoId, tipoCamion.getNombre());
            }
        }
        return descripcionPorTipo;
    }

    private double nullSafe(Double value) {
        return value != null ? value : 0.0;
    }

}
