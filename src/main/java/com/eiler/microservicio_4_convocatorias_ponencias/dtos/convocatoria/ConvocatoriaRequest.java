/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.eiler.microservicio_4_convocatorias_ponencias.dtos.convocatoria;

/**
 *
 * @author eiler
 */

import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConvocatoriaRequest {

    @NotNull(message = "El id del congreso es obligatorio")
    private Long idCongreso;

    @NotBlank(message = "El nombre de la convocatoria es obligatorio")
    @Size(max = 255, message = "El nombre no puede superar 255 caracteres")
    private String nombreConvocatoria;

    private String descripcion;

    @NotNull(message = "La fecha de apertura es obligatoria")
    private LocalDateTime fechaApertura;

    @NotNull(message = "La fecha de cierre es obligatoria")
    private LocalDateTime fechaCierre;
}
