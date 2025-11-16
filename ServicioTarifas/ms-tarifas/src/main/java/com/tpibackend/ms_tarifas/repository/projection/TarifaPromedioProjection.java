package com.tpibackend.ms_tarifas.repository.projection;

public interface TarifaPromedioProjection {
    Integer getTipoCamionId();
    Double getCostoBaseXKmPromedio();
    Double getValorLitroCombustiblePromedio();
    Double getConsumoCombustibleGeneralPromedio();
}
