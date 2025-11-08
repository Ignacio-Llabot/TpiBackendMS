package org.tpibackend.mstransportes.repositories;

import org.tpibackend.mstransportes.models.Transportista;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransportistaRepository extends JpaRepository<Transportista, Integer> {
}