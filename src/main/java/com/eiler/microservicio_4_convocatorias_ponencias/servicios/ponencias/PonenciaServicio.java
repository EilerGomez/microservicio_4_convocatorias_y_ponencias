/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.eiler.microservicio_4_convocatorias_ponencias.servicios.ponencias;

/**
 *
 * @author eiler
 */

import com.eiler.microservicio_4_convocatorias_ponencias.dtos.ponencias.PonenciaRequest;
import com.eiler.microservicio_4_convocatorias_ponencias.dtos.ponencias.PonenciaResponse;
import com.eiler.microservicio_4_convocatorias_ponencias.excepciones.RecursoNoEncontradoException;
import java.util.List;

public interface PonenciaServicio {

    PonenciaResponse enviar(PonenciaRequest request, Long idUsuario)
            throws RecursoNoEncontradoException;

    PonenciaResponse obtenerPorId(Long id)
            throws RecursoNoEncontradoException;

    List<PonenciaResponse> listarPorConvocatoria(Long idConvocatoria);

    List<PonenciaResponse> listarMisPonencias(Long idUsuario);

    PonenciaResponse reenviar(Long idPonencia, PonenciaRequest request, Long idUsuario)
            throws RecursoNoEncontradoException;
    
    List<PonenciaResponse> listarAprobadasPorCongreso(Long idCongreso);

}