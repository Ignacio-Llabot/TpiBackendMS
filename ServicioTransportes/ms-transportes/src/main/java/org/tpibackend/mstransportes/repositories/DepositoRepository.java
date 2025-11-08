package org.tpibackend.mstransportes.repositories;

import org.tpibackend.mstransportes.models.Deposito;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface DepositoRepository extends JpaRepository<Deposito, Integer> {
}