package com.tpibackend.ms_tarifas.repository;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.tpibackend.ms_tarifas.entity.Tarifa;

@Repository
public interface TarifaRepository extends JpaRepository<Tarifa, Integer> {
    
}
