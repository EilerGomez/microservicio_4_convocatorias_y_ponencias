package com.eiler.microservicio_4_convocatorias_ponencias.storage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileStorageService {

    private final Path storageDirectory;
    private final String publicBaseUrl;

    public FileStorageService(
            @Value("${app.files.storage-path:./uploads}") String storagePath,
            @Value("${app.files.public-base-url:http://localhost:8081/api/v1/files}") String publicBaseUrl) {
        this.storageDirectory = Paths.get(storagePath).toAbsolutePath().normalize();
        this.publicBaseUrl = publicBaseUrl.endsWith("/")
                ? publicBaseUrl.substring(0, publicBaseUrl.length() - 1)
                : publicBaseUrl;
    }

    public String store(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return null;
        }

        try {
            Files.createDirectories(storageDirectory);

            String extension = StringUtils.getFilenameExtension(file.getOriginalFilename());
            String filename = UUID.randomUUID() + (StringUtils.hasText(extension) ? "." + extension : "");
            Path destination = storageDirectory.resolve(filename).normalize();

            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, destination, StandardCopyOption.REPLACE_EXISTING);
            }

            return buildPublicUrl(filename);
        } catch (IOException ex) {
            throw new IllegalStateException("No se pudo guardar el archivo", ex);
        }
    }

    public Resource loadAsResource(String filename) {
        try {
            Path filePath = storageDirectory.resolve(filename).normalize();
            if (!filePath.startsWith(storageDirectory)) {
                throw new IllegalArgumentException("Archivo inválido");
            }

            Resource resource = new UrlResource(filePath.toUri());
            if (!resource.exists() || !resource.isReadable()) {
                throw new IllegalArgumentException("Archivo no encontrado");
            }
            return resource;
        } catch (MalformedURLException ex) {
            throw new IllegalStateException("No se pudo leer el archivo", ex);
        }
    }

    public String buildPublicUrl(String filename) {
        String encodedFilename = UriUtils.encodePathSegment(filename, StandardCharsets.UTF_8);
        return publicBaseUrl + "/" + encodedFilename;
    }
}