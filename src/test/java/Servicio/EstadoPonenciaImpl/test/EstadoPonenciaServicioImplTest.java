/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Servicio.EstadoPonenciaImpl.test;

/**
 *
 * @author eiler
 */
import com.eiler.microservicio_4_convocatorias_ponencias.dtos.estadoPonencia.EstadoPonenciaResponse;
import com.eiler.microservicio_4_convocatorias_ponencias.excepciones.RecursoNoEncontradoException;
import com.eiler.microservicio_4_convocatorias_ponencias.modelos.estadoPonencia.EstadoPonencia;
import com.eiler.microservicio_4_convocatorias_ponencias.repositorios.estadoponencia.EstadoPonenciaRepositorio;
import com.eiler.microservicio_4_convocatorias_ponencias.servicios.estadoPonencia.EstadoPonenciaServicioImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EstadoPonenciaServicioImplTest {

    @Mock
    private EstadoPonenciaRepositorio repositorio;

    @InjectMocks
    private EstadoPonenciaServicioImpl servicio;

    private EstadoPonencia pendiente;
    private EstadoPonencia aprobado;
    private EstadoPonencia rechazado;

    @BeforeEach
    void setUp() {
        pendiente  = EstadoPonencia.builder().idEstado(1L).nombreEstado("PENDIENTE").build();
        aprobado   = EstadoPonencia.builder().idEstado(2L).nombreEstado("APROBADO").build();
        rechazado  = EstadoPonencia.builder().idEstado(3L).nombreEstado("RECHAZADO").build();
    }

    // obtener por id

    @Test
    void obtenerPorId_cuandoExiste_retornaResponse() throws RecursoNoEncontradoException {
        when(repositorio.findById(1L)).thenReturn(Optional.of(pendiente));

        EstadoPonenciaResponse resultado = servicio.obtenerPorId(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getIdEstado());
        assertEquals("PENDIENTE", resultado.getNombreEstado());
        verify(repositorio, times(1)).findById(1L);
    }

    @Test
    void obtenerPorId_cuandoNoExiste_lanzaExcepcion() {
        when(repositorio.findById(99L)).thenReturn(Optional.empty());

        RecursoNoEncontradoException ex = assertThrows(
                RecursoNoEncontradoException.class,
                () -> servicio.obtenerPorId(99L)
        );

        assertTrue(ex.getMessage().contains("99"));
        verify(repositorio, times(1)).findById(99L);
    }

    //listar todos

    @Test
    void listarTodos_retornaTodosLosEstados() {
        when(repositorio.findAll()).thenReturn(List.of(pendiente, aprobado, rechazado));

        List<EstadoPonenciaResponse> resultado = servicio.listarTodos();

        assertEquals(3, resultado.size());
        assertEquals("PENDIENTE",  resultado.get(0).getNombreEstado());
        assertEquals("APROBADO",   resultado.get(1).getNombreEstado());
        assertEquals("RECHAZADO",  resultado.get(2).getNombreEstado());
        verify(repositorio, times(1)).findAll();
    }

    @Test
    void listarTodos_cuandoNoHayEstados_retornaListaVacia() {
        when(repositorio.findAll()).thenReturn(List.of());

        List<EstadoPonenciaResponse> resultado = servicio.listarTodos();

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
        verify(repositorio, times(1)).findAll();
    }
}
