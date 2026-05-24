package com.eiler.microservicio_4_convocatorias_ponencias.dtos.ponencias;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;


public class PonenciaRequest {

    @NotNull(message = "El id de convocatoria es obligatorio")
    @Positive
    private Long idConvocatoria;

    @NotNull(message = "El id de tipo actividad es obligatorio")
    @Positive
    private Integer idTipoActividad;

    @NotBlank(message = "El título es obligatorio")
    private String tituloPonencia;

    private String resumen;

    public PonenciaRequest() {}

    public Long idConvocatoria()    { return idConvocatoria; }
    public Integer idTipoActividad() { return idTipoActividad; }
    public String tituloPonencia()  { return tituloPonencia; }
    public String resumen()         { return resumen; }

    public void setIdConvocatoria(Long idConvocatoria)      { this.idConvocatoria = idConvocatoria; }
    public void setIdTipoActividad(Integer idTipoActividad) { this.idTipoActividad = idTipoActividad; }
    public void setTituloPonencia(String tituloPonencia)    { this.tituloPonencia = tituloPonencia; }
    public void setResumen(String resumen)                  { this.resumen = resumen; }
}