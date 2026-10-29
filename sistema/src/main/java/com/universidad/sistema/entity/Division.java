package com.universidad.sistema.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.List;
import java.util.ArrayList;

@Getter
@Setter
@Entity
@Table(name = "divisiones")
public class Division {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;

    // Una division tiene muchas carreras
    @OneToMany(mappedBy = "division")
    @JsonIgnore
    private List<Carrera> carreras = new ArrayList<>();

}