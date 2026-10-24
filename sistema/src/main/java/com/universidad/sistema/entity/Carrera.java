package com.universidad.sistema.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "carreras")
public class Carrera {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;

    @Column(length = 1000)
    private String descripcion;

    // Una carrera pertenece a una division
    @ManyToOne
    @JoinColumn(name = "division_id")
    private Division division;

    // Campos del sistema anterior
    private String facultad;
    private String estado;

}