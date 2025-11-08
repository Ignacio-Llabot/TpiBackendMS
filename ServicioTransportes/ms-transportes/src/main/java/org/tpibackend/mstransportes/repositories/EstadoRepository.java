package org.tpibackend.mstransportes.repositories;

import org.tpibackend.mstransportes.models.Estado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import org.springframework.lang.NonNull;

@Repository
public interface EstadoRepository extends JpaRepository<Estado, Integer> {

    @NonNull Optional<Estado> findById(@NonNull Integer id);

    @NonNull Optional<Estado> findByNombre(@NonNull String nombre);
}