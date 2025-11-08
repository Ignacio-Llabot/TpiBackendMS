package org.tpibackend.mstransportes.repositories;

import org.tpibackend.mstransportes.models.TipoCamion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TipoCamionRepository extends JpaRepository<TipoCamion, Integer> {
}