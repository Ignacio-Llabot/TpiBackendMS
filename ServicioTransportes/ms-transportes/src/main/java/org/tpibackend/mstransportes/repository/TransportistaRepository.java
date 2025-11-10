package org.tpibackend.mstransportes.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.tpibackend.mstransportes.entity.Transportista;

@Repository
public interface TransportistaRepository extends JpaRepository<Transportista, Integer> {
}