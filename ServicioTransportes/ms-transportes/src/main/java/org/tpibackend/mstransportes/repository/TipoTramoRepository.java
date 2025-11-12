package org.tpibackend.mstransportes.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.tpibackend.mstransportes.entity.TipoTramo;

@Repository
public interface TipoTramoRepository extends JpaRepository<TipoTramo, Integer> {
	Optional<TipoTramo> findByNombre(String nombre);
}