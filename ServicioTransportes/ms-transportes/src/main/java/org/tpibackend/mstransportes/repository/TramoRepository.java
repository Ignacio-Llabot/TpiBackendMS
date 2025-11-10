package org.tpibackend.mstransportes.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.tpibackend.mstransportes.entity.Tramo;
import org.tpibackend.mstransportes.entity.TramoId;

@Repository
public interface TramoRepository extends JpaRepository<Tramo, TramoId> {
}