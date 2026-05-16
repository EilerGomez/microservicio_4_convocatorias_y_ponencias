/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.eiler.microservicio_4_convocatorias_ponencias.excepciones;

/**
 *
 * @author eiler
 */


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ManejadorExcepcionesTest {

    @InjectMocks
    private ManejadorExcepciones manejador;


    @Test
    void recursoNoEncontrado_retorna404() {
        RecursoNoEncontradoException ex =
                new RecursoNoEncontradoException("Convocatoria con ID 99 no encontrada");

        ResponseEntity<Map<String, Object>> respuesta =
                manejador.handleNoEncontrado(ex);

        assertEquals(HttpStatus.NOT_FOUND, respuesta.getStatusCode());
        assertNotNull(respuesta.getBody());
        assertEquals(404, respuesta.getBody().get("status"));
        assertEquals("Convocatoria con ID 99 no encontrada",
                respuesta.getBody().get("mensaje"));
    }

    @Test
    void recursoNoEncontrado_tieneTimestamp() {
        RecursoNoEncontradoException ex =
                new RecursoNoEncontradoException("No encontrado");

        ResponseEntity<Map<String, Object>> respuesta =
                manejador.handleNoEncontrado(ex);

        assertNotNull(respuesta.getBody().get("timestamp"));
        assertInstanceOf(LocalDateTime.class, respuesta.getBody().get("timestamp"));
    }


    @Test
    void illegalArgument_retorna400() {
        IllegalArgumentException ex =
                new IllegalArgumentException(
                        "La fecha de cierre debe ser posterior a la fecha de apertura");

        ResponseEntity<Map<String, Object>> respuesta =
                manejador.handleBadRequest(ex);

        assertEquals(HttpStatus.BAD_REQUEST, respuesta.getStatusCode());
        assertNotNull(respuesta.getBody());
        assertEquals(400, respuesta.getBody().get("status"));
        assertEquals(
                "La fecha de cierre debe ser posterior a la fecha de apertura",
                respuesta.getBody().get("mensaje"));
    }

    @Test
    void illegalArgument_tieneTimestamp() {
        IllegalArgumentException ex =
                new IllegalArgumentException("Argumento inválido");

        ResponseEntity<Map<String, Object>> respuesta =
                manejador.handleBadRequest(ex);

        assertNotNull(respuesta.getBody().get("timestamp"));
        assertInstanceOf(LocalDateTime.class, respuesta.getBody().get("timestamp"));
    }


    @Test
    void illegalState_retorna400() {
        IllegalStateException ex =
                new IllegalStateException("No se puede modificar una convocatoria cerrada");

        ResponseEntity<Map<String, Object>> respuesta =
                manejador.handleBadRequest(ex);

        assertEquals(HttpStatus.BAD_REQUEST, respuesta.getStatusCode());
        assertEquals(400, respuesta.getBody().get("status"));
        assertEquals(
                "No se puede modificar una convocatoria cerrada",
                respuesta.getBody().get("mensaje"));
    }

  
    @Test
    void validacionNotBlank_retorna400ConCampoYMensaje() {
        FieldError fieldError = new FieldError(
                "convocatoriaRequest",
                "nombreConvocatoria",
                "El nombre de la convocatoria es obligatorio");

        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError));

        MethodArgumentNotValidException ex =
                mock(MethodArgumentNotValidException.class);
        when(ex.getBindingResult()).thenReturn(bindingResult);

        ResponseEntity<Map<String, Object>> respuesta =
                manejador.handleValidacion(ex);

        assertEquals(HttpStatus.BAD_REQUEST, respuesta.getStatusCode());
        assertEquals(400, respuesta.getBody().get("status"));
        assertEquals(
                "nombreConvocatoria: El nombre de la convocatoria es obligatorio",
                respuesta.getBody().get("mensaje"));
    }

    @Test
    void validacion_sinErrores_retornaMensajeGenerico() {
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.getFieldErrors()).thenReturn(List.of());

        MethodArgumentNotValidException ex =
                mock(MethodArgumentNotValidException.class);
        when(ex.getBindingResult()).thenReturn(bindingResult);

        ResponseEntity<Map<String, Object>> respuesta =
                manejador.handleValidacion(ex);

        assertEquals(HttpStatus.BAD_REQUEST, respuesta.getStatusCode());
        assertEquals("Error de validación", respuesta.getBody().get("mensaje"));
    }

    @Test
    void validacion_variosErrores_retornaPrimerCampo() {
        FieldError error1 = new FieldError(
                "convocatoriaRequest", "nombreConvocatoria", "obligatorio");
        FieldError error2 = new FieldError(
                "convocatoriaRequest", "fechaApertura", "no puede ser nula");

        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(error1, error2));

        MethodArgumentNotValidException ex =
                mock(MethodArgumentNotValidException.class);
        when(ex.getBindingResult()).thenReturn(bindingResult);

        ResponseEntity<Map<String, Object>> respuesta =
                manejador.handleValidacion(ex);

        // solo retorna el primer error
        assertEquals("nombreConvocatoria: obligatorio",
                respuesta.getBody().get("mensaje"));
    }


    @Test
    void excepcionGenerica_retorna500() {
        Exception ex = new Exception("Error inesperado en la BD");

        ResponseEntity<Map<String, Object>> respuesta =
                manejador.handleGeneral(ex);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, respuesta.getStatusCode());
        assertNotNull(respuesta.getBody());
        assertEquals(500, respuesta.getBody().get("status"));
        assertEquals("Error interno del servidor", respuesta.getBody().get("mensaje"));
    }

    @Test
    void excepcionGenerica_noExponeDetallesInternos() {
        Exception ex = new RuntimeException("NullPointerException en linea 42");

        ResponseEntity<Map<String, Object>> respuesta =
                manejador.handleGeneral(ex);

        assertNotEquals(
                "NullPointerException en linea 42",
                respuesta.getBody().get("mensaje"));
        assertEquals("Error interno del servidor",
                respuesta.getBody().get("mensaje"));
    }

    @Test
    void excepcionGenerica_tieneTimestamp() {
        Exception ex = new Exception("error");

        ResponseEntity<Map<String, Object>> respuesta =
                manejador.handleGeneral(ex);

        assertNotNull(respuesta.getBody().get("timestamp"));
        assertInstanceOf(LocalDateTime.class, respuesta.getBody().get("timestamp"));
    }
    
    @Test
    void respuesta_siempreTieneTimestampStatusYMensaje() {
        RecursoNoEncontradoException ex =
                new RecursoNoEncontradoException("no existe");

        ResponseEntity<Map<String, Object>> respuesta =
                manejador.handleNoEncontrado(ex);

        Map<String, Object> body = respuesta.getBody();
        assertNotNull(body);
        assertTrue(body.containsKey("timestamp"), "debe tener timestamp");
        assertTrue(body.containsKey("status"),    "debe tener status");
        assertTrue(body.containsKey("mensaje"),   "debe tener mensaje");
    }
}