/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.eiler.microservicio_4_convocatorias_ponencias.servicios.evaluacionPonencia;

/**
 *
 * @author eiler
 */


import com.eiler.microservicio_4_convocatorias_ponencias.dtos.evaluacionPonencia.EvaluacionPonenciaRequest;
import com.eiler.microservicio_4_convocatorias_ponencias.dtos.evaluacionPonencia.EvaluacionPonenciaResponse;
import com.eiler.microservicio_4_convocatorias_ponencias.excepciones.RecursoNoEncontradoException;
import com.eiler.microservicio_4_convocatorias_ponencias.modelos.estadoPonencia.EstadoPonencia;
import com.eiler.microservicio_4_convocatorias_ponencias.modelos.evaluacionPonencia.EvaluacionPonencia;
import com.eiler.microservicio_4_convocatorias_ponencias.modelos.ponencias.Ponencia;
// import org.springframework.beans.factory.annotation.Value;
// import org.springframework.http.HttpEntity;
// import org.springframework.http.HttpHeaders;
// import org.springframework.http.HttpMethod;
import com.eiler.microservicio_4_convocatorias_ponencias.repositorios.estadoponencia.EstadoPonenciaRepositorio;
import com.eiler.microservicio_4_convocatorias_ponencias.repositorios.evaluacionPonencia.EvaluacionPonenciaRepositorio;
import com.eiler.microservicio_4_convocatorias_ponencias.repositorios.ponencias.PonenciaRepositorio;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;
// import org.springframework.http.ResponseEntity;
// import org.springframework.security.core.context.SecurityContextHolder;
// import org.springframework.web.client.RestTemplate;
 

@Service
public class EvaluacionPonenciaServicioImpl implements EvaluacionPonenciaServicio {
    
    // @Value("${gateway.base-url}")
    // private String gatewayBaseUrl;
    // private final RestTemplate restTemplate;


    private static final Long ESTADO_PENDIENTE = 1L;
    private static final Long ESTADO_APROBADO  = 2L;
    private static final Long ESTADO_RECHAZADO = 3L;

    private final EvaluacionPonenciaRepositorio evaluacionRepositorio;
    private final PonenciaRepositorio           ponenciaRepositorio;
    private final EstadoPonenciaRepositorio     estadoRepositorio;

    public EvaluacionPonenciaServicioImpl(
            EvaluacionPonenciaRepositorio evaluacionRepositorio,
            PonenciaRepositorio ponenciaRepositorio,
            EstadoPonenciaRepositorio estadoRepositorio) {
        this.evaluacionRepositorio = evaluacionRepositorio;
        this.ponenciaRepositorio   = ponenciaRepositorio;
        this.estadoRepositorio     = estadoRepositorio;
    }

    // private HttpEntity<Void> requestConJwt() {
    //     String token = (String) SecurityContextHolder.getContext()
    //                         .getAuthentication().getCredentials();
    //     HttpHeaders headers = new HttpHeaders();
    //     headers.setBearerAuth(token);
    //     return new HttpEntity<>(headers);
    // }
 

    @Override
    @Transactional(rollbackFor = Exception.class)
    public EvaluacionPonenciaResponse evaluar(EvaluacionPonenciaRequest request,
                                              Long idEvaluador)
            throws RecursoNoEncontradoException {


        /*
        Long idCongreso = ponencia.getConvocatoria().getIdCongreso();
        tring urlComite = gatewayBaseUrl + "/api/v1/scientific-committees"
                           + "?conferenceId=" + idCongreso + "&userId=" + idEvaluador;
        esponseEntity<List> comiteResp = restTemplate.exchange(
             urlComite, HttpMethod.GET, requestConJwt(), List.class);
         boolean esComite = comiteResp.getStatusCode().is2xxSuccessful()
                            && comiteResp.getBody() != null
                            && !comiteResp.getBody().isEmpty();
         if (!esComite) {
             throw new IllegalStateException(
                 "No perteneces al comité científico de este congreso");
         }
 
         String urlUser = gatewayBaseUrl + "/api/v1/users/" + idEvaluador;
         ResponseEntity<Object> userResp = restTemplate.exchange(
             urlUser, HttpMethod.GET, requestConJwt(), Object.class);
         if (!userResp.getStatusCode().is2xxSuccessful()) {
             throw new RecursoNoEncontradoException(
                 "Evaluador con ID " + idEvaluador + " no encontrado");
         }*/


        Ponencia ponencia = ponenciaRepositorio.findById(request.getIdPonencia())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Ponencia con ID " + request.getIdPonencia() + " no encontrada"));

        if (!ponencia.getEstado().getIdEstado().equals(ESTADO_PENDIENTE)) {
            throw new IllegalStateException(
                    "Solo se pueden evaluar ponencias en estado PENDIENTE");
        }

        if (Boolean.FALSE.equals(request.getEstaAprobado())
                && (request.getComentarios() == null
                    || request.getComentarios().isBlank())) {
            throw new IllegalArgumentException(
                    "Los comentarios son obligatorios al rechazar una ponencia");
        }

        Long nuevoEstadoId = Boolean.TRUE.equals(request.getEstaAprobado())
                ? ESTADO_APROBADO
                : ESTADO_RECHAZADO;

        EstadoPonencia nuevoEstado = estadoRepositorio.findById(nuevoEstadoId)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Estado con ID " + nuevoEstadoId + " no encontrado"));

        // Actualiza el estado de la ponencia
        ponencia.setEstado(nuevoEstado);
        ponenciaRepositorio.save(ponencia);

        // Crea el registro de evaluación
        EvaluacionPonencia evaluacion = EvaluacionPonencia.builder()
                .ponencia(ponencia)
                .idEvaluador(idEvaluador)
                .comentarios(request.getComentarios())
                .estaAprobado(request.getEstaAprobado())
                .build();

        return new EvaluacionPonenciaResponse(evaluacionRepositorio.save(evaluacion));
    }

    @Override
    @Transactional(readOnly = true)
    public EvaluacionPonenciaResponse obtenerPorId(Long id)
            throws RecursoNoEncontradoException {
        return evaluacionRepositorio.findById(id)
                .map(EvaluacionPonenciaResponse::new)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Evaluación con ID " + id + " no encontrada"));
    }

    @Override
    @Transactional(readOnly = true)
    public List<EvaluacionPonenciaResponse> listarPorPonencia(Long idPonencia) {
        return evaluacionRepositorio.findByPonencia_IdPonencia(idPonencia)
                .stream()
                .map(EvaluacionPonenciaResponse::new)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<EvaluacionPonenciaResponse> listarPorEvaluador(Long idEvaluador) {
        return evaluacionRepositorio.findByIdEvaluador(idEvaluador)
                .stream()
                .map(EvaluacionPonenciaResponse::new)
                .collect(Collectors.toList());
    }
}