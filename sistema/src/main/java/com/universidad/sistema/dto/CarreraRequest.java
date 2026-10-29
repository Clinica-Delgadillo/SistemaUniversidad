package com.universidad.sistema.dto;

import lombok.Data;

@Data
public class CarreraRequest {

    private String nombre;
    private String descripcion;
    private Long divisionId;

}