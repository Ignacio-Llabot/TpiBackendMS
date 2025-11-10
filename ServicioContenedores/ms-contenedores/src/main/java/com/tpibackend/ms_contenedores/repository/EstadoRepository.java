package com.tpibackend.ms_contenedores.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.tpibackend.ms_contenedores.entity.Estado;
import org.springframework.stereotype.Repository;

@Repository
public interface EstadoRepository extends JpaRepository<Estado, Integer> {
    
}
