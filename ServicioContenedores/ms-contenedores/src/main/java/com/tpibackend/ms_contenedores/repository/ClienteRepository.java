package com.tpibackend.ms_contenedores.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.tpibackend.ms_contenedores.entity.Cliente;
import org.springframework.stereotype.Repository;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, String> {
}
