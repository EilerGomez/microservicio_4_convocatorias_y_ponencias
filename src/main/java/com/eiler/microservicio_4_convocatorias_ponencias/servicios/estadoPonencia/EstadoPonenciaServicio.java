/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.eiler.microservicio_4_convocatorias_ponencias.servicios.estadoPonencia;

/**
 *
 * @author eiler
 */

import com.eiler.microservicio_4_convocatorias_ponencias.dtos.estadoPonencia.EstadoPonenciaResponse;
import com.eiler.microservicio_4_convocatorias_ponencias.excepciones.RecursoNoEncontradoException;
import java.util.List;

public interface EstadoPonenciaServicio {

    EstadoPonenciaResponse obtenerPorId(Long id) throws RecursoNoEncontradoException;

    List<EstadoPonenciaResponse> listarTodos();
}