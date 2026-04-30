/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.eiler.microservicio_4_convocatorias_ponencias.servicios.evaluacionPonencia;

/**
 *
 * @author eiler
 */

import com.eiler.microservicio_4_convocatorias_ponencias.dtos.evaluacionPonencia.EvaluacionPonenciaRequest;
import com.eiler.microservicio_4_convocatorias_ponencias.dtos.evaluacionPonencia.EvaluacionPonenciaResponse;
import com.eiler.microservicio_4_convocatorias_ponencias.excepciones.RecursoNoEncontradoException;
import java.util.List;

public interface EvaluacionPonenciaServicio {

    EvaluacionPonenciaResponse evaluar(EvaluacionPonenciaRequest request, Long idEvaluador)
            throws RecursoNoEncontradoException;

    EvaluacionPonenciaResponse obtenerPorId(Long id)
            throws RecursoNoEncontradoException;

    List<EvaluacionPonenciaResponse> listarPorPonencia(Long idPonencia);

    List<EvaluacionPonenciaResponse> listarPorEvaluador(Long idEvaluador);
}