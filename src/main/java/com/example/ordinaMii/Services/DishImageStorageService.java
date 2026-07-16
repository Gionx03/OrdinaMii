package com.example.ordinaMii.Services;

import com.example.ordinaMii.Exceptions.BadRequestException;
import com.example.ordinaMii.Exceptions.FileStorageException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
public class DishImageStorageService {

    private static final long MAX_IMAGE_SIZE = 5L * 1024L * 1024L;

    private static final Map<String, String> ALLOWED_CONTENT_TYPES = Map.of(
            "image/jpeg", "jpg",
            "image/png", "png",
            "image/webp", "webp"
    );

    private final Path uploadDirectory;

    public DishImageStorageService(
            @Value("${app.uploads.dishes-directory:uploads/dishes}")
            String directory) {

        this.uploadDirectory = Paths.get(directory)
                .toAbsolutePath()
                .normalize();
    }

    public String store(MultipartFile file) {
        validate(file);

        String contentType = file.getContentType()
                .toLowerCase(Locale.ROOT);

        String extension = ALLOWED_CONTENT_TYPES.get(contentType);
        String fileName = UUID.randomUUID() + "." + extension;

        Path destination = uploadDirectory
                .resolve(fileName)
                .normalize();

        if (!destination.getParent().equals(uploadDirectory)) {
            throw new BadRequestException("Nome del file non valido");
        }

        try {
            Files.createDirectories(uploadDirectory);

            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(
                        inputStream,
                        destination,
                        StandardCopyOption.REPLACE_EXISTING
                );
            }
        } catch (IOException exception) {
            log.error(
                    "Impossibile salvare l'immagine in {}",
                    destination,
                    exception
            );

            throw new FileStorageException(
                    "Non è stato possibile salvare l'immagine",
                    exception
            );
        }

        log.info("Immagine del piatto salvata in {}", destination);

        return "/uploads/dishes/" + fileName;
    }

    private void validate(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException(
                    "Seleziona un'immagine da caricare"
            );
        }

        if (file.getSize() > MAX_IMAGE_SIZE) {
            throw new BadRequestException(
                    "L'immagine non può superare 5 MB"
            );
        }

        String contentType = file.getContentType();

        if (
                contentType == null ||
                        !ALLOWED_CONTENT_TYPES.containsKey(
                                contentType.toLowerCase(Locale.ROOT)
                        )
        ) {
            throw new BadRequestException(
                    "Formato non supportato. Usa JPG, PNG oppure WEBP"
            );
        }
    }
}