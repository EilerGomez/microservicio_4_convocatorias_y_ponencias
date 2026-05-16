/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.eiler.microservicio_4_convocatorias_ponencias.modelos.ponencias;

/**
 *
 * @author eiler
 */

public enum EstadoPonenciaEnum {
    PENDIENTE(1L),
    APROBADO(2L),
    RECHAZADO(3L);

    private final Long id;

    EstadoPonenciaEnum(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}
