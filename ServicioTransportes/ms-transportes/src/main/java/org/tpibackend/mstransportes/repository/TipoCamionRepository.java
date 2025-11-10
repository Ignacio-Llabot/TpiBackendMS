package org.tpibackend.mstransportes.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.tpibackend.mstransportes.entity.TipoCamion;

@Repository
public interface TipoCamionRepository extends JpaRepository<TipoCamion, Integer> {
}