package org.tpibackend.mstransportes.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.tpibackend.mstransportes.entity.Camion;


@Repository
public interface CamionRepository extends JpaRepository<Camion, String> {
}