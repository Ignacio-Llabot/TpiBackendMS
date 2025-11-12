package org.tpibackend.mstransportes.service.osrmstategies;

import java.util.List;

import org.tpibackend.mstransportes.dto.TramoDTO;
import org.tpibackend.mstransportes.entity.Deposito;
import org.tpibackend.mstransportes.entity.Ubicacion;

public interface Strategy {
    public List<TramoDTO> calcularRuta(
        Ubicacion origen, 
        Ubicacion destino,
        List<Deposito> depositos,
        String osrmUrl
        );
}
