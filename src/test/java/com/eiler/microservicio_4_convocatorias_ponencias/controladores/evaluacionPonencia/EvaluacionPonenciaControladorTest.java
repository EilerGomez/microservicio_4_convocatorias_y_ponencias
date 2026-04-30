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
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EvaluacionPonenciaControladorTest {

    @Mock  private EvaluacionPonenciaServicio servicio;
    @InjectMocks private EvaluacionPonenciaControlador controlador;

    private EvaluacionPonenciaRequest  requestAprobar;
    private EvaluacionPonenciaRequest  requestRechazar;
    private EvaluacionPonenciaResponse resAprobada;
    private EvaluacionPonenciaResponse resRechazada;
    private EvaluacionPonenciaResponse.EstadoPonencia estadoPonencia;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        "10", null,
                        List.of(new SimpleGrantedAuthority("ROLE_ADMIN_CONGRESO"))
                )
        );

        requestAprobar = EvaluacionPonenciaRequest.builder()
                .idPonencia(1L).estaAprobado(true)
                .comentarios("Aprobada").build();

        requestRechazar = EvaluacionPonenciaRequest.builder()
                .idPonencia(1L).estaAprobado(false)
                .comentarios("No cumple requisitos").build();

        resAprobada = EvaluacionPonenciaResponse.builder()
                .idEvaluacion(1L).idPonencia(1L)
                .idEvaluador(10L).estaAprobado(true)
                .estadoPonencia(estadoPonencia.APROBADO).build();

        resRechazada = EvaluacionPonenciaResponse.builder()
                .idEvaluacion(2L).idPonencia(1L)
                .idEvaluador(10L).estaAprobado(false)
                .estadoPonencia(estadoPonencia.RECHAZADO).build();
    }

    @AfterEach
    void limpiarContexto() {
        SecurityContextHolder.clearContext();
    }


    @Test
    void evaluarAprobarRetorna201() throws RecursoNoEncontradoException {
        when(servicio.evaluar(any(), eq(10L))).thenReturn(resAprobada);

        ResponseEntity<EvaluacionPonenciaResponse> r =
                controlador.evaluar(requestAprobar);

        assertEquals(HttpStatus.CREATED, r.getStatusCode());
        assertTrue(r.getBody().getEstaAprobado());
        assertEquals(estadoPonencia.APROBADO, r.getBody().getEstadoPonencia());
        verify(servicio).evaluar(any(), eq(10L));
    }

    @Test
    void evaluarRechazarRetorna201() throws RecursoNoEncontradoException {
        when(servicio.evaluar(any(), eq(10L))).thenReturn(resRechazada);

        ResponseEntity<EvaluacionPonenciaResponse> r =
                controlador.evaluar(requestRechazar);

        assertEquals(HttpStatus.CREATED, r.getStatusCode());
        assertFalse(r.getBody().getEstaAprobado());
        assertEquals(estadoPonencia.RECHAZADO, r.getBody().getEstadoPonencia());
    }

    @Test
    void evaluarPonenciaNoExisteLanzaExcepcion() throws RecursoNoEncontradoException {
        when(servicio.evaluar(any(), eq(10L)))
                .thenThrow(new RecursoNoEncontradoException("99"));

        assertThrows(RecursoNoEncontradoException.class,
                () -> controlador.evaluar(requestAprobar));
    }

    @Test
    void evaluarNoPendientePropagaExcepcion() throws RecursoNoEncontradoException {
        when(servicio.evaluar(any(), eq(10L)))
                .thenThrow(new IllegalStateException("PENDIENTE"));

        assertThrows(IllegalStateException.class,
                () -> controlador.evaluar(requestAprobar));
    }

    @Test
    void evaluarRechazarSinComentariosPropagaExcepcion()
            throws RecursoNoEncontradoException {
        when(servicio.evaluar(any(), eq(10L)))
                .thenThrow(new IllegalArgumentException("comentarios"));

        assertThrows(IllegalArgumentException.class,
                () -> controlador.evaluar(requestRechazar));
    }

    // TODO: cuando se integre ms-congresos agregar:
    // @Test
    // void evaluarEvaluadorNoEsComiteRetorna403() { ... }


    @Test
    void obtenerPorIdExisteRetorna200() throws RecursoNoEncontradoException {
        when(servicio.obtenerPorId(1L)).thenReturn(resAprobada);

        ResponseEntity<EvaluacionPonenciaResponse> r = controlador.obtenerPorId(1L);

        assertEquals(HttpStatus.OK, r.getStatusCode());
        assertEquals(1L, r.getBody().getIdEvaluacion());
    }

    @Test
    void obtenerPorIdNoExisteLanzaExcepcion() throws RecursoNoEncontradoException {
        when(servicio.obtenerPorId(99L))
                .thenThrow(new RecursoNoEncontradoException("99"));

        assertThrows(RecursoNoEncontradoException.class,
                () -> controlador.obtenerPorId(99L));
    }


    @Test
    void listarPorPonenciaRetorna200() {
        when(servicio.listarPorPonencia(1L)).thenReturn(List.of(resAprobada));

        ResponseEntity<List<EvaluacionPonenciaResponse>> r =
                controlador.listarPorPonencia(1L);

        assertEquals(HttpStatus.OK, r.getStatusCode());
        assertEquals(1, r.getBody().size());
    }

    @Test
    void listarPorPonenciaVaciaRetornaListaVacia() {
        when(servicio.listarPorPonencia(99L)).thenReturn(List.of());

        assertTrue(controlador.listarPorPonencia(99L).getBody().isEmpty());
    }

    @Test
    void listarMisEvaluacionesRetorna200() {
        when(servicio.listarPorEvaluador(10L))
                .thenReturn(List.of(resAprobada, resRechazada));

        ResponseEntity<List<EvaluacionPonenciaResponse>> r =
                controlador.listarMisEvaluaciones();

        assertEquals(HttpStatus.OK, r.getStatusCode());
        assertEquals(2, r.getBody().size());
    }
}