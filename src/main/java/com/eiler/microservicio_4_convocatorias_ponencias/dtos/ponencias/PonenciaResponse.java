/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.eiler.microservicio_4_convocatorias_ponencias.dtos.ponencias;

/**
 *
 * @author eiler
 */

import com.eiler.microservicio_4_convocatorias_ponencias.modelos.ponencias.Ponencia;
import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PonenciaResponse {

    private Long    idPonencia;
    private Long    idConvocatoria;
    private String  nombreConvocatoria;
    private Long    idUsuario;
    private Integer idTipoActividad;
    private Long    idEstado;
    private String  nombreEstado;
    private String  tituloPonencia;
    private String  resumen;
    private String  urlArchivo;
    private LocalDateTime enviadoEn;
    private LocalDateTime actualizadoEn;

    public PonenciaResponse(Ponencia p) {
        this.idPonencia        = p.getIdPonencia();
        this.idConvocatoria    = p.getConvocatoria().getIdConvocatoria();
        this.nombreConvocatoria = p.getConvocatoria().getNombreConvocatoria();
        this.idUsuario         = p.getIdUsuario();
        this.idTipoActividad   = p.getIdTipoActividad();
        this.idEstado          = p.getEstado().getIdEstado();
        this.nombreEstado      = p.getEstado().getNombreEstado();
        this.tituloPonencia    = p.getTituloPonencia();
        this.resumen           = p.getResumen();
        this.urlArchivo        = p.getUrlArchivo();
        this.enviadoEn         = p.getEnviadoEn();
        this.actualizadoEn     = p.getActualizadoEn();
    }
}