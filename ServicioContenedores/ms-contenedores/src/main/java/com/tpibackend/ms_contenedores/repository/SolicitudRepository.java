package com.tpibackend.ms_contenedores.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.tpibackend.ms_contenedores.entity.Solicitud;
import org.springframework.stereotype.Repository;

@Repository
public interface SolicitudRepository extends JpaRepository<Solicitud, Integer> {
    //NIGG
}
