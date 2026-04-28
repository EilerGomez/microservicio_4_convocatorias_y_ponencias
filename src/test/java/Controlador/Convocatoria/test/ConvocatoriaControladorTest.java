/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controlador.Convocatoria.test;

/**
 *
 * @author eiler
 */


import com.eiler.microservicio_4_convocatorias_ponencias.controladores.convocatorias.ConvocatoriaControlador;
import com.eiler.microservicio_4_convocatorias_ponencias.dtos.convocatoria.ConvocatoriaRequest;
import com.eiler.microservicio_4_convocatorias_ponencias.dtos.convocatoria.ConvocatoriaResponse;
import com.eiler.microservicio_4_convocatorias_ponencias.excepciones.RecursoNoEncontradoException;
import com.eiler.microservicio_4_convocatorias_ponencias.servicios.convocatoria.ConvocatoriaServicio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConvocatoriaControladorTest {

    @Mock
    private ConvocatoriaServicio servicio;

    @InjectMocks
    private ConvocatoriaControlador controlador;

    private ConvocatoriaResponse responseAbierta;
    private ConvocatoriaResponse responseCerrada;
    private ConvocatoriaRequest  requestValido;

    private final LocalDateTime ahora       = LocalDateTime.now();
    private final LocalDateTime enUnaSemana = ahora.plusDays(7);

    @BeforeEach
    void setUp() {
        responseAbierta = ConvocatoriaResponse.builder()
                .idConvocatoria(1L)
                .idCongreso(10L)
                .nombreConvocatoria("Convocatoria 2026")
                .descripcion("Descripción de prueba")
                .fechaApertura(ahora)
                .fechaCierre(enUnaSemana)
                .estaAbierta(true)
                .build();

        responseCerrada = ConvocatoriaResponse.builder()
                .idConvocatoria(2L)
                .idCongreso(10L)
                .nombreConvocatoria("Convocatoria Cerrada")
                .estaAbierta(false)
                .build();

        requestValido = ConvocatoriaRequest.builder()
                .idCongreso(10L)
                .nombreConvocatoria("Convocatoria 2026")
                .descripcion("Descripción de prueba")
                .fechaApertura(ahora)
                .fechaCierre(enUnaSemana)
                .build();
    }

    //listar por congreso
    @Test
    void listarPorCongreso_retorna200ConLista() {
        when(servicio.listarPorCongreso(10L))
                .thenReturn(List.of(responseAbierta, responseCerrada));

        ResponseEntity<List<ConvocatoriaResponse>> respuesta =
                controlador.listarPorCongreso(10L);

        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        assertNotNull(respuesta.getBody());
        assertEquals(2, respuesta.getBody().size());
        verify(servicio, times(1)).listarPorCongreso(10L);
    }

    @Test
    void listarPorCongreso_sinResultados_retorna200ConListaVacia() {
        when(servicio.listarPorCongreso(99L)).thenReturn(List.of());

        ResponseEntity<List<ConvocatoriaResponse>> respuesta =
                controlador.listarPorCongreso(99L);

        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        assertNotNull(respuesta.getBody());
        assertTrue(respuesta.getBody().isEmpty());
        verify(servicio, times(1)).listarPorCongreso(99L);
    }

    //obtener por id
    @Test
    void obtenerPorId_cuandoExiste_retorna200() throws RecursoNoEncontradoException {
        when(servicio.obtenerPorId(1L)).thenReturn(responseAbierta);

        ResponseEntity<ConvocatoriaResponse> respuesta =
                controlador.obtenerPorId(1L);

        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        assertNotNull(respuesta.getBody());
        assertEquals(1L,                  respuesta.getBody().getIdConvocatoria());
        assertEquals("Convocatoria 2026", respuesta.getBody().getNombreConvocatoria());
        assertTrue(respuesta.getBody().getEstaAbierta());
        verify(servicio, times(1)).obtenerPorId(1L);
    }

    @Test
    void obtenerPorId_cuandoNoExiste_lanzaExcepcion() throws RecursoNoEncontradoException {
        when(servicio.obtenerPorId(99L))
                .thenThrow(new RecursoNoEncontradoException(
                        "Convocatoria con ID 99 no encontrada"));

        RecursoNoEncontradoException ex = assertThrows(
                RecursoNoEncontradoException.class,
                () -> controlador.obtenerPorId(99L)
        );

        assertTrue(ex.getMessage().contains("99"));
        verify(servicio, times(1)).obtenerPorId(99L);
    }

    //crear
    void crear_conRequestValido_retorna201() {
        when(servicio.crear(any(ConvocatoriaRequest.class))).thenReturn(responseAbierta);

        ResponseEntity<ConvocatoriaResponse> respuesta =
                controlador.crear(requestValido);

        assertEquals(HttpStatus.CREATED, respuesta.getStatusCode());
        assertNotNull(respuesta.getBody());
        assertEquals(1L, respuesta.getBody().getIdConvocatoria());
        assertTrue(respuesta.getBody().getEstaAbierta());
        verify(servicio, times(1)).crear(any(ConvocatoriaRequest.class));
    }

    @Test
    void crear_cuandoServicioLanzaExcepcion_propagaExcepcion() {
        when(servicio.crear(any(ConvocatoriaRequest.class)))
                .thenThrow(new IllegalArgumentException(
                        "La fecha de cierre debe ser posterior a la fecha de apertura"));

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> controlador.crear(requestValido)
        );

        assertTrue(ex.getMessage().contains("posterior"));
        verify(servicio, times(1)).crear(any(ConvocatoriaRequest.class));
    }

    // actualizar
    @Test
    void actualizar_cuandoExisteYAbierta_retorna200() throws RecursoNoEncontradoException {
        when(servicio.actualizar(eq(1L), any(ConvocatoriaRequest.class)))
                .thenReturn(responseAbierta);

        ResponseEntity<ConvocatoriaResponse> respuesta =
                controlador.actualizar(1L, requestValido);

        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        assertNotNull(respuesta.getBody());
        assertEquals(1L, respuesta.getBody().getIdConvocatoria());
        verify(servicio, times(1)).actualizar(eq(1L), any(ConvocatoriaRequest.class));
    }

    @Test
    void actualizar_cuandoNoExiste_lanzaExcepcion() throws RecursoNoEncontradoException {
        when(servicio.actualizar(eq(99L), any(ConvocatoriaRequest.class)))
                .thenThrow(new RecursoNoEncontradoException(
                        "Convocatoria con ID 99 no encontrada"));

        RecursoNoEncontradoException ex = assertThrows(
                RecursoNoEncontradoException.class,
                () -> controlador.actualizar(99L, requestValido)
        );

        assertTrue(ex.getMessage().contains("99"));
        verify(servicio, times(1)).actualizar(eq(99L), any(ConvocatoriaRequest.class));
    }

    @Test
    void actualizar_cuandoCerrada_lanzaIllegalState() throws RecursoNoEncontradoException {
        when(servicio.actualizar(eq(2L), any(ConvocatoriaRequest.class)))
                .thenThrow(new IllegalStateException(
                        "No se puede modificar una convocatoria cerrada"));

        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> controlador.actualizar(2L, requestValido)
        );

        assertTrue(ex.getMessage().contains("cerrada"));
        verify(servicio, times(1)).actualizar(eq(2L), any(ConvocatoriaRequest.class));
    }

    // cerrar convocatoria
    @Test
    void cerrar_cuandoAbierta_retorna204() throws RecursoNoEncontradoException {
        doNothing().when(servicio).cerrar(1L);

        ResponseEntity<Void> respuesta = controlador.cerrar(1L);

        assertEquals(HttpStatus.NO_CONTENT, respuesta.getStatusCode());
        assertNull(respuesta.getBody());
        verify(servicio, times(1)).cerrar(1L);
    }

    @Test
    void cerrar_cuandoYaCerrada_lanzaIllegalState() throws RecursoNoEncontradoException {
        doThrow(new IllegalStateException("La convocatoria con ID 2 ya está cerrada"))
                .when(servicio).cerrar(2L);

        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> controlador.cerrar(2L)
        );

        assertTrue(ex.getMessage().contains("ya está cerrada"));
        verify(servicio, times(1)).cerrar(2L);
    }

    @Test
    void cerrar_cuandoNoExiste_lanzaExcepcion() throws RecursoNoEncontradoException {
        doThrow(new RecursoNoEncontradoException("Convocatoria con ID 99 no encontrada"))
                .when(servicio).cerrar(99L);

        RecursoNoEncontradoException ex = assertThrows(
                RecursoNoEncontradoException.class,
                () -> controlador.cerrar(99L)
        );

        assertTrue(ex.getMessage().contains("99"));
        verify(servicio, times(1)).cerrar(99L);
    }

    //eliminar
    @Test
    void eliminar_cuandoExiste_retorna204() throws RecursoNoEncontradoException {
        doNothing().when(servicio).eliminar(1L);

        ResponseEntity<Void> respuesta = controlador.eliminar(1L);

        assertEquals(HttpStatus.NO_CONTENT, respuesta.getStatusCode());
        assertNull(respuesta.getBody());
        verify(servicio, times(1)).eliminar(1L);
    }

    @Test
    void eliminar_cuandoNoExiste_lanzaExcepcion() throws RecursoNoEncontradoException {
        doThrow(new RecursoNoEncontradoException("Convocatoria con ID 99 no encontrada"))
                .when(servicio).eliminar(99L);

        RecursoNoEncontradoException ex = assertThrows(
                RecursoNoEncontradoException.class,
                () -> controlador.eliminar(99L)
        );

        assertTrue(ex.getMessage().contains("99"));
        verify(servicio, times(1)).eliminar(99L);
    }
}