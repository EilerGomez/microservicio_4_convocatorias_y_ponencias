/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.eiler.microservicio_4_convocatorias_ponencias.controladores.ponencias;

/**
 *
 * @author eiler
 */

import com.eiler.microservicio_4_convocatorias_ponencias.dtos.ponencias.PonenciaRequest;
import com.eiler.microservicio_4_convocatorias_ponencias.dtos.ponencias.PonenciaResponse;
import com.eiler.microservicio_4_convocatorias_ponencias.excepciones.RecursoNoEncontradoException;
import com.eiler.microservicio_4_convocatorias_ponencias.servicios.ponencias.PonenciaServicio;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/ponencias")
public class PonenciaControlador {

    private final PonenciaServicio servicio;

    public PonenciaControlador(PonenciaServicio servicio) {
        this.servicio = servicio;
    }

    // cualquier usuario autenticado envía su ponencia
    @PostMapping
    public ResponseEntity<PonenciaResponse> enviar(
            @Valid @RequestBody PonenciaRequest request,
            @RequestHeader("X-User-Id") Long idUsuario)
            throws RecursoNoEncontradoException {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(servicio.enviar(request, idUsuario));
    }

    //cualquier autenticado puede ver una ponencia
    @GetMapping("/{id}")
    public ResponseEntity<PonenciaResponse> obtenerPorId(
            @PathVariable Long id) throws RecursoNoEncontradoException {
        return ResponseEntity.ok(servicio.obtenerPorId(id));
    }

    //admin congreso o sistema lista por convocatoria
    @GetMapping("/convocatoria/{idConvocatoria}")
    @PreAuthorize("hasRole('ADMIN_CONGRESO') or hasRole('ADMIN_SISTEMA')")
    public ResponseEntity<List<PonenciaResponse>> listarPorConvocatoria(
            @PathVariable Long idConvocatoria) {
        return ResponseEntity.ok(servicio.listarPorConvocatoria(idConvocatoria));
    }

    //cada usuario ve sus propias ponencias
    @GetMapping("/mis-ponencias")
    public ResponseEntity<List<PonenciaResponse>> listarMisPonencias(
            @RequestHeader("X-User-Id") Long idUsuario) {
        return ResponseEntity.ok(servicio.listarMisPonencias(idUsuario));
    }

    //reenviar ponencia rechazada
    @PutMapping("/{id}/reenviar")
    public ResponseEntity<PonenciaResponse> reenviar(
            @PathVariable Long id,
            @Valid @RequestBody PonenciaRequest request,
            @RequestHeader("X-User-Id") Long idUsuario)
            throws RecursoNoEncontradoException {
        return ResponseEntity.ok(servicio.reenviar(id, request, idUsuario));
    }

    // PATCH — aprobar ponencia
    // X-User-Id es el evaluador
    // TODO: cuando se integre ms-congresos, validar que X-User-Id sea comité científico
    @PatchMapping("/{id}/aprobar")
    @PreAuthorize("hasRole('ADMIN_CONGRESO') or hasRole('ADMIN_SISTEMA')")
    public ResponseEntity<PonenciaResponse> aprobar(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") Long idEvaluador)
            throws RecursoNoEncontradoException {
        return ResponseEntity.ok(servicio.aprobar(id, idEvaluador));
    }

    // PATCH — rechazar ponencia con comentarios
    // X-User-Id es el evaluador
    // TODO: cuando se integre ms-congresos, validar que X-User-Id sea comité científico
    @PatchMapping("/{id}/rechazar")
    @PreAuthorize("hasRole('ADMIN_CONGRESO') or hasRole('ADMIN_SISTEMA')")
    public ResponseEntity<PonenciaResponse> rechazar(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") Long idEvaluador,
            @RequestParam String comentarios)
            throws RecursoNoEncontradoException {
        return ResponseEntity.ok(servicio.rechazar(id, idEvaluador, comentarios));
    }
}
