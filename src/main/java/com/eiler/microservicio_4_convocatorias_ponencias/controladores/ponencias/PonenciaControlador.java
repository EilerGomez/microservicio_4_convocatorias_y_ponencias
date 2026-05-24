package com.eiler.microservicio_4_convocatorias_ponencias.controladores.ponencias;

import com.eiler.microservicio_4_convocatorias_ponencias.dtos.ponencias.PonenciaRequest;
import com.eiler.microservicio_4_convocatorias_ponencias.dtos.ponencias.PonenciaResponse;
import com.eiler.microservicio_4_convocatorias_ponencias.excepciones.RecursoNoEncontradoException;
import com.eiler.microservicio_4_convocatorias_ponencias.servicios.ponencias.PonenciaServicio;
import com.eiler.microservicio_4_convocatorias_ponencias.storage.FileStorageService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/ponencias")
public class PonenciaControlador {

    private final PonenciaServicio servicio;
    private final FileStorageService fileStorageService;

    public PonenciaControlador(PonenciaServicio servicio,
                                FileStorageService fileStorageService) {
        this.servicio = servicio;
        this.fileStorageService = fileStorageService;
    }

    private Long resolverIdUsuario() {
        String principal = (String) SecurityContextHolder
                .getContext().getAuthentication().getPrincipal();
        return Long.parseLong(principal);
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<PonenciaResponse> enviar(
            @Valid @ModelAttribute PonenciaRequest request,
            @RequestPart(value = "archivo", required = false) MultipartFile archivo)
            throws RecursoNoEncontradoException {

        // Subir el archivo y obtener su URL pública
        String urlArchivo = null;
        if (archivo != null && !archivo.isEmpty()) {
            urlArchivo = fileStorageService.store(archivo);
        }

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(servicio.enviar(request, resolverIdUsuario(), urlArchivo));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PonenciaResponse> obtenerPorId(
            @PathVariable Long id) throws RecursoNoEncontradoException {
        return ResponseEntity.ok(servicio.obtenerPorId(id));
    }

    @GetMapping("/convocatoria/{idConvocatoria}")
    @PreAuthorize("hasRole('ADMIN_CONGRESO') or hasRole('ADMIN_SISTEMA')")
    public ResponseEntity<List<PonenciaResponse>> listarPorConvocatoria(
            @PathVariable Long idConvocatoria) {
        return ResponseEntity.ok(servicio.listarPorConvocatoria(idConvocatoria));
    }

    @GetMapping("/mis-ponencias")
    public ResponseEntity<List<PonenciaResponse>> listarMisPonencias() {
        return ResponseEntity.ok(servicio.listarMisPonencias(resolverIdUsuario()));
    }

    @PutMapping(value = "/{id}/reenviar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<PonenciaResponse> reenviar(
            @PathVariable Long id,
            @Valid @ModelAttribute PonenciaRequest request,
            @RequestPart(value = "archivo", required = false) MultipartFile archivo)
            throws RecursoNoEncontradoException {

        String urlArchivo = null;
        if (archivo != null && !archivo.isEmpty()) {
            urlArchivo = fileStorageService.store(archivo);
        }

        return ResponseEntity.ok(servicio.reenviar(id, request, resolverIdUsuario(), urlArchivo));
    }

    @GetMapping("/congreso/{idCongreso}/aprobadas")
    @PreAuthorize("hasRole('ADMIN_CONGRESO') or hasRole('ADMIN_SISTEMA')")
    public ResponseEntity<List<PonenciaResponse>> listarAprobadasPorCongreso(
            @PathVariable Long idCongreso) {
        return ResponseEntity.ok(servicio.listarAprobadasPorCongreso(idCongreso));
    }
}