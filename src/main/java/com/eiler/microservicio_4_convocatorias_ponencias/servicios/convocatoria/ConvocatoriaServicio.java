/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.eiler.microservicio_4_convocatorias_ponencias.servicios.convocatoria;

/**
 *
 * @author eiler
 */

import com.eiler.microservicio_4_convocatorias_ponencias.dtos.convocatoria.ConvocatoriaRequest;
import com.eiler.microservicio_4_convocatorias_ponencias.dtos.convocatoria.ConvocatoriaResponse;
import com.eiler.microservicio_4_convocatorias_ponencias.excepciones.RecursoNoEncontradoException;
import java.util.List;

public interface ConvocatoriaServicio {

    ConvocatoriaResponse crear(ConvocatoriaRequest request);

    ConvocatoriaResponse obtenerPorId(Long id) throws RecursoNoEncontradoException;

    List<ConvocatoriaResponse> listarPorCongreso(Long idCongreso);

    ConvocatoriaResponse actualizar(Long id, ConvocatoriaRequest request)
            throws RecursoNoEncontradoException;

    void cerrar(Long id) throws RecursoNoEncontradoException;

    void eliminar(Long id) throws RecursoNoEncontradoException;
}
