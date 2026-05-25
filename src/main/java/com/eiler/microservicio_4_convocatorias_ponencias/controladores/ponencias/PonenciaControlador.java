package com.eiler.microservicio_4_convocatorias_ponencias.controladores.ponencias;

import com.eiler.microservicio_4_convocatorias_ponencias.dtos.ponencias.PonenciaRequest;
import com.eiler.microservicio_4_convocatorias_ponencias.dtos.ponencias.PonenciaResponse;
import com.eiler.microservicio_4_convocatorias_ponencias.excepciones.RecursoNoEncontradoException;
import com.eiler.microservicio_4_convocatorias_ponencias.servicios.ponencias.PonenciaServicio;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/ponencias")
public class PonenciaControlador {

    private final PonenciaServicio servicio;

    public PonenciaControlador(PonenciaServicio servicio) {
        this.servicio = servicio;
    }

    private Long resolverIdUsuario() {
        String principal = (String) SecurityContextHolder
                .getContext().getAuthentication().getPrincipal();
        return Long.parseLong(principal);
    }

  
    @PostMapping
    public ResponseEntity<PonenciaResponse> enviar(
            @Valid @RequestBody PonenciaRequest request)
            throws RecursoNoEncontradoException {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(servicio.enviar(request, resolverIdUsuario(), request.urlArchivo()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PonenciaResponse> obtenerPorId(
            @PathVariable Long id) throws RecursoNoEncontradoException {
        return ResponseEntity.ok(servicio.obtenerPorId(id));
    }

    @GetMapping("/convocatoria/{idConvocatoria}")
    public ResponseEntity<List<PonenciaResponse>> listarPorConvocatoria(
            @PathVariable Long idConvocatoria) {
        return ResponseEntity.ok(servicio.listarPorConvocatoria(idConvocatoria));
    }

    @GetMapping("/mis-ponencias")
    public ResponseEntity<List<PonenciaResponse>> listarMisPonencias() {
        return ResponseEntity.ok(servicio.listarMisPonencias(resolverIdUsuario()));
    }

    @PutMapping("/{id}/reenviar")
    public ResponseEntity<PonenciaResponse> reenviar(
            @PathVariable Long id,
            @Valid @RequestBody PonenciaRequest request)
            throws RecursoNoEncontradoException {
        return ResponseEntity.ok(
                servicio.reenviar(id, request, resolverIdUsuario(), request.urlArchivo()));
    }

    @GetMapping("/congreso/{idCongreso}/aprobadas")
    public ResponseEntity<List<PonenciaResponse>> listarAprobadasPorCongreso(
            @PathVariable Long idCongreso) {
        return ResponseEntity.ok(servicio.listarAprobadasPorCongreso(idCongreso));
    }
}