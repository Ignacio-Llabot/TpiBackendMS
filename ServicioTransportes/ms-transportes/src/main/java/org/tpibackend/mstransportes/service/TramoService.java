package org.tpibackend.mstransportes.service;

import java.sql.Date;
import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;
import org.tpibackend.mstransportes.dto.TramoDTO;
import org.tpibackend.mstransportes.entity.Ruta;
import org.tpibackend.mstransportes.entity.Tramo;
import org.tpibackend.mstransportes.entity.Ubicacion;
import org.tpibackend.mstransportes.repository.TramoRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class TramoService {
    
    private final TramoRepository tramoRepository;

    private final OsrmService osrmService;

    public TramoService(TramoRepository tramoRepository, OsrmService osrmService) {
        this.tramoRepository = tramoRepository;
        this.osrmService = osrmService;
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

    public List<Tramo> calcularTramos(
        Ruta ruta,
        Ubicacion ubicacionInicio,
        Ubicacion ubicacionFin,
        Date fechaHoraInicio
    ) {

        List<TramoDTO> tramoDTOs = osrmService.calcularRutaConDepositos(
            ubicacionInicio,
            ubicacionFin
        );

        // TODO procesamiento de tramoDTO a tramo

        // acá seguro tenga que agregar un acumulador pero en fecha para ir viendo la fecha hora inicio y la fechaHoraFinEstimada
        // TODO sacar notNull de patenteCamion en la BD y en la entidad!!!!


        List<Tramo> tramos = tramoDTOs.stream().map(dto -> {
            Tramo tramo = new Tramo();
            tramo.setRuta(ruta);
            tramo.setUbicacionOrigen(dto.getUbicacionOrigen());
            tramo.setUbicacionDestino(dto.getUbicacionDestino());
            tramo.setDistancia(dto.getDistancia());

            // ver como asignar tipoTramo
            // ver como asignar estado
            // ver como asignar costoAproximado ( not nullable btw )

            // completar mapeo de dto a entidad tramo
            return tramo;
        }).toList();

        // acá persistir los tramos antes de devolverlos
        guardarTramos(tramos);

        return tramos;
    }
}
