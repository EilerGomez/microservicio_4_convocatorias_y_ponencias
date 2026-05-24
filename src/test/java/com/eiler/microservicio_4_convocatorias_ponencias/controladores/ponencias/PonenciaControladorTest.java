package com.eiler.microservicio_4_convocatorias_ponencias.controladores.ponencias;

import com.eiler.microservicio_4_convocatorias_ponencias.dtos.ponencias.PonenciaRequest;
import com.eiler.microservicio_4_convocatorias_ponencias.dtos.ponencias.PonenciaResponse;
import com.eiler.microservicio_4_convocatorias_ponencias.excepciones.RecursoNoEncontradoException;
import com.eiler.microservicio_4_convocatorias_ponencias.servicios.ponencias.PonenciaServicio;
import com.eiler.microservicio_4_convocatorias_ponencias.storage.FileStorageService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
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

    @Mock
    private FileStorageService fileStorageService;

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

        req = new PonenciaRequest(
                1L,
                1,
                "Mi ponencia",
                "Resumen"
        );

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
    void enviarRetorna201SinArchivo() throws RecursoNoEncontradoException {
        when(servicio.enviar(any(PonenciaRequest.class), eq(42L), isNull()))
                .thenReturn(resPendiente);

        ResponseEntity<PonenciaResponse> r = controlador.enviar(req, null);

        assertEquals(HttpStatus.CREATED, r.getStatusCode());
        assertNotNull(r.getBody());
        assertEquals("PENDIENTE", r.getBody().getNombreEstado());

        verify(servicio).enviar(any(PonenciaRequest.class), eq(42L), isNull());
        verifyNoInteractions(fileStorageService);
    }

    @Test
    void enviarRetorna201ConArchivo() throws RecursoNoEncontradoException {
        MockMultipartFile archivo = new MockMultipartFile(
                "archivo",
                "ponencia.pdf",
                "application/pdf",
                "contenido".getBytes()
        );

        when(fileStorageService.store(archivo))
                .thenReturn("http://localhost/uploads/ponencia.pdf");

        when(servicio.enviar(
                any(PonenciaRequest.class),
                eq(42L),
                eq("http://localhost/uploads/ponencia.pdf")
        )).thenReturn(resPendiente);

        ResponseEntity<PonenciaResponse> r = controlador.enviar(req, archivo);

        assertEquals(HttpStatus.CREATED, r.getStatusCode());
        assertNotNull(r.getBody());
        assertEquals("PENDIENTE", r.getBody().getNombreEstado());

        verify(fileStorageService).store(archivo);
        verify(servicio).enviar(
                any(PonenciaRequest.class),
                eq(42L),
                eq("http://localhost/uploads/ponencia.pdf")
        );
    }

    @Test
    void enviarConvCerradaPropagaExcepcion() throws RecursoNoEncontradoException {
        when(servicio.enviar(any(PonenciaRequest.class), eq(42L), isNull()))
                .thenThrow(new IllegalStateException("cerrada"));

        assertThrows(
                IllegalStateException.class,
                () -> controlador.enviar(req, null)
        );

        verify(servicio).enviar(any(PonenciaRequest.class), eq(42L), isNull());
        verifyNoInteractions(fileStorageService);
    }

    @Test
    void obtenerPorIdExisteRetorna200() throws RecursoNoEncontradoException {
        when(servicio.obtenerPorId(1L)).thenReturn(resPendiente);

        ResponseEntity<PonenciaResponse> r = controlador.obtenerPorId(1L);

        assertEquals(HttpStatus.OK, r.getStatusCode());
        assertNotNull(r.getBody());
        assertEquals(1L, r.getBody().getIdPonencia());
    }

    @Test
    void obtenerPorIdNoExisteLanzaExcepcion() throws RecursoNoEncontradoException {
        when(servicio.obtenerPorId(99L))
                .thenThrow(new RecursoNoEncontradoException("99"));

        assertThrows(
                RecursoNoEncontradoException.class,
                () -> controlador.obtenerPorId(99L)
        );
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
    }

    @Test
    void reenviarExitosoRetorna200SinArchivo() throws RecursoNoEncontradoException {
        when(servicio.reenviar(eq(2L), any(PonenciaRequest.class), eq(42L), isNull()))
                .thenReturn(resPendiente);

        ResponseEntity<PonenciaResponse> r =
                controlador.reenviar(2L, req, null);

        assertEquals(HttpStatus.OK, r.getStatusCode());
        assertNotNull(r.getBody());

        verify(servicio).reenviar(eq(2L), any(PonenciaRequest.class), eq(42L), isNull());
        verifyNoInteractions(fileStorageService);
    }

    @Test
    void reenviarExitosoRetorna200ConArchivo() throws RecursoNoEncontradoException {
        MockMultipartFile archivo = new MockMultipartFile(
                "archivo",
                "correccion.pdf",
                "application/pdf",
                "contenido corregido".getBytes()
        );

        when(fileStorageService.store(archivo))
                .thenReturn("http://localhost/uploads/correccion.pdf");

        when(servicio.reenviar(
                eq(2L),
                any(PonenciaRequest.class),
                eq(42L),
                eq("http://localhost/uploads/correccion.pdf")
        )).thenReturn(resPendiente);

        ResponseEntity<PonenciaResponse> r =
                controlador.reenviar(2L, req, archivo);

        assertEquals(HttpStatus.OK, r.getStatusCode());
        assertNotNull(r.getBody());

        verify(fileStorageService).store(archivo);
        verify(servicio).reenviar(
                eq(2L),
                any(PonenciaRequest.class),
                eq(42L),
                eq("http://localhost/uploads/correccion.pdf")
        );
    }

    @Test
    void reenviarNoRechazadaPropagaExcepcion() throws RecursoNoEncontradoException {
        when(servicio.reenviar(eq(1L), any(PonenciaRequest.class), eq(42L), isNull()))
                .thenThrow(new IllegalStateException("rechazada"));

        assertThrows(
                IllegalStateException.class,
                () -> controlador.reenviar(1L, req, null)
        );

        verify(servicio).reenviar(eq(1L), any(PonenciaRequest.class), eq(42L), isNull());
        verifyNoInteractions(fileStorageService);
    }

    @Test
    void reenviarNoExisteLanzaExcepcion() throws RecursoNoEncontradoException {
        when(servicio.reenviar(eq(99L), any(PonenciaRequest.class), eq(42L), isNull()))
                .thenThrow(new RecursoNoEncontradoException("99"));

        assertThrows(
                RecursoNoEncontradoException.class,
                () -> controlador.reenviar(99L, req, null)
        );

        verify(servicio).reenviar(eq(99L), any(PonenciaRequest.class), eq(42L), isNull());
        verifyNoInteractions(fileStorageService);
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
    }
}