package com.eiler.microservicio_4_convocatorias_ponencias.seguridad;

import com.eiler.microservicio_4_convocatorias_ponencias.servicios.convocatoria.ConvocatoriaServicio;
import com.eiler.microservicio_4_convocatorias_ponencias.servicios.estadoPonencia.EstadoPonenciaServicio;
import com.eiler.microservicio_4_convocatorias_ponencias.servicios.evaluacionPonencia.EvaluacionPonenciaServicio;
import com.eiler.microservicio_4_convocatorias_ponencias.servicios.ponencias.PonenciaServicio;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertTrue;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
@Import({ConfiguracionSeguridad.class, FiltroAutenticacionGateway.class})
class ConfiguracionSeguridadTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ConvocatoriaServicio convocatoriaServicio;

    @MockitoBean
    private EstadoPonenciaServicio estadoPonenciaServicio;
    
    @MockitoBean
    private PonenciaServicio ponenciaServicio;
    
    @MockitoBean
    private EvaluacionPonenciaServicio evaluacionPonenciaServicio;
    
    
    @Test
    void rutaProtegida_sinHeaders_retorna401() throws Exception {
        mockMvc.perform(get("/api/v1/convocatorias/1"))
                .andExpect(status().isUnauthorized());  // 401
    }

    @Test
    void rutaProtegida_sinHeaders_listar_retorna401() throws Exception {
        mockMvc.perform(get("/api/v1/convocatorias/congreso/10"))
                .andExpect(status().isUnauthorized());  // 401
    }

    @Test
    void rutaProtegida_sinHeaders_estados_retorna401() throws Exception {
        mockMvc.perform(get("/api/v1/estados-ponencia"))
                .andExpect(status().isUnauthorized());  // 401
    }

    @Test
    void rutaProtegida_conHeadersParticipante_pasaFiltro() throws Exception {
        mockMvc.perform(get("/api/v1/convocatorias/1")
                .header("X-User-Id",   "42")
                .header("X-User-Role", "PARTICIPANTE"))
                .andExpect(status().isOk());  // 200 porque el mock retorna null
    }

    @Test
    void rutaProtegida_conHeadersAdminCongreso_pasaFiltro() throws Exception {
        mockMvc.perform(get("/api/v1/convocatorias/congreso/10")
                .header("X-User-Id",   "1")
                .header("X-User-Role", "ADMIN_CONGRESO"))
                .andExpect(status().isOk());
    }

    @Test
    void rutaProtegida_conHeadersAdminSistema_pasaFiltro() throws Exception {
        mockMvc.perform(get("/api/v1/estados-ponencia")
                .header("X-User-Id",   "1")
                .header("X-User-Role", "ADMIN_SISTEMA"))
                .andExpect(status().isOk());
    }

    @Test
    void sesion_esStateless_noGeneraCookie() throws Exception {
        mockMvc.perform(get("/api/v1/convocatorias/1")
                .header("X-User-Id",   "42")
                .header("X-User-Role", "PARTICIPANTE"))
                .andExpect(result -> {
                    String cookie = result.getResponse().getHeader("Set-Cookie");
                    assertTrue(
                        cookie == null || !cookie.contains("JSESSIONID"),
                        "No debe generar JSESSIONID"
                    );
                });
    }
}