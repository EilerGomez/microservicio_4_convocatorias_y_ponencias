package com.eiler.microservicio_4_convocatorias_ponencias.servicios.ponencias;

import com.eiler.microservicio_4_convocatorias_ponencias.dtos.ponencias.PonenciaRequest;
import com.eiler.microservicio_4_convocatorias_ponencias.dtos.ponencias.PonenciaResponse;
import com.eiler.microservicio_4_convocatorias_ponencias.excepciones.RecursoNoEncontradoException;
import com.eiler.microservicio_4_convocatorias_ponencias.modelos.convocatoria.Convocatoria;
import com.eiler.microservicio_4_convocatorias_ponencias.modelos.estadoPonencia.EstadoPonencia;
import com.eiler.microservicio_4_convocatorias_ponencias.modelos.ponencias.EstadoPonenciaEnum;
import com.eiler.microservicio_4_convocatorias_ponencias.modelos.ponencias.Ponencia;
import com.eiler.microservicio_4_convocatorias_ponencias.repositorios.convocatoria.ConvocatoriaRepositorio;
import com.eiler.microservicio_4_convocatorias_ponencias.repositorios.estadoponencia.EstadoPonenciaRepositorio;
import com.eiler.microservicio_4_convocatorias_ponencias.repositorios.ponencias.PonenciaRepositorio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
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
class PonenciaServicioImplTest {

    @Mock
    private PonenciaRepositorio ponenciaRepositorio;

    @Mock
    private ConvocatoriaRepositorio convocatoriaRepositorio;

    @Mock
    private EstadoPonenciaRepositorio estadoRepositorio;

    @InjectMocks
    private PonenciaServicioImpl servicio;

    private Convocatoria convAbierta;
    private Convocatoria convCerrada;
    private EstadoPonencia pendiente;
    private EstadoPonencia aprobado;
    private EstadoPonencia rechazado;
    private PonenciaRequest requestValido;
    private Ponencia ponPendiente;
    private Ponencia ponRechazada;

    @BeforeEach
    void setUp() {
        convAbierta = Convocatoria.builder()
                .idConvocatoria(1L)
                .nombreConvocatoria("Conv 2026")
                .estaAbierta(true)
                .fechaApertura(LocalDateTime.now().minusDays(1))
                .fechaCierre(LocalDateTime.now().plusDays(10))
                .build();

        convCerrada = Convocatoria.builder()
                .idConvocatoria(2L)
                .nombreConvocatoria("Cerrada")
                .estaAbierta(false)
                .build();

        pendiente = EstadoPonencia.builder()
                .idEstado(EstadoPonenciaEnum.PENDIENTE.getId())
                .nombreEstado("PENDIENTE")
                .build();

        aprobado = EstadoPonencia.builder()
                .idEstado(EstadoPonenciaEnum.APROBADO.getId())
                .nombreEstado("APROBADO")
                .build();

        rechazado = EstadoPonencia.builder()
                .idEstado(EstadoPonenciaEnum.RECHAZADO.getId())
                .nombreEstado("RECHAZADO")
                .build();

        requestValido = PonenciaRequest.builder()
                .idConvocatoria(1L)
                .idTipoActividad(1)
                .tituloPonencia("Mi ponencia")
                .resumen("Resumen detallado")
                .urlArchivo("http://archivo-request.pdf")
                .build();

        ponPendiente = Ponencia.builder()
                .idPonencia(1L)
                .convocatoria(convAbierta)
                .idUsuario(42L)
                .idTipoActividad(1)
                .estado(pendiente)
                .tituloPonencia("Mi ponencia")
                .resumen("Resumen")
                .urlArchivo("http://archivo.pdf")
                .build();

        ponRechazada = Ponencia.builder()
                .idPonencia(2L)
                .convocatoria(convAbierta)
                .idUsuario(42L)
                .idTipoActividad(1)
                .estado(rechazado)
                .tituloPonencia("Rechazada")
                .resumen("Resumen anterior")
                .urlArchivo("http://archivo-anterior.pdf")
                .build();
    }

    @Test
    void enviarConvocatoriaAbiertaSinActivaCreaPonencia() throws RecursoNoEncontradoException {
        when(convocatoriaRepositorio.findById(1L))
                .thenReturn(Optional.of(convAbierta));
        when(ponenciaRepositorio.findByConvocatoriaIdConvocatoriaAndIdUsuario(1L, 42L))
                .thenReturn(List.of());
        when(estadoRepositorio.findById(EstadoPonenciaEnum.PENDIENTE.getId()))
                .thenReturn(Optional.of(pendiente));
        when(ponenciaRepositorio.save(any(Ponencia.class)))
                .thenReturn(ponPendiente);

        PonenciaResponse response = servicio.enviar(
                requestValido,
                42L,
                "http://archivo.pdf"
        );

        assertNotNull(response);
        assertEquals(1L, response.getIdPonencia());
        assertEquals(42L, response.getIdUsuario());
        assertEquals("PENDIENTE", response.getNombreEstado());

        verify(ponenciaRepositorio).save(any(Ponencia.class));
    }

    @Test
    void enviarGuardaDatosCorrectosIncluyendoUrlArchivo() throws RecursoNoEncontradoException {
        when(convocatoriaRepositorio.findById(1L))
                .thenReturn(Optional.of(convAbierta));
        when(ponenciaRepositorio.findByConvocatoriaIdConvocatoriaAndIdUsuario(1L, 42L))
                .thenReturn(List.of());
        when(estadoRepositorio.findById(EstadoPonenciaEnum.PENDIENTE.getId()))
                .thenReturn(Optional.of(pendiente));
        when(ponenciaRepositorio.save(any(Ponencia.class)))
                .thenAnswer(invocation -> {
                    Ponencia ponencia = invocation.getArgument(0);
                    ponencia.setIdPonencia(100L);
                    return ponencia;
                });

        servicio.enviar(
                requestValido,
                42L,
                "http://archivo-nuevo.pdf"
        );

        ArgumentCaptor<Ponencia> captor = ArgumentCaptor.forClass(Ponencia.class);
        verify(ponenciaRepositorio).save(captor.capture());

        Ponencia guardada = captor.getValue();

        assertEquals(convAbierta, guardada.getConvocatoria());
        assertEquals(42L, guardada.getIdUsuario());
        assertEquals(1, guardada.getIdTipoActividad());
        assertEquals(pendiente, guardada.getEstado());
        assertEquals("Mi ponencia", guardada.getTituloPonencia());
        assertEquals("Resumen detallado", guardada.getResumen());
        assertEquals("http://archivo-nuevo.pdf", guardada.getUrlArchivo());
    }

    @Test
    void enviarConvocatoriaNoExisteLanzaExcepcion() {
        when(convocatoriaRepositorio.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                RecursoNoEncontradoException.class,
                () -> servicio.enviar(requestValido, 42L, null)
        );

        verify(ponenciaRepositorio, never()).save(any(Ponencia.class));
    }

    @Test
    void enviarConvocatoriaCerradaLanzaIllegalState() {
        when(convocatoriaRepositorio.findById(1L))
                .thenReturn(Optional.of(convCerrada));

        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> servicio.enviar(requestValido, 42L, null)
        );

        assertTrue(ex.getMessage().contains("cerrada"));
        verify(ponenciaRepositorio, never()).save(any(Ponencia.class));
    }

    @Test
    void enviarYaTienePonenciaActivaLanzaIllegalState() {
        when(convocatoriaRepositorio.findById(1L))
                .thenReturn(Optional.of(convAbierta));
        when(ponenciaRepositorio.findByConvocatoriaIdConvocatoriaAndIdUsuario(1L, 42L))
                .thenReturn(List.of(ponPendiente));

        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> servicio.enviar(requestValido, 42L, null)
        );

        assertTrue(ex.getMessage().contains("activa"));
        verify(ponenciaRepositorio, never()).save(any(Ponencia.class));
    }

    @Test
    void enviarSoloTieneRechazadaPermiteCrearNueva() throws RecursoNoEncontradoException {
        when(convocatoriaRepositorio.findById(1L))
                .thenReturn(Optional.of(convAbierta));
        when(ponenciaRepositorio.findByConvocatoriaIdConvocatoriaAndIdUsuario(1L, 42L))
                .thenReturn(List.of(ponRechazada));
        when(estadoRepositorio.findById(EstadoPonenciaEnum.PENDIENTE.getId()))
                .thenReturn(Optional.of(pendiente));
        when(ponenciaRepositorio.save(any(Ponencia.class)))
                .thenReturn(ponPendiente);

        PonenciaResponse response = servicio.enviar(
                requestValido,
                42L,
                "http://archivo.pdf"
        );

        assertNotNull(response);
        assertEquals("PENDIENTE", response.getNombreEstado());

        verify(ponenciaRepositorio).save(any(Ponencia.class));
    }

    @Test
    void enviarEstadoPendienteNoExisteLanzaIllegalState() {
        when(convocatoriaRepositorio.findById(1L))
                .thenReturn(Optional.of(convAbierta));
        when(ponenciaRepositorio.findByConvocatoriaIdConvocatoriaAndIdUsuario(1L, 42L))
                .thenReturn(List.of());
        when(estadoRepositorio.findById(EstadoPonenciaEnum.PENDIENTE.getId()))
                .thenReturn(Optional.empty());

        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> servicio.enviar(requestValido, 42L, null)
        );

        assertTrue(ex.getMessage().contains("PENDIENTE"));
        verify(ponenciaRepositorio, never()).save(any(Ponencia.class));
    }

    @Test
    void obtenerPorIdExisteRetornaResponse() throws RecursoNoEncontradoException {
        when(ponenciaRepositorio.findById(1L))
                .thenReturn(Optional.of(ponPendiente));

        PonenciaResponse response = servicio.obtenerPorId(1L);

        assertEquals(1L, response.getIdPonencia());
        assertEquals(42L, response.getIdUsuario());
        assertEquals("PENDIENTE", response.getNombreEstado());
    }

    @Test
    void obtenerPorIdNoExisteLanzaExcepcion() {
        when(ponenciaRepositorio.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                RecursoNoEncontradoException.class,
                () -> servicio.obtenerPorId(99L)
        );
    }

    @Test
    void listarPorConvocatoriaRetornaLista() {
        when(ponenciaRepositorio.findByConvocatoriaIdConvocatoria(1L))
                .thenReturn(List.of(ponPendiente, ponRechazada));

        List<PonenciaResponse> response = servicio.listarPorConvocatoria(1L);

        assertEquals(2, response.size());
        assertEquals(1L, response.get(0).getIdPonencia());
        assertEquals(2L, response.get(1).getIdPonencia());
    }

    @Test
    void listarPorConvocatoriaSinResultadosRetornaListaVacia() {
        when(ponenciaRepositorio.findByConvocatoriaIdConvocatoria(99L))
                .thenReturn(List.of());

        List<PonenciaResponse> response = servicio.listarPorConvocatoria(99L);

        assertTrue(response.isEmpty());
    }

    @Test
    void listarMisPonenciasRetornaListaDelUsuario() {
        when(ponenciaRepositorio.findByIdUsuario(42L))
                .thenReturn(List.of(ponPendiente));

        List<PonenciaResponse> response = servicio.listarMisPonencias(42L);

        assertEquals(1, response.size());
        assertEquals(42L, response.get(0).getIdUsuario());
    }

    @Test
    void listarMisPonenciasSinResultadosRetornaListaVacia() {
        when(ponenciaRepositorio.findByIdUsuario(99L))
                .thenReturn(List.of());

        List<PonenciaResponse> response = servicio.listarMisPonencias(99L);

        assertTrue(response.isEmpty());
    }

    @Test
    void reenviarPonenciaRechazadaConArchivoNuevoActualiza() throws RecursoNoEncontradoException {
        when(ponenciaRepositorio.findById(2L))
                .thenReturn(Optional.of(ponRechazada));
        when(convocatoriaRepositorio.findById(1L))
                .thenReturn(Optional.of(convAbierta));
        when(estadoRepositorio.findById(EstadoPonenciaEnum.PENDIENTE.getId()))
                .thenReturn(Optional.of(pendiente));
        when(ponenciaRepositorio.save(any(Ponencia.class)))
                .thenReturn(ponRechazada);

        PonenciaResponse response = servicio.reenviar(
                2L,
                requestValido,
                42L,
                "http://archivo-nuevo.pdf"
        );

        assertNotNull(response);
        verify(ponenciaRepositorio).save(any(Ponencia.class));
    }

    @Test
    void reenviarConArchivoNuevoActualizaUrlYEstado() throws RecursoNoEncontradoException {
        when(ponenciaRepositorio.findById(2L))
                .thenReturn(Optional.of(ponRechazada));
        when(convocatoriaRepositorio.findById(1L))
                .thenReturn(Optional.of(convAbierta));
        when(estadoRepositorio.findById(EstadoPonenciaEnum.PENDIENTE.getId()))
                .thenReturn(Optional.of(pendiente));
        when(ponenciaRepositorio.save(any(Ponencia.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        servicio.reenviar(
                2L,
                requestValido,
                42L,
                "http://archivo-nuevo.pdf"
        );

        ArgumentCaptor<Ponencia> captor = ArgumentCaptor.forClass(Ponencia.class);
        verify(ponenciaRepositorio).save(captor.capture());

        Ponencia guardada = captor.getValue();

        assertEquals("Mi ponencia", guardada.getTituloPonencia());
        assertEquals("Resumen detallado", guardada.getResumen());
        assertEquals(1, guardada.getIdTipoActividad());
        assertEquals(pendiente, guardada.getEstado());
        assertEquals("http://archivo-nuevo.pdf", guardada.getUrlArchivo());
    }

    @Test
    void reenviarSinArchivoNuevoConservaUrlAnterior() throws RecursoNoEncontradoException {
        when(ponenciaRepositorio.findById(2L))
                .thenReturn(Optional.of(ponRechazada));
        when(convocatoriaRepositorio.findById(1L))
                .thenReturn(Optional.of(convAbierta));
        when(estadoRepositorio.findById(EstadoPonenciaEnum.PENDIENTE.getId()))
                .thenReturn(Optional.of(pendiente));
        when(ponenciaRepositorio.save(any(Ponencia.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        servicio.reenviar(
                2L,
                requestValido,
                42L,
                null
        );

        ArgumentCaptor<Ponencia> captor = ArgumentCaptor.forClass(Ponencia.class);
        verify(ponenciaRepositorio).save(captor.capture());

        assertEquals("http://archivo-anterior.pdf", captor.getValue().getUrlArchivo());
    }

    @Test
    void reenviarConArchivoEnBlancoConservaUrlAnterior() throws RecursoNoEncontradoException {
        when(ponenciaRepositorio.findById(2L))
                .thenReturn(Optional.of(ponRechazada));
        when(convocatoriaRepositorio.findById(1L))
                .thenReturn(Optional.of(convAbierta));
        when(estadoRepositorio.findById(EstadoPonenciaEnum.PENDIENTE.getId()))
                .thenReturn(Optional.of(pendiente));
        when(ponenciaRepositorio.save(any(Ponencia.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        servicio.reenviar(
                2L,
                requestValido,
                42L,
                "   "
        );

        ArgumentCaptor<Ponencia> captor = ArgumentCaptor.forClass(Ponencia.class);
        verify(ponenciaRepositorio).save(captor.capture());

        assertEquals("http://archivo-anterior.pdf", captor.getValue().getUrlArchivo());
    }

    @Test
    void reenviarPonenciaNoExisteLanzaExcepcion() {
        when(ponenciaRepositorio.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                RecursoNoEncontradoException.class,
                () -> servicio.reenviar(99L, requestValido, 42L, null)
        );

        verify(ponenciaRepositorio, never()).save(any(Ponencia.class));
    }

    @Test
    void reenviarOtroUsuarioLanzaIllegalState() {
        when(ponenciaRepositorio.findById(2L))
                .thenReturn(Optional.of(ponRechazada));

        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> servicio.reenviar(2L, requestValido, 99L, null)
        );

        assertTrue(ex.getMessage().contains("no es tuya"));
        verify(ponenciaRepositorio, never()).save(any(Ponencia.class));
    }

    @Test
    void reenviarPonenciaNoRechazadaLanzaIllegalState() {
        when(ponenciaRepositorio.findById(1L))
                .thenReturn(Optional.of(ponPendiente));

        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> servicio.reenviar(1L, requestValido, 42L, null)
        );

        assertTrue(ex.getMessage().contains("rechazada"));
        verify(ponenciaRepositorio, never()).save(any(Ponencia.class));
    }

    @Test
    void reenviarConvocatoriaCerradaLanzaIllegalState() {
        Ponencia ponenciaConvocatoriaCerrada = Ponencia.builder()
                .idPonencia(3L)
                .convocatoria(convCerrada)
                .idUsuario(42L)
                .idTipoActividad(1)
                .estado(rechazado)
                .tituloPonencia("Rechazada")
                .resumen("Resumen")
                .urlArchivo("http://archivo.pdf")
                .build();

        when(ponenciaRepositorio.findById(3L))
                .thenReturn(Optional.of(ponenciaConvocatoriaCerrada));
        when(convocatoriaRepositorio.findById(2L))
                .thenReturn(Optional.of(convCerrada));

        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> servicio.reenviar(3L, requestValido, 42L, null)
        );

        assertTrue(ex.getMessage().contains("cerrada"));
        verify(ponenciaRepositorio, never()).save(any(Ponencia.class));
    }

    @Test
    void reenviarEstadoPendienteNoExisteLanzaIllegalState() {
        when(ponenciaRepositorio.findById(2L))
                .thenReturn(Optional.of(ponRechazada));
        when(convocatoriaRepositorio.findById(1L))
                .thenReturn(Optional.of(convAbierta));
        when(estadoRepositorio.findById(EstadoPonenciaEnum.PENDIENTE.getId()))
                .thenReturn(Optional.empty());

        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> servicio.reenviar(2L, requestValido, 42L, null)
        );

        assertTrue(ex.getMessage().contains("PENDIENTE"));
        verify(ponenciaRepositorio, never()).save(any(Ponencia.class));
    }

    @Test
    void listarAprobadasPorCongresoRetornaLista() {
        Ponencia ponAprobada = Ponencia.builder()
                .idPonencia(3L)
                .convocatoria(convAbierta)
                .idUsuario(42L)
                .idTipoActividad(1)
                .estado(aprobado)
                .tituloPonencia("Ponencia aprobada")
                .resumen("Resumen")
                .urlArchivo("http://aprobada.pdf")
                .build();

        when(estadoRepositorio.findById(EstadoPonenciaEnum.APROBADO.getId()))
                .thenReturn(Optional.of(aprobado));
        when(ponenciaRepositorio.findByConvocatoria_IdCongresoAndEstado(10L, aprobado))
                .thenReturn(List.of(ponAprobada));

        List<PonenciaResponse> response = servicio.listarAprobadasPorCongreso(10L);

        assertEquals(1, response.size());
        assertEquals("APROBADO", response.get(0).getNombreEstado());
    }

    @Test
    void listarAprobadasPorCongresoSinResultadosRetornaListaVacia() {
        when(estadoRepositorio.findById(EstadoPonenciaEnum.APROBADO.getId()))
                .thenReturn(Optional.of(aprobado));
        when(ponenciaRepositorio.findByConvocatoria_IdCongresoAndEstado(99L, aprobado))
                .thenReturn(List.of());

        List<PonenciaResponse> response = servicio.listarAprobadasPorCongreso(99L);

        assertTrue(response.isEmpty());
    }

    @Test
    void listarAprobadasEstadoAprobadoNoExisteLanzaIllegalState() {
        when(estadoRepositorio.findById(EstadoPonenciaEnum.APROBADO.getId()))
                .thenReturn(Optional.empty());

        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> servicio.listarAprobadasPorCongreso(10L)
        );

        assertTrue(ex.getMessage().contains("APROBADO"));
    }
}