/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.eiler.microservicio_4_convocatorias_ponencias.dtos.ponencias;

/**
 *
 * @author eiler
 */


import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PonenciaRequest {

    @NotNull(message = "El id de la convocatoria es obligatorio")
    private Long idConvocatoria;

    @NotNull(message = "El tipo de actividad es obligatorio")
    private Integer idTipoActividad;

    @NotBlank(message = "El título de la ponencia es obligatorio")
    @Size(max = 255, message = "El título no puede superar 255 caracteres")
    private String tituloPonencia;

    @NotBlank(message = "El resumen es obligatorio")
    private String resumen;

    private String urlArchivo;
}
