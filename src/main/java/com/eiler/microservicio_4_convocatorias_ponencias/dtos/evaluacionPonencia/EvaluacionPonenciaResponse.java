/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.eiler.microservicio_4_convocatorias_ponencias.dtos.evaluacionPonencia;

/**
 *
 * @author eiler
 */
// dtos/evaluacionPonencia/EvaluacionPonenciaResponse.java

import com.eiler.microservicio_4_convocatorias_ponencias.modelos.evaluacionPonencia.EvaluacionPonencia;
import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EvaluacionPonenciaResponse {

    private Long    idEvaluacion;
    private Long    idPonencia;
    private String  tituloPonencia;
    private Long    idEvaluador;
    private String  comentarios;
    private Boolean estaAprobado;
    private EstadoPonencia estadoPonencia;
    private LocalDateTime evaluadoEn;

    public EvaluacionPonenciaResponse(EvaluacionPonencia e) {
        this.idEvaluacion   = e.getIdEvaluacion();
        this.idPonencia     = e.getPonencia().getIdPonencia();
        this.tituloPonencia = e.getPonencia().getTituloPonencia();
        this.idEvaluador    = e.getIdEvaluador();
        this.comentarios    = e.getComentarios();
        this.estaAprobado   = e.getEstaAprobado();
        this.estadoPonencia = e.getEstaAprobado()
                       ? EstadoPonencia.APROBADO
                       : EstadoPonencia.RECHAZADO;        
        this.evaluadoEn     = e.getEvaluadoEn();
        }
    public enum EstadoPonencia {
        APROBADO,
        RECHAZADO
    }
}

