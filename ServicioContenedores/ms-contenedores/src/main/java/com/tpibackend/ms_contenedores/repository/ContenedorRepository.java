package com.tpibackend.ms_contenedores.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.tpibackend.ms_contenedores.entity.Contenedor;
import org.springframework.stereotype.Repository;

@Repository
public interface ContenedorRepository extends JpaRepository<Contenedor, Integer> {
    
}
