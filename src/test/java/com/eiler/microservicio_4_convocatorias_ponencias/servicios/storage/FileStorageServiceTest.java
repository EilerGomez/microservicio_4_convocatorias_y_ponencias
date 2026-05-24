/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.eiler.microservicio_4_convocatorias_ponencias.servicios.storage;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockMultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import com.eiler.microservicio_4_convocatorias_ponencias.storage.FileStorageService;
/**
 *
 * @author eiler
 */


class FileStorageServiceTest {

    @TempDir
    Path tempDir;

    @Test
    void storeConArchivoValidoGuardaArchivoYRetornaUrlPublica() throws Exception {
        FileStorageService service = new FileStorageService(
                tempDir.toString(),
                "http://localhost:8085/api/v1/files"
        );

        MockMultipartFile file = new MockMultipartFile(
                "archivo",
                "ponencia.pdf",
                "application/pdf",
                "contenido del pdf".getBytes()
        );

        String url = service.store(file);

        assertNotNull(url);
        assertTrue(url.startsWith("http://localhost:8085/api/v1/files/"));
        assertTrue(url.endsWith(".pdf"));

        List<Path> archivos = Files.list(tempDir).toList();

        assertEquals(1, archivos.size());
        assertTrue(Files.exists(archivos.get(0)));
        assertEquals("contenido del pdf", Files.readString(archivos.get(0)));
    }

    @Test
    void storeConArchivoNullRetornaNull() {
        FileStorageService service = new FileStorageService(
                tempDir.toString(),
                "http://localhost:8085/api/v1/files"
        );

        String url = service.store(null);

        assertNull(url);
    }

    @Test
    void storeConArchivoVacioRetornaNull() {
        FileStorageService service = new FileStorageService(
                tempDir.toString(),
                "http://localhost:8085/api/v1/files"
        );

        MockMultipartFile file = new MockMultipartFile(
                "archivo",
                "vacio.pdf",
                "application/pdf",
                new byte[0]
        );

        String url = service.store(file);

        assertNull(url);
    }

    @Test
    void storeSinExtensionGuardaArchivoSinExtension() throws Exception {
        FileStorageService service = new FileStorageService(
                tempDir.toString(),
                "http://localhost:8085/api/v1/files"
        );

        MockMultipartFile file = new MockMultipartFile(
                "archivo",
                "ponencia",
                "application/octet-stream",
                "contenido".getBytes()
        );

        String url = service.store(file);

        assertNotNull(url);
        assertTrue(url.startsWith("http://localhost:8085/api/v1/files/"));

        List<Path> archivos = Files.list(tempDir).toList();

        assertEquals(1, archivos.size());
        assertFalse(archivos.get(0).getFileName().toString().contains("."));
    }

    @Test
    void loadAsResourceConArchivoExistenteRetornaResource() throws Exception {
        FileStorageService service = new FileStorageService(
                tempDir.toString(),
                "http://localhost:8085/api/v1/files"
        );

        Path archivo = tempDir.resolve("archivo.pdf");
        Files.writeString(archivo, "contenido");

        Resource resource = service.loadAsResource("archivo.pdf");

        assertNotNull(resource);
        assertTrue(resource.exists());
        assertTrue(resource.isReadable());
        assertEquals("archivo.pdf", resource.getFilename());
    }

    @Test
    void loadAsResourceConArchivoInexistenteLanzaIllegalArgumentException() {
        FileStorageService service = new FileStorageService(
                tempDir.toString(),
                "http://localhost:8085/api/v1/files"
        );

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> service.loadAsResource("no-existe.pdf")
        );

        assertEquals("Archivo no encontrado", ex.getMessage());
    }

    @Test
    void loadAsResourceConPathTraversalLanzaIllegalArgumentException() {
        FileStorageService service = new FileStorageService(
                tempDir.toString(),
                "http://localhost:8085/api/v1/files"
        );

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> service.loadAsResource("../archivo.pdf")
        );

        assertEquals("Archivo inválido", ex.getMessage());
    }

    @Test
    void buildPublicUrlCodificaNombreConEspacios() {
        FileStorageService service = new FileStorageService(
                tempDir.toString(),
                "http://localhost:8085/api/v1/files/"
        );

        String url = service.buildPublicUrl("mi archivo.pdf");

        assertEquals(
                "http://localhost:8085/api/v1/files/mi%20archivo.pdf",
                url
        );
    }

    @Test
    void constructorEliminaSlashFinalDePublicBaseUrl() {
        FileStorageService service = new FileStorageService(
                tempDir.toString(),
                "http://localhost:8085/api/v1/files/"
        );

        String url = service.buildPublicUrl("archivo.pdf");

        assertEquals(
                "http://localhost:8085/api/v1/files/archivo.pdf",
                url
        );
    }

    @Test
    void storeCuandoNoPuedeGuardarArchivoLanzaIllegalStateException() throws Exception {
        Path rutaQueEsArchivo = tempDir.resolve("uploads");
        Files.writeString(rutaQueEsArchivo, "no soy carpeta");

        FileStorageService service = new FileStorageService(
                rutaQueEsArchivo.toString(),
                "http://localhost:8085/api/v1/files"
        );

        MockMultipartFile file = new MockMultipartFile(
                "archivo",
                "ponencia.pdf",
                "application/pdf",
                "contenido".getBytes()
        );

        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> service.store(file)
        );

        assertEquals("No se pudo guardar el archivo", ex.getMessage());
    }
}