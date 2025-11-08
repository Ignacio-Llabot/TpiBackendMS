package org.tpibackend.mstransportes.repositories;

import org.tpibackend.mstransportes.models.TipoTramo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TipoTramoRepository extends JpaRepository<TipoTramo, Integer> {
}