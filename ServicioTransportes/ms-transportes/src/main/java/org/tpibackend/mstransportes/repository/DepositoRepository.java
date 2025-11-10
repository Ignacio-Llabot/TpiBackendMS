package org.tpibackend.mstransportes.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.tpibackend.mstransportes.entity.Deposito;


@Repository
public interface DepositoRepository extends JpaRepository<Deposito, Integer> {
}