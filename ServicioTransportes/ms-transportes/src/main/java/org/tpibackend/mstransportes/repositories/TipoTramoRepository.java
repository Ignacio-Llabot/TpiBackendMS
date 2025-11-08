package org.tpibackend.mstransportes.repositories;

import org.tpibackend.mstransportes.models.TipoTramo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.lang.NonNull;
import java.util.Optional;

@Repository
public interface TipoTramoRepository extends JpaRepository<TipoTramo, Integer> {
    @NonNull Optional<TipoTramo> findById(@NonNull Integer id);
    @NonNull Optional<TipoTramo> findByNombre(@NonNull String nombre);
}