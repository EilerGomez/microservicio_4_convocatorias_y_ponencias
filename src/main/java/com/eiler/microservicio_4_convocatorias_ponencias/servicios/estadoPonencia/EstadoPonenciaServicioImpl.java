/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.eiler.microservicio_4_convocatorias_ponencias.servicios.estadoPonencia;

/**
 *
 * @author eiler
 */

import com.eiler.microservicio_4_convocatorias_ponencias.dtos.estadoPonencia.EstadoPonenciaResponse;
import com.eiler.microservicio_4_convocatorias_ponencias.excepciones.RecursoNoEncontradoException;
import com.eiler.microservicio_4_convocatorias_ponencias.repositorios.estadoponencia.EstadoPonenciaRepositorio;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EstadoPonenciaServicioImpl implements EstadoPonenciaServicio {

    private final EstadoPonenciaRepositorio repositorio;

    public EstadoPonenciaServicioImpl(EstadoPonenciaRepositorio repositorio) {
        this.repositorio = repositorio;
    }

    @Override
    @Transactional(readOnly = true)
    public EstadoPonenciaResponse obtenerPorId(Long id)
            throws RecursoNoEncontradoException {

        return repositorio.findById(id)
                .map(EstadoPonenciaResponse::new)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Estado de ponencia con ID " + id + " no encontrado"));
    }

    @Override
    @Transactional(readOnly = true)
    public List<EstadoPonenciaResponse> listarTodos() {
        return repositorio.findAll()
                .stream()
                .map(EstadoPonenciaResponse::new)
                .collect(Collectors.toList());
    }
}
