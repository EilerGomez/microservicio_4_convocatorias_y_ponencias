/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Servicio.ConvocatoriaServicioImpl.test;

/**
 *
 * @author eiler
 */


import com.eiler.microservicio_4_convocatorias_ponencias.dtos.convocatoria.ConvocatoriaRequest;
import com.eiler.microservicio_4_convocatorias_ponencias.dtos.convocatoria.ConvocatoriaResponse;
import com.eiler.microservicio_4_convocatorias_ponencias.excepciones.RecursoNoEncontradoException;
import com.eiler.microservicio_4_convocatorias_ponencias.modelos.convocatoria.Convocatoria;
import com.eiler.microservicio_4_convocatorias_ponencias.repositorios.convocatoria.ConvocatoriaRepositorio;
import com.eiler.microservicio_4_convocatorias_ponencias.servicios.convocatoria.ConvocatoriaServicioImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConvocatoriaServicioImplTest {

    @Mock
    private ConvocatoriaRepositorio repositorio;

    @InjectMocks
    private ConvocatoriaServicioImpl servicio;

    private Convocatoria convocatoriaAbierta;
    private Convocatoria convocatoriaCerrada;
    private ConvocatoriaRequest requestValido;

    private final LocalDateTime ahora        = LocalDateTime.now();
    private final LocalDateTime enUnaSemana  = ahora.plusDays(7);
    private final LocalDateTime ayer         = ahora.minusDays(1);

    @BeforeEach
    void setUp() {
        convocatoriaAbierta = Convocatoria.builder()
                .idConvocatoria(1L)
                .idCongreso(10L)
                .nombreConvocatoria("Convocatoria 2026")
                .descripcion("Descripción de prueba")
                .fechaApertura(ahora)
                .fechaCierre(enUnaSemana)
                .estaAbierta(true)
                .build();

        convocatoriaCerrada = Convocatoria.builder()
                .idConvocatoria(2L)
                .idCongreso(10L)
                .nombreConvocatoria("Convocatoria Cerrada")
                .descripcion("Ya cerró")
                .fechaApertura(ayer)
                .fechaCierre(ahora.minusHours(1))
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

    @Test
    void crearConDatosValidosRetornaResponse() {
        when(repositorio.save(any(Convocatoria.class))).thenReturn(convocatoriaAbierta);

        ConvocatoriaResponse resultado = servicio.crear(requestValido);

        assertNotNull(resultado);
        assertEquals(1L,                 resultado.getIdConvocatoria());
        assertEquals("Convocatoria 2026", resultado.getNombreConvocatoria());
        assertTrue(resultado.getEstaAbierta());
        verify(repositorio, times(1)).save(any(Convocatoria.class));
    }

    @Test
    void crearCierreMenorQueAperturaLanzaIllegalArgument() {
        requestValido.setFechaCierre(ahora.minusDays(1));

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> servicio.crear(requestValido)
        );

        assertTrue(ex.getMessage().contains("posterior"));
        verify(repositorio, never()).save(any());
    }

    @Test
    void crearCierreIgualQueAperturaLanzaIllegalArgument() {
        requestValido.setFechaCierre(ahora);

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> servicio.crear(requestValido)
        );

        assertTrue(ex.getMessage().contains("posterior"));
        verify(repositorio, never()).save(any());
    }

    @Test
    void crearFechaAperturaNullLanzaIllegalArgument() {
        requestValido.setFechaApertura(null);

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> servicio.crear(requestValido)
        );

        assertTrue(ex.getMessage().contains("obligatorias"));
        verify(repositorio, never()).save(any());
    }

    @Test
    void crear_fechaCierreNull_lanzaIllegalArgument() {
        requestValido.setFechaCierre(null);

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> servicio.crear(requestValido)
        );

        assertTrue(ex.getMessage().contains("obligatorias"));
        verify(repositorio, never()).save(any());
    }


    @Test
    void obtenerPorId_cuandoExiste_retornaResponse() throws RecursoNoEncontradoException {
        when(repositorio.findById(1L)).thenReturn(Optional.of(convocatoriaAbierta));

        ConvocatoriaResponse resultado = servicio.obtenerPorId(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getIdConvocatoria());
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

    @Test
    void listarPorCongreso_retornaLista() {
        when(repositorio.findByIdCongreso(10L))
                .thenReturn(List.of(convocatoriaAbierta, convocatoriaCerrada));

        List<ConvocatoriaResponse> resultado = servicio.listarPorCongreso(10L);

        assertEquals(2, resultado.size());
        verify(repositorio, times(1)).findByIdCongreso(10L);
    }

    @Test
    void listarPorCongreso_sinConvocatorias_retornaListaVacia() {
        when(repositorio.findByIdCongreso(99L)).thenReturn(List.of());

        List<ConvocatoriaResponse> resultado = servicio.listarPorCongreso(99L);

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
        verify(repositorio, times(1)).findByIdCongreso(99L);
    }


    @Test
    void actualizar_convocatoriaAbierta_retornaResponseActualizado()
            throws RecursoNoEncontradoException {
        when(repositorio.findById(1L)).thenReturn(Optional.of(convocatoriaAbierta));
        when(repositorio.save(any(Convocatoria.class))).thenReturn(convocatoriaAbierta);

        ConvocatoriaResponse resultado = servicio.actualizar(1L, requestValido);

        assertNotNull(resultado);
        verify(repositorio, times(1)).findById(1L);
        verify(repositorio, times(1)).save(any(Convocatoria.class));
    }

    @Test
    void actualizar_convocatoriaCerrada_lanzaIllegalState() {
        when(repositorio.findById(2L)).thenReturn(Optional.of(convocatoriaCerrada));

        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> servicio.actualizar(2L, requestValido)
        );

        assertTrue(ex.getMessage().contains("cerrada"));
        verify(repositorio, never()).save(any());
    }

    @Test
    void actualizar_noExiste_lanzaExcepcion() {
        when(repositorio.findById(99L)).thenReturn(Optional.empty());

        assertThrows(
                RecursoNoEncontradoException.class,
                () -> servicio.actualizar(99L, requestValido)
        );

        verify(repositorio, never()).save(any());
    }

    @Test
    void actualizar_fechasInvalidas_lanzaIllegalArgument() {
        when(repositorio.findById(1L)).thenReturn(Optional.of(convocatoriaAbierta));
        requestValido.setFechaCierre(ahora.minusDays(1));

        assertThrows(
                IllegalArgumentException.class,
                () -> servicio.actualizar(1L, requestValido)
        );

        verify(repositorio, never()).save(any());
    }


    @Test
    void cerrar_convocatoriaAbierta_guardaConFalse() throws RecursoNoEncontradoException {
        when(repositorio.findById(1L)).thenReturn(Optional.of(convocatoriaAbierta));
        when(repositorio.save(any(Convocatoria.class))).thenReturn(convocatoriaAbierta);

        servicio.cerrar(1L);

        assertFalse(convocatoriaAbierta.getEstaAbierta());
        verify(repositorio, times(1)).save(convocatoriaAbierta);
    }

    @Test
    void cerrar_convocatoriaYaCerrada_lanzaIllegalState() {
        when(repositorio.findById(2L)).thenReturn(Optional.of(convocatoriaCerrada));

        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> servicio.cerrar(2L)
        );

        assertTrue(ex.getMessage().contains("ya está cerrada"));
        verify(repositorio, never()).save(any());
    }

    @Test
    void cerrar_noExiste_lanzaExcepcion() {
        when(repositorio.findById(99L)).thenReturn(Optional.empty());

        assertThrows(
                RecursoNoEncontradoException.class,
                () -> servicio.cerrar(99L)
        );

        verify(repositorio, never()).save(any());
    }


    @Test
    void eliminar_cuandoExiste_eliminaCorrectamente() throws RecursoNoEncontradoException {
        when(repositorio.findById(1L)).thenReturn(Optional.of(convocatoriaAbierta));

        servicio.eliminar(1L);

        verify(repositorio, times(1)).delete(convocatoriaAbierta);
    }

    @Test
    void eliminar_cuandoNoExiste_lanzaExcepcion() {
        when(repositorio.findById(99L)).thenReturn(Optional.empty());

        assertThrows(
                RecursoNoEncontradoException.class,
                () -> servicio.eliminar(99L)
        );

        verify(repositorio, never()).delete(any());
    }
}