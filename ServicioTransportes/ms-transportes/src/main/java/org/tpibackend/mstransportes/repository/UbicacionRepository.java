package org.tpibackend.mstransportes.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.tpibackend.mstransportes.entity.Ubicacion;

@Repository
public interface UbicacionRepository extends JpaRepository<Ubicacion, Integer> {
	Optional<Ubicacion> findByLatitudAndLongitud(Double latitud, Double longitud);
}