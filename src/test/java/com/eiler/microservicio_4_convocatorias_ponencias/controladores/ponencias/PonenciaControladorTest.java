package com.eiler.microservicio_4_convocatorias_ponencias.controladores.ponencias;

import com.eiler.microservicio_4_convocatorias_ponencias.dtos.ponencias.PonenciaRequest;
import com.eiler.microservicio_4_convocatorias_ponencias.dtos.ponencias.PonenciaResponse;
import com.eiler.microservicio_4_convocatorias_ponencias.excepciones.RecursoNoEncontradoException;
import com.eiler.microservicio_4_convocatorias_ponencias.servicios.ponencias.PonenciaServicio;
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
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PonenciaControladorTest {

    @Mock
    private PonenciaServicio servicio;

    @InjectMocks
    private PonenciaControlador controlador;

    private PonenciaRequest req;
    private PonenciaResponse resPendiente;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        "42",
                        null,
                        List.of(new SimpleGrantedAuthority("ROLE_PARTICIPANTE"))
                )
        );

        req = PonenciaRequest.builder()
                .idConvocatoria(1L)
                .idTipoActividad(1)
                .tituloPonencia("Mi ponencia")
                .resumen("Resumen")
                .urlArchivo("http://localhost:8081/api/v1/files/ponencia.pdf")
                .build();

        resPendiente = PonenciaResponse.builder()
                .idPonencia(1L)
                .idConvocatoria(1L)
                .idUsuario(42L)
                .nombreEstado("PENDIENTE")
                .tituloPonencia("Mi ponencia")
                .build();
    }

    @AfterEach
    void limpiarContexto() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void enviarRetorna201() throws RecursoNoEncontradoException {
        when(servicio.enviar(
                any(PonenciaRequest.class),
                eq(42L),
                eq("http://localhost:8081/api/v1/files/ponencia.pdf")
        )).thenReturn(resPendiente);

        ResponseEntity<PonenciaResponse> r = controlador.enviar(req);

        assertEquals(HttpStatus.CREATED, r.getStatusCode());
        assertNotNull(r.getBody());
        assertEquals("PENDIENTE", r.getBody().getNombreEstado());

        verify(servicio).enviar(
                any(PonenciaRequest.class),
                eq(42L),
                eq("http://localhost:8081/api/v1/files/ponencia.pdf")
        );
    }

    @Test
    void enviarSinUrlArchivoRetorna201() throws RecursoNoEncontradoException {
        PonenciaRequest requestSinArchivo = PonenciaRequest.builder()
                .idConvocatoria(1L)
                .idTipoActividad(1)
                .tituloPonencia("Mi ponencia")
                .resumen("Resumen")
                .urlArchivo(null)
                .build();

        when(servicio.enviar(
                any(PonenciaRequest.class),
                eq(42L),
                isNull()
        )).thenReturn(resPendiente);

        ResponseEntity<PonenciaResponse> r = controlador.enviar(requestSinArchivo);

        assertEquals(HttpStatus.CREATED, r.getStatusCode());
        assertNotNull(r.getBody());
        assertEquals("PENDIENTE", r.getBody().getNombreEstado());

        verify(servicio).enviar(
                any(PonenciaRequest.class),
                eq(42L),
                isNull()
        );
    }

    @Test
    void enviarConvocatoriaCerradaPropagaExcepcion() throws RecursoNoEncontradoException {
        when(servicio.enviar(
                any(PonenciaRequest.class),
                eq(42L),
                eq("http://localhost:8081/api/v1/files/ponencia.pdf")
        )).thenThrow(new IllegalStateException("cerrada"));

        assertThrows(
                IllegalStateException.class,
                () -> controlador.enviar(req)
        );

        verify(servicio).enviar(
                any(PonenciaRequest.class),
                eq(42L),
                eq("http://localhost:8081/api/v1/files/ponencia.pdf")
        );
    }

    @Test
    void obtenerPorIdExisteRetorna200() throws RecursoNoEncontradoException {
        when(servicio.obtenerPorId(1L)).thenReturn(resPendiente);

        ResponseEntity<PonenciaResponse> r = controlador.obtenerPorId(1L);

        assertEquals(HttpStatus.OK, r.getStatusCode());
        assertNotNull(r.getBody());
        assertEquals(1L, r.getBody().getIdPonencia());

        verify(servicio).obtenerPorId(1L);
    }

    @Test
    void obtenerPorIdNoExisteLanzaExcepcion() throws RecursoNoEncontradoException {
        when(servicio.obtenerPorId(99L))
                .thenThrow(new RecursoNoEncontradoException("99"));

        assertThrows(
                RecursoNoEncontradoException.class,
                () -> controlador.obtenerPorId(99L)
        );

        verify(servicio).obtenerPorId(99L);
    }

    @Test
    void listarPorConvocatoriaRetorna200() {
        when(servicio.listarPorConvocatoria(1L))
                .thenReturn(List.of(resPendiente));

        ResponseEntity<List<PonenciaResponse>> r =
                controlador.listarPorConvocatoria(1L);

        assertEquals(HttpStatus.OK, r.getStatusCode());
        assertNotNull(r.getBody());
        assertEquals(1, r.getBody().size());

        verify(servicio).listarPorConvocatoria(1L);
    }

    @Test
    void listarPorConvocatoriaVaciaRetornaListaVacia() {
        when(servicio.listarPorConvocatoria(99L))
                .thenReturn(List.of());

        ResponseEntity<List<PonenciaResponse>> r =
                controlador.listarPorConvocatoria(99L);

        assertEquals(HttpStatus.OK, r.getStatusCode());
        assertNotNull(r.getBody());
        assertTrue(r.getBody().isEmpty());

        verify(servicio).listarPorConvocatoria(99L);
    }

    @Test
    void listarMisPonenciasRetorna200() {
        when(servicio.listarMisPonencias(42L))
                .thenReturn(List.of(resPendiente));

        ResponseEntity<List<PonenciaResponse>> r =
                controlador.listarMisPonencias();

        assertEquals(HttpStatus.OK, r.getStatusCode());
        assertNotNull(r.getBody());
        assertEquals(42L, r.getBody().get(0).getIdUsuario());

        verify(servicio).listarMisPonencias(42L);
    }

    @Test
    void reenviarExitosoRetorna200() throws RecursoNoEncontradoException {
        when(servicio.reenviar(
                eq(2L),
                any(PonenciaRequest.class),
                eq(42L),
                eq("http://localhost:8081/api/v1/files/ponencia.pdf")
        )).thenReturn(resPendiente);

        ResponseEntity<PonenciaResponse> r =
                controlador.reenviar(2L, req);

        assertEquals(HttpStatus.OK, r.getStatusCode());
        assertNotNull(r.getBody());

        verify(servicio).reenviar(
                eq(2L),
                any(PonenciaRequest.class),
                eq(42L),
                eq("http://localhost:8081/api/v1/files/ponencia.pdf")
        );
    }

    @Test
    void reenviarSinUrlArchivoRetorna200() throws RecursoNoEncontradoException {
        PonenciaRequest requestSinArchivo = PonenciaRequest.builder()
                .idConvocatoria(1L)
                .idTipoActividad(1)
                .tituloPonencia("Mi ponencia corregida")
                .resumen("Resumen corregido")
                .urlArchivo(null)
                .build();

        when(servicio.reenviar(
                eq(2L),
                any(PonenciaRequest.class),
                eq(42L),
                isNull()
        )).thenReturn(resPendiente);

        ResponseEntity<PonenciaResponse> r =
                controlador.reenviar(2L, requestSinArchivo);

        assertEquals(HttpStatus.OK, r.getStatusCode());
        assertNotNull(r.getBody());

        verify(servicio).reenviar(
                eq(2L),
                any(PonenciaRequest.class),
                eq(42L),
                isNull()
        );
    }

    @Test
    void reenviarNoRechazadaPropagaExcepcion() throws RecursoNoEncontradoException {
        when(servicio.reenviar(
                eq(1L),
                any(PonenciaRequest.class),
                eq(42L),
                eq("http://localhost:8081/api/v1/files/ponencia.pdf")
        )).thenThrow(new IllegalStateException("rechazada"));

        assertThrows(
                IllegalStateException.class,
                () -> controlador.reenviar(1L, req)
        );

        verify(servicio).reenviar(
                eq(1L),
                any(PonenciaRequest.class),
                eq(42L),
                eq("http://localhost:8081/api/v1/files/ponencia.pdf")
        );
    }

    @Test
    void reenviarNoExisteLanzaExcepcion() throws RecursoNoEncontradoException {
        when(servicio.reenviar(
                eq(99L),
                any(PonenciaRequest.class),
                eq(42L),
                eq("http://localhost:8081/api/v1/files/ponencia.pdf")
        )).thenThrow(new RecursoNoEncontradoException("99"));

        assertThrows(
                RecursoNoEncontradoException.class,
                () -> controlador.reenviar(99L, req)
        );

        verify(servicio).reenviar(
                eq(99L),
                any(PonenciaRequest.class),
                eq(42L),
                eq("http://localhost:8081/api/v1/files/ponencia.pdf")
        );
    }

    @Test
    void listarAprobadasPorCongresoRetorna200() {
        PonenciaResponse resAprobada = PonenciaResponse.builder()
                .idPonencia(3L)
                .idUsuario(42L)
                .nombreEstado("APROBADO")
                .tituloPonencia("Aprobada")
                .build();

        when(servicio.listarAprobadasPorCongreso(10L))
                .thenReturn(List.of(resAprobada));

        ResponseEntity<List<PonenciaResponse>> r =
                controlador.listarAprobadasPorCongreso(10L);

        assertEquals(HttpStatus.OK, r.getStatusCode());
        assertNotNull(r.getBody());
        assertEquals(1, r.getBody().size());
        assertEquals("APROBADO", r.getBody().get(0).getNombreEstado());

        verify(servicio).listarAprobadasPorCongreso(10L);
    }

    @Test
    void listarAprobadasPorCongresoVacioRetornaListaVacia() {
        when(servicio.listarAprobadasPorCongreso(99L))
                .thenReturn(List.of());

        ResponseEntity<List<PonenciaResponse>> r =
                controlador.listarAprobadasPorCongreso(99L);

        assertEquals(HttpStatus.OK, r.getStatusCode());
        assertNotNull(r.getBody());
        assertTrue(r.getBody().isEmpty());

        verify(servicio).listarAprobadasPorCongreso(99L);
    }
}