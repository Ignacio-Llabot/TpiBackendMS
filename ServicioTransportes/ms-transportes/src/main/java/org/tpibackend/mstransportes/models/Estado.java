package org.tpibackend.mstransportes.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "\"Estados\"")
public class Estado {

	@Id
	@Column(name = "\"idEstado\"")
	private Integer idEstado;

	@Column(name = "\"nombre\"", nullable = false)
	private String nombre;
}
