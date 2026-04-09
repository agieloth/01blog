package io.aotchoun.blog.service;

import io.aotchoun.blog.exception.BadRequestException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

/**
 * Service de gestion des fichiers uploadés
 *
 * FIX v2 :
 * - getOriginalFilename() peut retourner null → NPE dans StringUtils.cleanPath()
 *   Ajout d'un null-check avec valeur par défaut "file"
 * - upload-dir injecté depuis les properties (au lieu d'être hardcodé)
 * - Validation du type MIME renforcée
 */
@Service
public class FileStorageService {

    private final Path uploadDir;
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5 MB

    public FileStorageService(@Value("${file.upload-dir:./uploads}") String uploadDirPath) {
        this.uploadDir = Paths.get(uploadDirPath).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.uploadDir);
        } catch (IOException e) {
            throw new RuntimeException("Could not create upload directory: " + uploadDirPath, e);
        }
    }

    /**
     * Stocke un fichier et retourne le chemin relatif accessible via HTTP
     *
     * FIX : null-check sur getOriginalFilename() avant StringUtils.cleanPath()
     */
    public String storeFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("File is empty");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new BadRequestException("File size exceeds maximum limit of 5MB");
        }

        String contentType = file.getContentType();
        if (contentType == null || !isValidImageType(contentType)) {
            throw new BadRequestException("Only image files (JPG, PNG, GIF, WEBP) are allowed");
        }

        // FIX : getOriginalFilename() peut être null → utiliser "file" par défaut
        String rawName = file.getOriginalFilename();
        String originalFilename = StringUtils.cleanPath(
                rawName != null ? rawName : "file"
        );

        // Sécurité : empêcher les path traversal ("../../etc/passwd")
        if (originalFilename.contains("..")) {
            throw new BadRequestException("Invalid file name: path traversal detected");
        }

        String extension = getFileExtension(originalFilename);
        String filename = UUID.randomUUID() + "_" + System.currentTimeMillis() + extension;

        try {
            Path targetLocation = uploadDir.resolve(filename);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            return "/uploads/" + filename;
        } catch (IOException e) {
            throw new RuntimeException("Could not store file " + filename, e);
        }
    }

    /**
     * Supprime un fichier uploadé
     */
    public void deleteFile(String fileUrl) {
        if (fileUrl == null || fileUrl.isEmpty()) return;
        try {
            String filename = fileUrl.substring(fileUrl.lastIndexOf("/") + 1);
            Path filePath = uploadDir.resolve(filename).normalize();
            // Sécurité : vérifier que le fichier est bien dans le dossier uploads
            if (!filePath.startsWith(uploadDir)) {
                return; // path traversal tenté → ignorer silencieusement
            }
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            System.err.println("Could not delete file: " + fileUrl + " — " + e.getMessage());
        }
    }

    private boolean isValidImageType(String contentType) {
        return contentType.equals("image/jpeg") ||
               contentType.equals("image/jpg")  ||
               contentType.equals("image/png")  ||
               contentType.equals("image/gif")  ||
               contentType.equals("image/webp");
    }

    private String getFileExtension(String filename) {
        if (filename == null) return "";
        int lastDot = filename.lastIndexOf(".");
        return lastDot == -1 ? "" : filename.substring(lastDot).toLowerCase();
    }
}