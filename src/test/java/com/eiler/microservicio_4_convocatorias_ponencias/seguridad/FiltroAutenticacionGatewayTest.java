/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.eiler.microservicio_4_convocatorias_ponencias.seguridad;

/**
 *
 * @author eiler
 */


import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FiltroAutenticacionGatewayTest {

    @InjectMocks
    private FiltroAutenticacionGateway filtro;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @AfterEach
    void limpiarContexto() {
        SecurityContextHolder.clearContext();
    }


    @Test
    void conHeadersValidos_estableceAutenticacion() throws Exception {
        when(request.getHeader("X-User-Id")).thenReturn("42");
        when(request.getHeader("X-User-Role")).thenReturn("ADMIN_CONGRESO");

        filtro.doFilterInternal(request, response, filterChain);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(auth);
        assertTrue(auth.isAuthenticated());
        assertEquals("42", auth.getPrincipal());
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    void conHeadersValidos_asignaRolCorrectamente() throws Exception {
        when(request.getHeader("X-User-Id")).thenReturn("1");
        when(request.getHeader("X-User-Role")).thenReturn("ADMIN_SISTEMA");

        filtro.doFilterInternal(request, response, filterChain);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(auth);
        assertTrue(auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN_SISTEMA")));
    }

    @Test
    void conRolParticipante_asignaRolParticipante() throws Exception {
        when(request.getHeader("X-User-Id")).thenReturn("99");
        when(request.getHeader("X-User-Role")).thenReturn("PARTICIPANTE");

        filtro.doFilterInternal(request, response, filterChain);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(auth);
        assertTrue(auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_PARTICIPANTE")));
        assertEquals("99", auth.getPrincipal());
    }


    @Test
    void sinHeaders_noEstableceAutenticacion() throws Exception {
        when(request.getHeader("X-User-Id")).thenReturn(null);
        when(request.getHeader("X-User-Role")).thenReturn(null);

        filtro.doFilterInternal(request, response, filterChain);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertNull(auth);
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    void soloUserId_sinRole_noEstableceAutenticacion() throws Exception {
        when(request.getHeader("X-User-Id")).thenReturn("42");
        when(request.getHeader("X-User-Role")).thenReturn(null);

        filtro.doFilterInternal(request, response, filterChain);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertNull(auth);
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    void soloRole_sinUserId_noEstableceAutenticacion() throws Exception {
        when(request.getHeader("X-User-Id")).thenReturn(null);
        when(request.getHeader("X-User-Role")).thenReturn("ADMIN_CONGRESO");

        filtro.doFilterInternal(request, response, filterChain);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertNull(auth);
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    void conHeaders_siempreContinuaCadena() throws Exception {
        when(request.getHeader("X-User-Id")).thenReturn("42");
        when(request.getHeader("X-User-Role")).thenReturn("PARTICIPANTE");

        filtro.doFilterInternal(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    void sinHeaders_siempreContinuaCadena() throws Exception {
        when(request.getHeader("X-User-Id")).thenReturn(null);
        when(request.getHeader("X-User-Role")).thenReturn(null);

        filtro.doFilterInternal(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
    }


    @Test
    void rol_tienePrefijoROLE_agregado() throws Exception {
        when(request.getHeader("X-User-Id")).thenReturn("1");
        when(request.getHeader("X-User-Role")).thenReturn("ADMIN_CONGRESO");

        filtro.doFilterInternal(request, response, filterChain);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String autoridad = auth.getAuthorities().iterator().next().getAuthority();

        assertTrue(autoridad.startsWith("ROLE_"),
                "La autoridad debe tener prefijo ROLE_");
        assertEquals("ROLE_ADMIN_CONGRESO", autoridad);
    }
}