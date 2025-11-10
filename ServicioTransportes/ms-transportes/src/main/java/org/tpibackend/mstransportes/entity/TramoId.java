package org.tpibackend.mstransportes.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class TramoId implements Serializable {

    private static final long serialVersionUID = 1L;

    @Column(name = "\"idTramo\"")
    private Integer idTramo;

    @Column(name = "\"idRuta\"")
    private Integer idRuta;
}
