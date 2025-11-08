package org.tpibackend.mstransportes.repositories;

import org.tpibackend.mstransportes.models.Tramo;
import org.tpibackend.mstransportes.models.TramoId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TramoRepository extends JpaRepository<Tramo, TramoId> {
}