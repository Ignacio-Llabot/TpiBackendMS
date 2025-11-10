package org.tpibackend.mstransportes.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.tpibackend.mstransportes.entity.Estado;

@Repository
public interface EstadoRepository extends JpaRepository<Estado, Integer> {
}