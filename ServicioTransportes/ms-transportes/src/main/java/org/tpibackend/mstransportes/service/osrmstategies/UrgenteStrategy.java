package org.tpibackend.mstransportes.service.osrmstategies;

import java.util.List;

import org.tpibackend.mstransportes.dto.TramoDTO;
import org.tpibackend.mstransportes.entity.Deposito;
import org.tpibackend.mstransportes.entity.Ubicacion;

public class UrgenteStrategy implements Strategy {
    @Override
    public List<TramoDTO> calcularRuta(
        Ubicacion origen, 
        Ubicacion destino,
        List<Deposito> depositos,
        String osrmUrl
        ) {
            // Lógica específica para rutas urgentes
            return null;
    }

}
