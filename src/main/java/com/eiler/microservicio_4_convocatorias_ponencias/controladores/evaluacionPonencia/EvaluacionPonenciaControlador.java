/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.eiler.microservicio_4_convocatorias_ponencias.controladores.evaluacionPonencia;

/**
 *
 * @author eiler
 */



import com.eiler.microservicio_4_convocatorias_ponencias.dtos.evaluacionPonencia.EvaluacionPonenciaRequest;
import com.eiler.microservicio_4_convocatorias_ponencias.dtos.evaluacionPonencia.EvaluacionPonenciaResponse;
import com.eiler.microservicio_4_convocatorias_ponencias.excepciones.RecursoNoEncontradoException;
import com.eiler.microservicio_4_convocatorias_ponencias.servicios.evaluacionPonencia.EvaluacionPonenciaServicio;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/evaluaciones")
public class EvaluacionPonenciaControlador {

    private final EvaluacionPonenciaServicio servicio;

    public EvaluacionPonenciaControlador(EvaluacionPonenciaServicio servicio) {
        this.servicio = servicio;
    }

    // POST — evalúa una ponencia (aprueba o rechaza)
    // X-User-Id es el evaluador
    // TODO: validar que X-User-Id sea miembro del comité científico (pendiente ms-congresos)
    @PostMapping
    @PreAuthorize("hasRole('ADMIN_CONGRESO') or hasRole('ADMIN_SISTEMA')")
    public ResponseEntity<EvaluacionPonenciaResponse> evaluar(
            @Valid @RequestBody EvaluacionPonenciaRequest request,
            @RequestHeader("X-User-Id") Long idEvaluador)
            throws RecursoNoEncontradoException {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(servicio.evaluar(request, idEvaluador));
    }

    // GET — obtener evaluación por id
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN_CONGRESO') or hasRole('ADMIN_SISTEMA')")
    public ResponseEntity<EvaluacionPonenciaResponse> obtenerPorId(
            @PathVariable Long id) throws RecursoNoEncontradoException {
        return ResponseEntity.ok(servicio.obtenerPorId(id));
    }

    // GET — listar evaluaciones de una ponencia
    @GetMapping("/ponencia/{idPonencia}")
    @PreAuthorize("hasRole('ADMIN_CONGRESO') or hasRole('ADMIN_SISTEMA')")
    public ResponseEntity<List<EvaluacionPonenciaResponse>> listarPorPonencia(
            @PathVariable Long idPonencia) {
        return ResponseEntity.ok(servicio.listarPorPonencia(idPonencia));
    }

    // GET — listar evaluaciones hechas por el evaluador logueado
    @GetMapping("/mis-evaluaciones")
    @PreAuthorize("hasRole('ADMIN_CONGRESO') or hasRole('ADMIN_SISTEMA')")
    public ResponseEntity<List<EvaluacionPonenciaResponse>> listarMisEvaluaciones(
            @RequestHeader("X-User-Id") Long idEvaluador) {
        return ResponseEntity.ok(servicio.listarPorEvaluador(idEvaluador));
    }
}