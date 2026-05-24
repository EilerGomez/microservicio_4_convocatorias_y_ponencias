/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.eiler.microservicio_4_convocatorias_ponencias.controladores.storage;

/**
 *
 * @author eiler
 */

import com.eiler.microservicio_4_convocatorias_ponencias.storage.FileStorageService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FileControllerTest {

    @Mock
    private FileStorageService fileStorageService;

    @InjectMocks
    private FileController controller;

    @Test
    void getFilePdfRetorna200ConHeadersYContentTypePdf() {
        Resource resource = new ByteArrayResource("contenido pdf".getBytes()) {
            @Override
            public String getFilename() {
                return "archivo.pdf";
            }
        };

        when(fileStorageService.loadAsResource("archivo.pdf"))
                .thenReturn(resource);

        ResponseEntity<Resource> response = controller.getFile("archivo.pdf");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(MediaType.APPLICATION_PDF, response.getHeaders().getContentType());
        assertEquals(resource, response.getBody());

        assertEquals(
                "inline; filename=\"archivo.pdf\"",
                response.getHeaders().getFirst(HttpHeaders.CONTENT_DISPOSITION)
        );

        assertEquals(
                "public, max-age=86400",
                response.getHeaders().getFirst(HttpHeaders.CACHE_CONTROL)
        );

        verify(fileStorageService).loadAsResource("archivo.pdf");
    }

    @Test
    void getFileSinExtensionRetornaOctetStream() {
        Resource resource = new ByteArrayResource("contenido".getBytes()) {
            @Override
            public String getFilename() {
                return "archivo";
            }
        };

        when(fileStorageService.loadAsResource("archivo"))
                .thenReturn(resource);

        ResponseEntity<Resource> response = controller.getFile("archivo");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(MediaType.APPLICATION_OCTET_STREAM, response.getHeaders().getContentType());
        assertEquals(resource, response.getBody());

        assertEquals(
                "inline; filename=\"archivo\"",
                response.getHeaders().getFirst(HttpHeaders.CONTENT_DISPOSITION)
        );

        verify(fileStorageService).loadAsResource("archivo");
    }

    @Test
    void getFilePngRetornaContentTypeImagePng() {
        Resource resource = new ByteArrayResource("imagen".getBytes()) {
            @Override
            public String getFilename() {
                return "imagen.png";
            }
        };

        when(fileStorageService.loadAsResource("imagen.png"))
                .thenReturn(resource);

        ResponseEntity<Resource> response = controller.getFile("imagen.png");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(MediaType.IMAGE_PNG, response.getHeaders().getContentType());
        assertEquals(resource, response.getBody());

        verify(fileStorageService).loadAsResource("imagen.png");
    }

    @Test
    void getFileCuandoNoExistePropagaExcepcion() {
        when(fileStorageService.loadAsResource("no-existe.pdf"))
                .thenThrow(new IllegalArgumentException("Archivo no encontrado"));

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> controller.getFile("no-existe.pdf")
        );

        assertEquals("Archivo no encontrado", ex.getMessage());

        verify(fileStorageService).loadAsResource("no-existe.pdf");
    }
}
