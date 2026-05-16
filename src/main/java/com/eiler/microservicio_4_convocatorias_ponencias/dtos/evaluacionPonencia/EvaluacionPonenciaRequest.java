/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.eiler.microservicio_4_convocatorias_ponencias.dtos.evaluacionPonencia;

/**
 *
 * @author eiler
 */
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EvaluacionPonenciaRequest {

    @NotNull(message = "El id de la ponencia es obligatorio")
    private Long idPonencia;

    @NotNull(message = "Debe indicar si aprueba o rechaza")
    private Boolean estaAprobado;

    private String comentarios;
}
