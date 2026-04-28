/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.eiler.microservicio_4_convocatorias_ponencias.controladores.estadoPonencia;

/**
 *
 * @author eiler
 */

import com.eiler.microservicio_4_convocatorias_ponencias.dtos.estadoPonencia.EstadoPonenciaResponse;
import com.eiler.microservicio_4_convocatorias_ponencias.excepciones.RecursoNoEncontradoException;
import com.eiler.microservicio_4_convocatorias_ponencias.servicios.estadoPonencia.EstadoPonenciaServicio;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/estados-ponencia")
public class EstadoPonenciaControlador {

    private final EstadoPonenciaServicio servicio;

    public EstadoPonenciaControlador(EstadoPonenciaServicio servicio) {
        this.servicio = servicio;
    }

    @GetMapping
    public ResponseEntity<List<EstadoPonenciaResponse>> listarTodos() {
        return ResponseEntity.ok(servicio.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EstadoPonenciaResponse> obtenerPorId(
            @PathVariable Long id) throws RecursoNoEncontradoException {
        return ResponseEntity.ok(servicio.obtenerPorId(id));
    }
}