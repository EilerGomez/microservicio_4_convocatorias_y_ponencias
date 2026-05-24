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
                .idEstado(1L)
                .nombreEstado("PENDIENTE")
                .build();

        rechazado = EstadoPonencia.builder()
                .idEstado(3L)
                .nombreEstado("RECHAZADO")
                .build();

        requestValido = PonenciaRequest.builder()
                .idConvocatoria(1L)
                .idTipoActividad(1)
                .tituloPonencia("Mi ponencia")
                .resumen("Resumen detallado")
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
                .resumen("Resumen")
                .urlArchivo("http://archivo-anterior.pdf")
                .build();
    }

    @Test
    void enviarConvocatoriaAbiertaSinActivaCrea() throws RecursoNoEncontradoException {
        when(convocatoriaRepositorio.findById(1L)).thenReturn(Optional.of(convAbierta));
        when(ponenciaRepositorio.findByConvocatoriaIdConvocatoriaAndIdUsuario(1L, 42L))
                .thenReturn(List.of());
        when(estadoRepositorio.findById(EstadoPonenciaEnum.PENDIENTE.getId()))
                .thenReturn(Optional.of(pendiente));
        when(ponenciaRepositorio.save(any(Ponencia.class))).thenReturn(ponPendiente);

        PonenciaResponse r = servicio.enviar(
                requestValido,
                42L,
                "http://archivo.pdf"
        );

        assertNotNull(r);
        assertEquals("PENDIENTE", r.getNombreEstado());

        verify(ponenciaRepositorio).save(any(Ponencia.class));
    }

    @Test
    void enviarGuardaUrlArchivo() throws RecursoNoEncontradoException {
        when(convocatoriaRepositorio.findById(1L)).thenReturn(Optional.of(convAbierta));
        when(ponenciaRepositorio.findByConvocatoriaIdConvocatoriaAndIdUsuario(1L, 42L))
                .thenReturn(List.of());
        when(estadoRepositorio.findById(EstadoPonenciaEnum.PENDIENTE.getId()))
                .thenReturn(Optional.of(pendiente));
        when(ponenciaRepositorio.save(any(Ponencia.class))).thenAnswer(invocation -> invocation.getArgument(0));

        servicio.enviar(
                requestValido,
                42L,
                "http://archivo.pdf"
        );

        ArgumentCaptor<Ponencia> captor = ArgumentCaptor.forClass(Ponencia.class);
        verify(ponenciaRepositorio).save(captor.capture());

        assertEquals("http://archivo.pdf", captor.getValue().getUrlArchivo());
    }

    @Test
    void enviarConvocatoriaCerradaLanzaIllegalState() {
        when(convocatoriaRepositorio.findById(1L)).thenReturn(Optional.of(convCerrada));

        assertThrows(
                IllegalStateException.class,
                () -> servicio.enviar(requestValido, 42L, null)
        );

        verify(ponenciaRepositorio, never()).save(any(Ponencia.class));
    }

    @Test
    void enviarConvocatoriaNoExisteLanzaExcepcion() {
        when(convocatoriaRepositorio.findById(1L)).thenReturn(Optional.empty());

        assertThrows(
                RecursoNoEncontradoException.class,
                () -> servicio.enviar(requestValido, 42L, null)
        );

        verify(ponenciaRepositorio, never()).save(any(Ponencia.class));
    }

    @Test
    void enviarYaTienePendienteLanzaIllegalState() {
        when(convocatoriaRepositorio.findById(1L)).thenReturn(Optional.of(convAbierta));
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
    void enviarSoloTieneRechazadaPermiteEnviar() throws RecursoNoEncontradoException {
        when(convocatoriaRepositorio.findById(1L)).thenReturn(Optional.of(convAbierta));
        when(ponenciaRepositorio.findByConvocatoriaIdConvocatoriaAndIdUsuario(1L, 42L))
                .thenReturn(List.of(ponRechazada));
        when(estadoRepositorio.findById(EstadoPonenciaEnum.PENDIENTE.getId()))
                .thenReturn(Optional.of(pendiente));
        when(ponenciaRepositorio.save(any(Ponencia.class))).thenReturn(ponPendiente);

        PonenciaResponse r = servicio.enviar(
                requestValido,
                42L,
                "http://archivo.pdf"
        );

        assertNotNull(r);
        verify(ponenciaRepositorio).save(any(Ponencia.class));
    }

    @Test
    void obtenerPorIdExisteRetorna() throws RecursoNoEncontradoException {
        when(ponenciaRepositorio.findById(1L)).thenReturn(Optional.of(ponPendiente));

        PonenciaResponse r = servicio.obtenerPorId(1L);

        assertEquals(1L, r.getIdPonencia());
    }

    @Test
    void obtenerPorIdNoExisteLanzaExcepcion() {
        when(ponenciaRepositorio.findById(99L)).thenReturn(Optional.empty());

        assertThrows(
                RecursoNoEncontradoException.class,
                () -> servicio.obtenerPorId(99L)
        );
    }

    @Test
    void listarPorConvocatoriaRetornaLista() {
        when(ponenciaRepositorio.findByConvocatoriaIdConvocatoria(1L))
                .thenReturn(List.of(ponPendiente, ponRechazada));

        List<PonenciaResponse> r = servicio.listarPorConvocatoria(1L);

        assertEquals(2, r.size());
    }

    @Test
    void listarMisPonenciasRetornaDelUsuario() {
        when(ponenciaRepositorio.findByIdUsuario(42L))
                .thenReturn(List.of(ponPendiente));

        List<PonenciaResponse> r = servicio.listarMisPonencias(42L);

        assertEquals(1, r.size());
        assertEquals(42L, r.get(0).getIdUsuario());
    }

    @Test
    void reenviarRechazadaConvAbiertaActualiza() throws RecursoNoEncontradoException {
        when(ponenciaRepositorio.findById(2L)).thenReturn(Optional.of(ponRechazada));
        when(convocatoriaRepositorio.findById(1L)).thenReturn(Optional.of(convAbierta));
        when(estadoRepositorio.findById(EstadoPonenciaEnum.PENDIENTE.getId()))
                .thenReturn(Optional.of(pendiente));
        when(ponenciaRepositorio.save(any(Ponencia.class))).thenReturn(ponRechazada);

        PonenciaResponse r = servicio.reenviar(
                2L,
                requestValido,
                42L,
                "http://archivo-nuevo.pdf"
        );

        assertNotNull(r);
        verify(ponenciaRepositorio).save(any(Ponencia.class));
    }

    @Test
    void reenviarConArchivoNuevoActualizaUrl() throws RecursoNoEncontradoException {
        when(ponenciaRepositorio.findById(2L)).thenReturn(Optional.of(ponRechazada));
        when(convocatoriaRepositorio.findById(1L)).thenReturn(Optional.of(convAbierta));
        when(estadoRepositorio.findById(EstadoPonenciaEnum.PENDIENTE.getId()))
                .thenReturn(Optional.of(pendiente));
        when(ponenciaRepositorio.save(any(Ponencia.class))).thenAnswer(invocation -> invocation.getArgument(0));

        servicio.reenviar(
                2L,
                requestValido,
                42L,
                "http://archivo-nuevo.pdf"
        );

        ArgumentCaptor<Ponencia> captor = ArgumentCaptor.forClass(Ponencia.class);
        verify(ponenciaRepositorio).save(captor.capture());

        assertEquals("http://archivo-nuevo.pdf", captor.getValue().getUrlArchivo());
    }

    @Test
    void reenviarSinArchivoNuevoConservaUrlAnterior() throws RecursoNoEncontradoException {
        when(ponenciaRepositorio.findById(2L)).thenReturn(Optional.of(ponRechazada));
        when(convocatoriaRepositorio.findById(1L)).thenReturn(Optional.of(convAbierta));
        when(estadoRepositorio.findById(EstadoPonenciaEnum.PENDIENTE.getId()))
                .thenReturn(Optional.of(pendiente));
        when(ponenciaRepositorio.save(any(Ponencia.class))).thenAnswer(invocation -> invocation.getArgument(0));

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
    void reenviarOtroUsuarioLanzaIllegalState() {
        when(ponenciaRepositorio.findById(1L)).thenReturn(Optional.of(ponPendiente));

        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> servicio.reenviar(1L, requestValido, 99L, null)
        );

        assertTrue(ex.getMessage().contains("no es tuya"));
        verify(ponenciaRepositorio, never()).save(any(Ponencia.class));
    }

    @Test
    void reenviarNoRechazadaLanzaIllegalState() {
        when(ponenciaRepositorio.findById(1L)).thenReturn(Optional.of(ponPendiente));

        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> servicio.reenviar(1L, requestValido, 42L, null)
        );

        assertTrue(ex.getMessage().contains("rechazada"));
        verify(ponenciaRepositorio, never()).save(any(Ponencia.class));
    }

    @Test
    void reenviarConvCerradaLanzaIllegalState() {
        Ponencia ponConvCerrada = Ponencia.builder()
                .idPonencia(3L)
                .convocatoria(convCerrada)
                .idUsuario(42L)
                .idTipoActividad(1)
                .estado(rechazado)
                .tituloPonencia("t")
                .resumen("r")
                .build();

        when(ponenciaRepositorio.findById(3L)).thenReturn(Optional.of(ponConvCerrada));
        when(convocatoriaRepositorio.findById(2L)).thenReturn(Optional.of(convCerrada));

        assertThrows(
                IllegalStateException.class,
                () -> servicio.reenviar(3L, requestValido, 42L, null)
        );

        verify(ponenciaRepositorio, never()).save(any(Ponencia.class));
    }

    @Test
    void listarAprobadasPorCongresoRetornaListaFiltrada() {
        EstadoPonencia estadoAprobado = EstadoPonencia.builder()
                .idEstado(EstadoPonenciaEnum.APROBADO.getId())
                .nombreEstado("APROBADO")
                .build();

        Ponencia ponAprobada = Ponencia.builder()
                .idPonencia(3L)
                .convocatoria(convAbierta)
                .idUsuario(42L)
                .estado(estadoAprobado)
                .tituloPonencia("Aprobada")
                .resumen("Resumen")
                .build();

        when(estadoRepositorio.findById(EstadoPonenciaEnum.APROBADO.getId()))
                .thenReturn(Optional.of(estadoAprobado));
        when(ponenciaRepositorio.findByConvocatoria_IdCongresoAndEstado(
                10L,
                estadoAprobado
        )).thenReturn(List.of(ponAprobada));

        List<PonenciaResponse> resultado =
                servicio.listarAprobadasPorCongreso(10L);

        assertEquals(1, resultado.size());
        assertEquals("APROBADO", resultado.get(0).getNombreEstado());
    }

    @Test
    void listarAprobadasPorCongresoSinResultadosRetornaListaVacia() {
        EstadoPonencia estadoAprobado = EstadoPonencia.builder()
                .idEstado(EstadoPonenciaEnum.APROBADO.getId())
                .nombreEstado("APROBADO")
                .build();

        when(estadoRepositorio.findById(EstadoPonenciaEnum.APROBADO.getId()))
                .thenReturn(Optional.of(estadoAprobado));
        when(ponenciaRepositorio.findByConvocatoria_IdCongresoAndEstado(
                99L,
                estadoAprobado
        )).thenReturn(List.of());

        List<PonenciaResponse> resultado =
                servicio.listarAprobadasPorCongreso(99L);

        assertTrue(resultado.isEmpty());
    }
}