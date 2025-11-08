package org.tpibackend.mstransportes.repositories;

import org.tpibackend.mstransportes.models.Ubicacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.lang.NonNull;
import java.util.Optional;

@Repository
public interface UbicacionRepository extends JpaRepository<Ubicacion, Integer> {
    @NonNull Optional<Ubicacion> findById(@NonNull Integer id);
}