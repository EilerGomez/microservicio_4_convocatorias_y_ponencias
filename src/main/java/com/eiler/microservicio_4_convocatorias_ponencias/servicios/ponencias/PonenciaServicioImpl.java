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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
// import org.springframework.beans.factory.annotation.Value;
// import org.springframework.http.HttpEntity;
// import org.springframework.http.HttpHeaders;
// import org.springframework.http.HttpMethod;
// import org.springframework.http.ResponseEntity;
// import org.springframework.security.core.context.SecurityContextHolder;
// import org.springframework.web.client.RestTemplate;
 
@Service
public class PonenciaServicioImpl implements PonenciaServicio {
    // @Value("${gateway.base-url}")
    // private String gatewayBaseUrl;
    // private final RestTemplate restTemplate;

    private final PonenciaRepositorio       ponenciaRepositorio;
    private final ConvocatoriaRepositorio   convocatoriaRepositorio;
    private final EstadoPonenciaRepositorio estadoRepositorio;

    public PonenciaServicioImpl(PonenciaRepositorio ponenciaRepositorio,
                                ConvocatoriaRepositorio convocatoriaRepositorio,
                                EstadoPonenciaRepositorio estadoRepositorio) {
        this.ponenciaRepositorio     = ponenciaRepositorio;
        this.convocatoriaRepositorio = convocatoriaRepositorio;
        this.estadoRepositorio       = estadoRepositorio;
    }
    //
    // private HttpEntity<Void> requestConJwt() {
    //     String token = (String) SecurityContextHolder.getContext()
    //                         .getAuthentication().getCredentials();
    //     HttpHeaders headers = new HttpHeaders();
    //     headers.setBearerAuth(token);
    //     return new HttpEntity<>(headers);
    // }
 


    @Override
    @Transactional(rollbackFor = Exception.class)
    public PonenciaResponse enviar(PonenciaRequest request,
                                   Long idUsuario,
                                   String urlArchivo)
            throws RecursoNoEncontradoException {
        
        /*String url = gatewayBaseUrl + "/api/v1/users/" + idUsuario;
        ResponseEntity<Object> userResp = restTemplate.exchange(
            url, HttpMethod.GET, requestConJwt(), Object.class);
        if (!userResp.getStatusCode().is2xxSuccessful()) {
            throw new RecursoNoEncontradoException("Usuario con ID " + idUsuario + " no encontrado");
        }
 
        Convocatoria conv = convocatoriaRepositorio.findById(request.idConvocatoria()).orElseThrow(...);
        String urlCongreso = gatewayBaseUrl + "/api/v1/conferences/" + conv.getIdCongreso();
        ResponseEntity<Object> congResp = restTemplate.exchange(
            urlCongreso, HttpMethod.GET, requestConJwt(), Object.class);
        if (!congResp.getStatusCode().is2xxSuccessful()) {
            throw new IllegalStateException("El congreso no está activo o no existe");
        }*/

        Convocatoria convocatoria = obtenerConvocatoriaAbierta(request.idConvocatoria());

        boolean tieneActiva = ponenciaRepositorio
                .findByConvocatoriaIdConvocatoriaAndIdUsuario(request.idConvocatoria(), idUsuario)
                .stream()
                .anyMatch(p -> !p.getEstado().getIdEstado()
                        .equals(EstadoPonenciaEnum.RECHAZADO.getId()));

        if (tieneActiva) {
            throw new IllegalStateException(
                    "Ya tienes una ponencia activa en esta convocatoria. " +
                    "Solo puedes reenviar si fue rechazada.");
        }

        EstadoPonencia estadoPendiente = obtenerEstado(EstadoPonenciaEnum.PENDIENTE);

        Ponencia ponencia = Ponencia.builder()
                .convocatoria(convocatoria)
                .idUsuario(idUsuario)
                .idTipoActividad(request.idTipoActividad())
                .estado(estadoPendiente)
                .tituloPonencia(request.tituloPonencia())
                .resumen(request.resumen())
                .urlArchivo(urlArchivo)
                .build();

        return new PonenciaResponse(ponenciaRepositorio.save(ponencia));
    }

    @Override
    @Transactional(readOnly = true)
    public PonenciaResponse obtenerPorId(Long id) throws RecursoNoEncontradoException {
        return ponenciaRepositorio.findById(id)
                .map(PonenciaResponse::new)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Ponencia con ID " + id + " no encontrada"));
    }

    @Override
    @Transactional(readOnly = true)
    public List<PonenciaResponse> listarPorConvocatoria(Long idConvocatoria) {
        return ponenciaRepositorio.findByConvocatoriaIdConvocatoria(idConvocatoria)
                .stream().map(PonenciaResponse::new).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PonenciaResponse> listarMisPonencias(Long idUsuario) {
        return ponenciaRepositorio.findByIdUsuario(idUsuario)
                .stream().map(PonenciaResponse::new).collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PonenciaResponse reenviar(Long idPonencia,
                                     PonenciaRequest request,
                                     Long idUsuario,
                                     String urlArchivo)
            throws RecursoNoEncontradoException {

        Ponencia ponencia = obtenerPonencia(idPonencia);

        if (!ponencia.getIdUsuario().equals(idUsuario))
            throw new IllegalStateException("No puedes modificar una ponencia que no es tuya");

        if (!ponencia.getEstado().getIdEstado().equals(EstadoPonenciaEnum.RECHAZADO.getId()))
            throw new IllegalStateException("Solo puedes reenviar una ponencia que fue rechazada");

        obtenerConvocatoriaAbierta(ponencia.getConvocatoria().getIdConvocatoria());

        ponencia.setTituloPonencia(request.tituloPonencia());
        ponencia.setResumen(request.resumen());
        ponencia.setIdTipoActividad(request.idTipoActividad());
        ponencia.setEstado(obtenerEstado(EstadoPonenciaEnum.PENDIENTE));

        if (urlArchivo != null && !urlArchivo.isBlank())
            ponencia.setUrlArchivo(urlArchivo);

        return new PonenciaResponse(ponenciaRepositorio.save(ponencia));
    }

    @Override
    @Transactional(readOnly = true)
    public List<PonenciaResponse> listarAprobadasPorCongreso(Long idCongreso) {
        EstadoPonencia estadoAprobado = obtenerEstado(EstadoPonenciaEnum.APROBADO);
        return ponenciaRepositorio
                .findByConvocatoria_IdCongresoAndEstado(idCongreso, estadoAprobado)
                .stream().map(PonenciaResponse::new).collect(Collectors.toList());
    }

    private Ponencia obtenerPonencia(Long id) throws RecursoNoEncontradoException {
        return ponenciaRepositorio.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Ponencia con ID " + id + " no encontrada"));
    }

    private Convocatoria obtenerConvocatoriaAbierta(Long idConvocatoria)
            throws RecursoNoEncontradoException {
        Convocatoria conv = convocatoriaRepositorio.findById(idConvocatoria)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Convocatoria con ID " + idConvocatoria + " no encontrada"));
        if (!conv.getEstaAbierta())
            throw new IllegalStateException("La convocatoria con ID " + idConvocatoria + " está cerrada");
        return conv;
    }

    private EstadoPonencia obtenerEstado(EstadoPonenciaEnum estadoEnum) {
        return estadoRepositorio.findById(estadoEnum.getId())
                .orElseThrow(() -> new IllegalStateException(
                        "Estado " + estadoEnum.name() + " no encontrado en catálogo"));
    }
}