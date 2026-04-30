/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.eiler.microservicio_4_convocatorias_ponencias.servicios.evaluacionPonencia;

/**
 *
 * @author eiler
 */

import com.eiler.microservicio_4_convocatorias_ponencias.dtos.evaluacionPonencia.EvaluacionPonenciaRequest;
import com.eiler.microservicio_4_convocatorias_ponencias.dtos.evaluacionPonencia.EvaluacionPonenciaResponse;
import com.eiler.microservicio_4_convocatorias_ponencias.excepciones.RecursoNoEncontradoException;
import com.eiler.microservicio_4_convocatorias_ponencias.modelos.convocatoria.Convocatoria;
import com.eiler.microservicio_4_convocatorias_ponencias.modelos.estadoPonencia.EstadoPonencia;
import com.eiler.microservicio_4_convocatorias_ponencias.modelos.evaluacionPonencia.EvaluacionPonencia;
import com.eiler.microservicio_4_convocatorias_ponencias.modelos.ponencias.Ponencia;
import com.eiler.microservicio_4_convocatorias_ponencias.repositorios.estadoponencia.EstadoPonenciaRepositorio;
import com.eiler.microservicio_4_convocatorias_ponencias.repositorios.evaluacionPonencia.EvaluacionPonenciaRepositorio;
import com.eiler.microservicio_4_convocatorias_ponencias.repositorios.ponencias.PonenciaRepositorio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EvaluacionPonenciaServicioImplTest {

    @Mock private EvaluacionPonenciaRepositorio evaluacionRepositorio;
    @Mock private PonenciaRepositorio           ponenciaRepositorio;
    @Mock private EstadoPonenciaRepositorio     estadoRepositorio;

    @InjectMocks
    private EvaluacionPonenciaServicioImpl servicio;

    private EstadoPonencia pendiente;
    private EstadoPonencia aprobado;
    private EstadoPonencia rechazado;
    private Ponencia       ponenciaPendiente;
    private Ponencia       ponenciaAprobada;
    private EvaluacionPonencia evaluacionAprobada;
    private EvaluacionPonencia evaluacionRechazada;
    private EvaluacionPonenciaRequest requestAprobar;
    private EvaluacionPonenciaRequest requestRechazar;

    @BeforeEach
    void setUp() {
        pendiente = EstadoPonencia.builder().idEstado(1L).nombreEstado("PENDIENTE").build();
        aprobado  = EstadoPonencia.builder().idEstado(2L).nombreEstado("APROBADO").build();
        rechazado = EstadoPonencia.builder().idEstado(3L).nombreEstado("RECHAZADO").build();

        Convocatoria conv = Convocatoria.builder()
                .idConvocatoria(1L).idCongreso(10L)
                .nombreConvocatoria("Conv 2026").estaAbierta(true).build();

        ponenciaPendiente = Ponencia.builder()
                .idPonencia(1L).convocatoria(conv)
                .idUsuario(42L).estado(pendiente)
                .tituloPonencia("Mi ponencia").resumen("Resumen").build();

        ponenciaAprobada = Ponencia.builder()
                .idPonencia(2L).convocatoria(conv)
                .idUsuario(42L).estado(aprobado)
                .tituloPonencia("Ya aprobada").resumen("Resumen").build();

        requestAprobar = EvaluacionPonenciaRequest.builder()
                .idPonencia(1L).estaAprobado(true)
                .comentarios("Excelente propuesta").build();

        requestRechazar = EvaluacionPonenciaRequest.builder()
                .idPonencia(1L).estaAprobado(false)
                .comentarios("No cumple los requisitos mínimos").build();

        evaluacionAprobada = EvaluacionPonencia.builder()
                .idEvaluacion(1L).ponencia(ponenciaPendiente)
                .idEvaluador(10L).estaAprobado(true)
                .comentarios("Excelente propuesta").build();

        evaluacionRechazada = EvaluacionPonencia.builder()
                .idEvaluacion(2L).ponencia(ponenciaPendiente)
                .idEvaluador(10L).estaAprobado(false)
                .comentarios("No cumple los requisitos").build();
    }


    @Test
    void evaluarAprobarCreaEvaluacionYCambiaEstado() throws RecursoNoEncontradoException {
        when(ponenciaRepositorio.findById(1L)).thenReturn(Optional.of(ponenciaPendiente));
        when(estadoRepositorio.findById(2L)).thenReturn(Optional.of(aprobado));
        when(ponenciaRepositorio.save(any())).thenReturn(ponenciaPendiente);
        when(evaluacionRepositorio.save(any())).thenReturn(evaluacionAprobada);

        EvaluacionPonenciaResponse r = servicio.evaluar(requestAprobar, 10L);

        assertNotNull(r);
        assertTrue(r.getEstaAprobado());
        assertEquals("APROBADO", r.getEstadoPonencia().toString());
        verify(ponenciaRepositorio).save(any());
        verify(evaluacionRepositorio).save(any());
    }

    @Test
    void evaluarAprobarSinComentariosEsValido() throws RecursoNoEncontradoException {
        requestAprobar.setComentarios(null);
        when(ponenciaRepositorio.findById(1L)).thenReturn(Optional.of(ponenciaPendiente));
        when(estadoRepositorio.findById(2L)).thenReturn(Optional.of(aprobado));
        when(ponenciaRepositorio.save(any())).thenReturn(ponenciaPendiente);
        when(evaluacionRepositorio.save(any())).thenReturn(evaluacionAprobada);

        assertNotNull(servicio.evaluar(requestAprobar, 10L));
    }

    @Test
    void evaluarRechazarConComentariosCreaEvaluacion() throws RecursoNoEncontradoException {
        when(ponenciaRepositorio.findById(1L)).thenReturn(Optional.of(ponenciaPendiente));
        when(estadoRepositorio.findById(3L)).thenReturn(Optional.of(rechazado));
        when(ponenciaRepositorio.save(any())).thenReturn(ponenciaPendiente);
        when(evaluacionRepositorio.save(any())).thenReturn(evaluacionRechazada);

        EvaluacionPonenciaResponse r = servicio.evaluar(requestRechazar, 10L);

        assertNotNull(r);
        assertFalse(r.getEstaAprobado());
        assertEquals("RECHAZADO", r.getEstadoPonencia().toString());
        verify(ponenciaRepositorio).save(any());
        verify(evaluacionRepositorio).save(any());
    }

    @Test
    void evaluarRechazarSinComentariosLanzaIllegalArgument() {
        requestRechazar.setComentarios(null);
        when(ponenciaRepositorio.findById(1L)).thenReturn(Optional.of(ponenciaPendiente));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> servicio.evaluar(requestRechazar, 10L));
        assertTrue(ex.getMessage().contains("comentarios"));
        verify(evaluacionRepositorio, never()).save(any());
    }

    @Test
    void evaluarRechazarComentariosVaciosLanzaIllegalArgument() {
        requestRechazar.setComentarios("   ");
        when(ponenciaRepositorio.findById(1L)).thenReturn(Optional.of(ponenciaPendiente));

        assertThrows(IllegalArgumentException.class,
                () -> servicio.evaluar(requestRechazar, 10L));
    }


    @Test
    void evaluarPonenciaNoExisteLanzaExcepcion() {
        when(ponenciaRepositorio.findById(99L)).thenReturn(Optional.empty());
        requestAprobar.setIdPonencia(99L);

        assertThrows(RecursoNoEncontradoException.class,
                () -> servicio.evaluar(requestAprobar, 10L));
        verify(evaluacionRepositorio, never()).save(any());
    }

    @Test
    void evaluarPonenciaNoEsPendienteLanzaIllegalState() {
        when(ponenciaRepositorio.findById(2L)).thenReturn(Optional.of(ponenciaAprobada));
        requestAprobar.setIdPonencia(2L);

        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> servicio.evaluar(requestAprobar, 10L));
        assertTrue(ex.getMessage().contains("PENDIENTE"));
        verify(evaluacionRepositorio, never()).save(any());
    }

    // TODO: cuando se integre ms-congresos agregar:
    // @Test
    // void evaluar_evaluadorNoEsComite_lanzaIllegalState() {
    //     // mockear msCongresoClient.esComiteCientifico() retornando false
    //     // assertThrows(IllegalStateException.class, () -> servicio.evaluar(...))
    //     // assertTrue(ex.getMessage().contains("comité científico"))
    // }


    @Test
    void obtenerPorIdExisteRetorna() throws RecursoNoEncontradoException {
        when(evaluacionRepositorio.findById(1L)).thenReturn(Optional.of(evaluacionAprobada));
        assertEquals(1L, servicio.obtenerPorId(1L).getIdEvaluacion());
    }

    @Test
    void obtenerPorIdNoExisteLanzaExcepcion() {
        when(evaluacionRepositorio.findById(99L)).thenReturn(Optional.empty());
        assertThrows(RecursoNoEncontradoException.class,
                () -> servicio.obtenerPorId(99L));
    }


    @Test
    void listarPorPonenciaRetornaLista() {
        when(evaluacionRepositorio.findByPonencia_IdPonencia(1L))
                .thenReturn(List.of(evaluacionAprobada));
        assertEquals(1, servicio.listarPorPonencia(1L).size());
    }

    @Test
    void listarPorPonenciaVaciaRetornaListaVacia() {
        when(evaluacionRepositorio.findByPonencia_IdPonencia(99L)).thenReturn(List.of());
        assertTrue(servicio.listarPorPonencia(99L).isEmpty());
    }

    @Test
    void listarPorEvaluadorRetornaLista() {
        when(evaluacionRepositorio.findByIdEvaluador(10L))
                .thenReturn(List.of(evaluacionAprobada, evaluacionRechazada));
        assertEquals(2, servicio.listarPorEvaluador(10L).size());
    }
}