package com.universidad.sistema.dto;

import lombok.Data;

@Data
public class CarreraResponse {

    private Long id;
    private String nombre;
    private String descripcion;
    private Long divisionId;
    private String divisionNombre;

}