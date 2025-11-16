package com.tpibackend.ms_tarifas.repository;

import com.tpibackend.ms_tarifas.entity.Tarifa;
import com.tpibackend.ms_tarifas.repository.projection.TarifaPromedioProjection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TarifaRepository extends JpaRepository<Tarifa, Integer> {
	@Query("SELECT t.tipoCamion AS tipoCamionId, AVG(t.costoBaseXKm) AS costoBaseXKmPromedio, "
		+ "AVG(t.valorLitroCombustible) AS valorLitroCombustiblePromedio, "
		+ "AVG(t.consumoCombustibleGeneral) AS consumoCombustibleGeneralPromedio "
		+ "FROM Tarifa t GROUP BY t.tipoCamion")
	List<TarifaPromedioProjection> obtenerPromediosPorTipoCamion();
}
