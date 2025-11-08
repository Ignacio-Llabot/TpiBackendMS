package org.tpibackend.mstransportes.repositories;

import org.tpibackend.mstransportes.models.TipoCamion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface TipoCamionRepository extends JpaRepository<TipoCamion, Integer> {

    Optional<TipoCamion> findById(Integer id);

    Optional<TipoCamion> findByNombre(String nombre);
}