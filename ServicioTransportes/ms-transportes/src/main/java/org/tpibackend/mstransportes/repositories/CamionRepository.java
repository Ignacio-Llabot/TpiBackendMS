package org.tpibackend.mstransportes.repositories;

import org.tpibackend.mstransportes.models.Camion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import org.springframework.lang.NonNull;

@Repository
public interface CamionRepository extends JpaRepository<Camion, String> {
    @NonNull Optional<Camion> findByPatente(@NonNull String patente);
}