package com.eiler.microservicio_4_convocatorias_ponencias.dtos.ponencias;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class PonenciaRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void requestValidoNoTieneErrores() {
        PonenciaRequest request = PonenciaRequest.builder()
                .idConvocatoria(1L)
                .idTipoActividad(1)
                .tituloPonencia("Mi ponencia")
                .resumen("Resumen")
                .urlArchivo("http://localhost/uploads/ponencia.pdf")
                .build();

        Set<ConstraintViolation<PonenciaRequest>> errores =
                validator.validate(request);

        assertTrue(errores.isEmpty());
    }

    @Test
    void idConvocatoriaNullGeneraError() {
        PonenciaRequest request = PonenciaRequest.builder()
                .idConvocatoria(null)
                .idTipoActividad(1)
                .tituloPonencia("Mi ponencia")
                .resumen("Resumen")
                .urlArchivo("http://localhost/uploads/ponencia.pdf")
                .build();

        Set<ConstraintViolation<PonenciaRequest>> errores =
                validator.validate(request);

        assertFalse(errores.isEmpty());
        assertTrue(errores.stream()
                .anyMatch(e -> e.getMessage().equals("El id de convocatoria es obligatorio")));
    }

    @Test
    void idConvocatoriaNegativoGeneraError() {
        PonenciaRequest request = PonenciaRequest.builder()
                .idConvocatoria(-1L)
                .idTipoActividad(1)
                .tituloPonencia("Mi ponencia")
                .resumen("Resumen")
                .urlArchivo("http://localhost/uploads/ponencia.pdf")
                .build();

        Set<ConstraintViolation<PonenciaRequest>> errores =
                validator.validate(request);

        assertFalse(errores.isEmpty());
        assertTrue(errores.stream()
                .anyMatch(e -> e.getPropertyPath().toString().equals("idConvocatoria")));
    }

    @Test
    void idTipoActividadNullGeneraError() {
        PonenciaRequest request = PonenciaRequest.builder()
                .idConvocatoria(1L)
                .idTipoActividad(null)
                .tituloPonencia("Mi ponencia")
                .resumen("Resumen")
                .urlArchivo("http://localhost/uploads/ponencia.pdf")
                .build();

        Set<ConstraintViolation<PonenciaRequest>> errores =
                validator.validate(request);

        assertFalse(errores.isEmpty());
        assertTrue(errores.stream()
                .anyMatch(e -> e.getMessage().equals("El id de tipo actividad es obligatorio")));
    }

    @Test
    void idTipoActividadNegativoGeneraError() {
        PonenciaRequest request = PonenciaRequest.builder()
                .idConvocatoria(1L)
                .idTipoActividad(-1)
                .tituloPonencia("Mi ponencia")
                .resumen("Resumen")
                .urlArchivo("http://localhost/uploads/ponencia.pdf")
                .build();

        Set<ConstraintViolation<PonenciaRequest>> errores =
                validator.validate(request);

        assertFalse(errores.isEmpty());
        assertTrue(errores.stream()
                .anyMatch(e -> e.getPropertyPath().toString().equals("idTipoActividad")));
    }

    @Test
    void tituloNullGeneraError() {
        PonenciaRequest request = PonenciaRequest.builder()
                .idConvocatoria(1L)
                .idTipoActividad(1)
                .tituloPonencia(null)
                .resumen("Resumen")
                .urlArchivo("http://localhost/uploads/ponencia.pdf")
                .build();

        Set<ConstraintViolation<PonenciaRequest>> errores =
                validator.validate(request);

        assertFalse(errores.isEmpty());
        assertTrue(errores.stream()
                .anyMatch(e -> e.getMessage().equals("El título es obligatorio")));
    }

    @Test
    void tituloVacioGeneraError() {
        PonenciaRequest request = PonenciaRequest.builder()
                .idConvocatoria(1L)
                .idTipoActividad(1)
                .tituloPonencia("")
                .resumen("Resumen")
                .urlArchivo("http://localhost/uploads/ponencia.pdf")
                .build();

        Set<ConstraintViolation<PonenciaRequest>> errores =
                validator.validate(request);

        assertFalse(errores.isEmpty());
        assertTrue(errores.stream()
                .anyMatch(e -> e.getMessage().equals("El título es obligatorio")));
    }

    @Test
    void tituloConEspaciosGeneraError() {
        PonenciaRequest request = PonenciaRequest.builder()
                .idConvocatoria(1L)
                .idTipoActividad(1)
                .tituloPonencia("   ")
                .resumen("Resumen")
                .urlArchivo("http://localhost/uploads/ponencia.pdf")
                .build();

        Set<ConstraintViolation<PonenciaRequest>> errores =
                validator.validate(request);

        assertFalse(errores.isEmpty());
        assertTrue(errores.stream()
                .anyMatch(e -> e.getMessage().equals("El título es obligatorio")));
    }

    @Test
    void resumenYUrlArchivoPuedenSerNull() {
        PonenciaRequest request = PonenciaRequest.builder()
                .idConvocatoria(1L)
                .idTipoActividad(1)
                .tituloPonencia("Mi ponencia")
                .resumen(null)
                .urlArchivo(null)
                .build();

        Set<ConstraintViolation<PonenciaRequest>> errores =
                validator.validate(request);

        assertTrue(errores.isEmpty());
    }
}