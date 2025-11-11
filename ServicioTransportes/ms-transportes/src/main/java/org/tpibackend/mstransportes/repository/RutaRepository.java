package org.tpibackend.mstransportes.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.tpibackend.mstransportes.entity.Ruta;

@Repository
public interface RutaRepository extends JpaRepository<Ruta, Integer> {
    java.util.Optional<Ruta> findByIdSolicitud(Integer solicitudId);
}
