/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.eiler.microservicio_4_convocatorias_ponencias.dtos.ponencias;


import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
 
/**
 *
 * @author eiler
 */


@Builder
public record PonenciaRequest(
 
        @NotNull(message = "El id de convocatoria es obligatorio")
        @Positive
        Long idConvocatoria,
 
        @NotNull(message = "El id de tipo actividad es obligatorio")
        @Positive
        Integer idTipoActividad,
 
        @NotNull(message = "El título es obligatorio")
        String tituloPonencia,
 
        String resumen
 
) {}

