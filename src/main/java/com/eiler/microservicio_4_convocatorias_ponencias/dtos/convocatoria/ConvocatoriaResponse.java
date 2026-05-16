/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.eiler.microservicio_4_convocatorias_ponencias.dtos.convocatoria;

/**
 *
 * @author eiler
 */

import com.eiler.microservicio_4_convocatorias_ponencias.modelos.convocatoria.Convocatoria;
import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConvocatoriaResponse {

    private Long idConvocatoria;
    private Long idCongreso;
    private String nombreConvocatoria;
    private String descripcion;
    private LocalDateTime fechaApertura;
    private LocalDateTime fechaCierre;
    private Boolean estaAbierta;
    private LocalDateTime creadoEn;
    private LocalDateTime actualizadoEn;

    // Constructor desde entidad — igual que tu ejemplo
    public ConvocatoriaResponse(Convocatoria c) {
        this.idConvocatoria    = c.getIdConvocatoria();
        this.idCongreso        = c.getIdCongreso();
        this.nombreConvocatoria = c.getNombreConvocatoria();
        this.descripcion       = c.getDescripcion();
        this.fechaApertura     = c.getFechaApertura();
        this.fechaCierre       = c.getFechaCierre();
        this.estaAbierta       = c.getEstaAbierta();
        this.creadoEn          = c.getCreadoEn();
        this.actualizadoEn     = c.getActualizadoEn();
    }
}