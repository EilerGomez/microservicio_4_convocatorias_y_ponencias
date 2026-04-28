/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.eiler.microservicio_4_convocatorias_ponencias.controladores.convocatorias;

/**
 *
 * @author eiler
 */

import com.eiler.microservicio_4_convocatorias_ponencias.dtos.convocatoria.ConvocatoriaRequest;
import com.eiler.microservicio_4_convocatorias_ponencias.dtos.convocatoria.ConvocatoriaResponse;
import com.eiler.microservicio_4_convocatorias_ponencias.excepciones.RecursoNoEncontradoException;
import com.eiler.microservicio_4_convocatorias_ponencias.servicios.convocatoria.ConvocatoriaServicio;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/convocatorias")
public class ConvocatoriaControlador {

    private final ConvocatoriaServicio servicio;

    public ConvocatoriaControlador(ConvocatoriaServicio servicio) {
        this.servicio = servicio;
    }

    // POST /api/v1/convocatorias
    @PostMapping
    public ResponseEntity<ConvocatoriaResponse> crear(
            @Valid @RequestBody ConvocatoriaRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(servicio.crear(request));
    }

    // GET /api/v1/convocatorias/{id}
    @GetMapping("/{id}")
    public ResponseEntity<ConvocatoriaResponse> obtenerPorId(
            @PathVariable Long id) throws RecursoNoEncontradoException {
        return ResponseEntity.ok(servicio.obtenerPorId(id));
    }

    // GET /api/v1/convocatorias/congreso/{idCongreso}
    @GetMapping("/congreso/{idCongreso}")
    public ResponseEntity<List<ConvocatoriaResponse>> listarPorCongreso(
            @PathVariable Long idCongreso) {
        return ResponseEntity.ok(servicio.listarPorCongreso(idCongreso));
    }

    // PUT /api/v1/convocatorias/{id}
    @PutMapping("/{id}")
    public ResponseEntity<ConvocatoriaResponse> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody ConvocatoriaRequest request)
            throws RecursoNoEncontradoException {
        return ResponseEntity.ok(servicio.actualizar(id, request));
    }

    // PATCH /api/v1/convocatorias/{id}/cerrar
    @PatchMapping("/{id}/cerrar")
    public ResponseEntity<Void> cerrar(
            @PathVariable Long id) throws RecursoNoEncontradoException {
        servicio.cerrar(id);
        return ResponseEntity.noContent().build();
    }

    // DELETE /api/v1/convocatorias/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @PathVariable Long id) throws RecursoNoEncontradoException {
        servicio.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}