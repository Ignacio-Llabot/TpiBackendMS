package org.tpibackend.mstransportes.repositories;

import org.tpibackend.mstransportes.models.Ruta;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RutaRepository extends JpaRepository<Ruta, Integer> {
    // El método findById ya está en JpaRepository; no hace falta redeclararlo.
}
