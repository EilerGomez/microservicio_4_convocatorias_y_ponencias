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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PonenciaControladorTest {

    @Mock  private PonenciaServicio servicio;
    @InjectMocks private PonenciaControlador controlador;

    private PonenciaRequest  req;
    private PonenciaResponse resPendiente;
    private PonenciaResponse resAprobado;

    @BeforeEach
    void setUp() {
        req = PonenciaRequest.builder()
                .idConvocatoria(1L).idTipoActividad(1)
                .tituloPonencia("Mi ponencia").resumen("Resumen").build();

        resPendiente = PonenciaResponse.builder()
                .idPonencia(1L).idConvocatoria(1L).idUsuario(42L)
                .nombreEstado("PENDIENTE").tituloPonencia("Mi ponencia").build();

        resAprobado = PonenciaResponse.builder()
                .idPonencia(1L).idConvocatoria(1L).idUsuario(42L)
                .nombreEstado("APROBADO").tituloPonencia("Mi ponencia").build();
    }


    @Test
    void enviarRetorna201() throws RecursoNoEncontradoException {
        when(servicio.enviar(any(), eq(42L))).thenReturn(resPendiente);
        ResponseEntity<PonenciaResponse> r = controlador.enviar(req, 42L);
        assertEquals(HttpStatus.CREATED, r.getStatusCode());
        assertEquals("PENDIENTE", r.getBody().getNombreEstado());
    }

    @Test
    void enviarConvCerradaPropagaExcepcion() throws RecursoNoEncontradoException {
        when(servicio.enviar(any(), eq(42L)))
                .thenThrow(new IllegalStateException("cerrada"));
        assertThrows(IllegalStateException.class, () -> controlador.enviar(req, 42L));
    }


    @Test
    void obtenerPorIdExiste_retorna200() throws RecursoNoEncontradoException {
        when(servicio.obtenerPorId(1L)).thenReturn(resPendiente);
        ResponseEntity<PonenciaResponse> r = controlador.obtenerPorId(1L);
        assertEquals(HttpStatus.OK, r.getStatusCode());
        assertEquals(1L, r.getBody().getIdPonencia());
    }

    @Test
    void obtenerPorIdNoExisteLanzaExcepcion() throws RecursoNoEncontradoException {
        when(servicio.obtenerPorId(99L))
                .thenThrow(new RecursoNoEncontradoException("99"));
        assertThrows(RecursoNoEncontradoException.class,
                () -> controlador.obtenerPorId(99L));
    }


    @Test
    void listarPorConvocatoriaRetorna200() {
        when(servicio.listarPorConvocatoria(1L)).thenReturn(List.of(resPendiente));
        ResponseEntity<List<PonenciaResponse>> r = controlador.listarPorConvocatoria(1L);
        assertEquals(HttpStatus.OK, r.getStatusCode());
        assertEquals(1, r.getBody().size());
    }

    @Test
    void listarPorConvocatoriaVaciaRetornaListaVacia() {
        when(servicio.listarPorConvocatoria(99L)).thenReturn(List.of());
        assertTrue(controlador.listarPorConvocatoria(99L).getBody().isEmpty());
    }

    @Test
    void listarMisPonenciasRetorna200() {
        when(servicio.listarMisPonencias(42L)).thenReturn(List.of(resPendiente));
        ResponseEntity<List<PonenciaResponse>> r = controlador.listarMisPonencias(42L);
        assertEquals(HttpStatus.OK, r.getStatusCode());
        assertEquals(42L, r.getBody().get(0).getIdUsuario());
    }


    @Test
    void reenviarExitosoRetorna200() throws RecursoNoEncontradoException {
        when(servicio.reenviar(eq(2L), any(), eq(42L))).thenReturn(resPendiente);
        ResponseEntity<PonenciaResponse> r = controlador.reenviar(2L, req, 42L);
        assertEquals(HttpStatus.OK, r.getStatusCode());
    }

    @Test
    void reenviarNoRechazadaPropagaExcepcion() throws RecursoNoEncontradoException {
        when(servicio.reenviar(eq(1L), any(), eq(42L)))
                .thenThrow(new IllegalStateException("rechazada"));
        assertThrows(IllegalStateException.class,
                () -> controlador.reenviar(1L, req, 42L));
    }


    @Test
    void aprobarExitosoRetorna200() throws RecursoNoEncontradoException {
        when(servicio.aprobar(1L, 10L)).thenReturn(resAprobado);
        ResponseEntity<PonenciaResponse> r = controlador.aprobar(1L, 10L);
        assertEquals(HttpStatus.OK, r.getStatusCode());
        assertEquals("APROBADO", r.getBody().getNombreEstado());
    }

    @Test
    void aprobarNoPendientePropagaExcepcion() throws RecursoNoEncontradoException {
        when(servicio.aprobar(1L, 10L))
                .thenThrow(new IllegalStateException("PENDIENTE"));
        assertThrows(IllegalStateException.class,
                () -> controlador.aprobar(1L, 10L));
    }

    @Test
    void aprobarNoExisteLanzaExcepcion() throws RecursoNoEncontradoException {
        when(servicio.aprobar(99L, 10L))
                .thenThrow(new RecursoNoEncontradoException("99"));
        assertThrows(RecursoNoEncontradoException.class,
                () -> controlador.aprobar(99L, 10L));
    }

    // TODO: cuando se integre ms-congresos agregar:
    // @Test
    // void aprobar_evaluadorNoEsComite_retorna403() { ... }
    // @Test
    // void rechazar_evaluadorNoEsComite_retorna403() { ... }


    @Test
    void rechazarExitosoRetorna200() throws RecursoNoEncontradoException {
        PonenciaResponse resRechazado = PonenciaResponse.builder()
                .idPonencia(1L).nombreEstado("RECHAZADO").build();
        when(servicio.rechazar(1L, 10L, "No cumple requisitos"))
                .thenReturn(resRechazado);

        ResponseEntity<PonenciaResponse> r =
                controlador.rechazar(1L, 10L, "No cumple requisitos");

        assertEquals(HttpStatus.OK, r.getStatusCode());
        assertEquals("RECHAZADO", r.getBody().getNombreEstado());
    }

    @Test
    void rechazarSinComentariosPropagaExcepcion() throws RecursoNoEncontradoException {
        when(servicio.rechazar(1L, 10L, ""))
                .thenThrow(new IllegalArgumentException("comentarios"));
        assertThrows(IllegalArgumentException.class,
                () -> controlador.rechazar(1L, 10L, ""));
    }

    @Test
    void rechazarNoPendientePropagaExcepcion() throws RecursoNoEncontradoException {
        when(servicio.rechazar(2L, 10L, "Motivo"))
                .thenThrow(new IllegalStateException("PENDIENTE"));
        assertThrows(IllegalStateException.class,
                () -> controlador.rechazar(2L, 10L, "Motivo"));
    }

    @Test
    void rechazarNoExisteLanzaExcepcion() throws RecursoNoEncontradoException {
        when(servicio.rechazar(99L, 10L, "Motivo"))
                .thenThrow(new RecursoNoEncontradoException("99"));
        assertThrows(RecursoNoEncontradoException.class,
                () -> controlador.rechazar(99L, 10L, "Motivo"));
    }
}
