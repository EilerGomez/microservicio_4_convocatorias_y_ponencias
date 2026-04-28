/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.eiler.microservicio_4_convocatorias_ponencias.servicios.convocatoria;

/**
 *
 * @author eiler
 */

import com.eiler.microservicio_4_convocatorias_ponencias.dtos.convocatoria.ConvocatoriaRequest;
import com.eiler.microservicio_4_convocatorias_ponencias.dtos.convocatoria.ConvocatoriaResponse;
import com.eiler.microservicio_4_convocatorias_ponencias.excepciones.RecursoNoEncontradoException;
import com.eiler.microservicio_4_convocatorias_ponencias.modelos.convocatoria.Convocatoria;
import com.eiler.microservicio_4_convocatorias_ponencias.repositorios.convocatoria.ConvocatoriaRepositorio;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ConvocatoriaServicioImpl implements ConvocatoriaServicio {

    private final ConvocatoriaRepositorio repositorio;

    public ConvocatoriaServicioImpl(ConvocatoriaRepositorio repositorio) {
        this.repositorio = repositorio;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ConvocatoriaResponse crear(ConvocatoriaRequest request) {

        validarFechas(request.getFechaApertura(), request.getFechaCierre());

        Convocatoria convocatoria = Convocatoria.builder()
                .idCongreso(request.getIdCongreso())
                .nombreConvocatoria(request.getNombreConvocatoria())
                .descripcion(request.getDescripcion())
                .fechaApertura(request.getFechaApertura())
                .fechaCierre(request.getFechaCierre())
                .estaAbierta(true)   // siempre abre en true
                .build();

        return new ConvocatoriaResponse(repositorio.save(convocatoria));
    }

    @Override
    @Transactional(readOnly = true)
    public ConvocatoriaResponse obtenerPorId(Long id) throws RecursoNoEncontradoException {
        Convocatoria convocatoria = repositorio.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Convocatoria con ID " + id + " no encontrada"));
        return new ConvocatoriaResponse(convocatoria);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ConvocatoriaResponse> listarPorCongreso(Long idCongreso) {
        return repositorio.findByIdCongreso(idCongreso)
                .stream()
                .map(ConvocatoriaResponse::new)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ConvocatoriaResponse actualizar(Long id, ConvocatoriaRequest request)
            throws RecursoNoEncontradoException {

        Convocatoria convocatoria = repositorio.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Convocatoria con ID " + id + " no encontrada"));

        if (!convocatoria.getEstaAbierta()) {
            throw new IllegalStateException(
                    "No se puede modificar una convocatoria cerrada");
        }

        validarFechas(request.getFechaApertura(), request.getFechaCierre());

        convocatoria.setNombreConvocatoria(request.getNombreConvocatoria());
        convocatoria.setDescripcion(request.getDescripcion());
        convocatoria.setFechaApertura(request.getFechaApertura());
        convocatoria.setFechaCierre(request.getFechaCierre());

        return new ConvocatoriaResponse(repositorio.save(convocatoria));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cerrar(Long id) throws RecursoNoEncontradoException {

        Convocatoria convocatoria = repositorio.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Convocatoria con ID " + id + " no encontrada"));

        if (!convocatoria.getEstaAbierta()) {
            throw new IllegalStateException(
                    "La convocatoria con ID " + id + " ya está cerrada");
        }

        convocatoria.setEstaAbierta(false);
        repositorio.save(convocatoria);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void eliminar(Long id) throws RecursoNoEncontradoException {

        Convocatoria convocatoria = repositorio.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Convocatoria con ID " + id + " no encontrada"));

        repositorio.delete(convocatoria);
    }

    private void validarFechas(
            java.time.LocalDateTime apertura,
            java.time.LocalDateTime cierre) {

        if (apertura == null || cierre == null) {
            throw new IllegalArgumentException(
                    "Las fechas de apertura y cierre son obligatorias");
        }
        if (!cierre.isAfter(apertura)) {
            throw new IllegalArgumentException(
                    "La fecha de cierre debe ser posterior a la fecha de apertura");
        }
    }
}
