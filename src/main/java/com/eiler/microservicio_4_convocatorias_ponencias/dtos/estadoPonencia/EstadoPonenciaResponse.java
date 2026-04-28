/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.eiler.microservicio_4_convocatorias_ponencias.dtos.estadoPonencia;

/**
 *
 * @author eiler
 */

import com.eiler.microservicio_4_convocatorias_ponencias.modelos.estadoPonencia.EstadoPonencia;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EstadoPonenciaResponse {

    private Long idEstado;
    private String nombreEstado;

    public EstadoPonenciaResponse(EstadoPonencia e) {
        this.idEstado     = e.getIdEstado();
        this.nombreEstado = e.getNombreEstado();
    }
}
