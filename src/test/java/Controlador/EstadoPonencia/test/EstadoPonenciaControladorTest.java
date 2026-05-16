/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controlador.EstadoPonencia.test;

/**
 *
 * @author eiler
 */

import com.eiler.microservicio_4_convocatorias_ponencias.controladores.estadoPonencia.EstadoPonenciaControlador;
import com.eiler.microservicio_4_convocatorias_ponencias.dtos.estadoPonencia.EstadoPonenciaResponse;
import com.eiler.microservicio_4_convocatorias_ponencias.excepciones.RecursoNoEncontradoException;
import com.eiler.microservicio_4_convocatorias_ponencias.servicios.estadoPonencia.EstadoPonenciaServicio;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EstadoPonenciaControladorTest {

    @Mock
    private EstadoPonenciaServicio servicio;

    @InjectMocks
    private EstadoPonenciaControlador controlador;

    private EstadoPonenciaResponse pendiente;
    private EstadoPonenciaResponse aprobado;
    private EstadoPonenciaResponse rechazado;

    @BeforeEach
    void setUp() {
        pendiente = new EstadoPonenciaResponse(1L, "PENDIENTE");
        aprobado  = new EstadoPonenciaResponse(2L, "APROBADO");
        rechazado = new EstadoPonenciaResponse(3L, "RECHAZADO");
    }


    @Test
    void listarTodos_retorna200ConLista() {
        when(servicio.listarTodos()).thenReturn(List.of(pendiente, aprobado, rechazado));

        ResponseEntity<List<EstadoPonenciaResponse>> respuesta = controlador.listarTodos();

        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        assertNotNull(respuesta.getBody());
        assertEquals(3, respuesta.getBody().size());
        verify(servicio, times(1)).listarTodos();
    }

    @Test
    void listarTodos_cuandoListaVacia_retorna200ConListaVacia() {
        when(servicio.listarTodos()).thenReturn(List.of());

        ResponseEntity<List<EstadoPonenciaResponse>> respuesta = controlador.listarTodos();

        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        assertNotNull(respuesta.getBody());
        assertTrue(respuesta.getBody().isEmpty());
        verify(servicio, times(1)).listarTodos();
    }


    @Test
    void obtenerPorId_cuandoExiste_retorna200() throws RecursoNoEncontradoException {
        when(servicio.obtenerPorId(1L)).thenReturn(pendiente);

        ResponseEntity<EstadoPonenciaResponse> respuesta = controlador.obtenerPorId(1L);

        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        assertNotNull(respuesta.getBody());
        assertEquals(1L,          respuesta.getBody().getIdEstado());
        assertEquals("PENDIENTE", respuesta.getBody().getNombreEstado());
        verify(servicio, times(1)).obtenerPorId(1L);
    }

    @Test
    void obtenerPorId_cuandoNoExiste_lanzaExcepcion() throws RecursoNoEncontradoException {
        when(servicio.obtenerPorId(99L))
                .thenThrow(new RecursoNoEncontradoException(
                        "Estado de ponencia con ID 99 no encontrado"));

        RecursoNoEncontradoException ex = assertThrows(
                RecursoNoEncontradoException.class,
                () -> controlador.obtenerPorId(99L)
        );

        assertTrue(ex.getMessage().contains("99"));
        verify(servicio, times(1)).obtenerPorId(99L);
    }
}