package io.aotchoun.blog.service;

import io.aotchoun.blog.exception.BadRequestException;
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
 * Stocke les fichiers dans ./uploads/
 * Format : {uuid}_{original-filename}
 * Validation : type MIME + taille
 */
@Service
public class FileStorageService {

    private final Path uploadDir = Paths.get("uploads");
    private final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB

    public FileStorageService() {
        try {
            Files.createDirectories(uploadDir);
        } catch (IOException e) {
            throw new RuntimeException("Could not create upload directory", e);
        }
    }

    /**
     * Stocke un fichier et retourne le chemin relatif
     */
    public String storeFile(MultipartFile file) {
        // Validation
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

        // Nettoyer le nom du fichier
        String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
        
        // Générer un nom unique
        String extension = getFileExtension(originalFilename);
        String filename = UUID.randomUUID().toString() + "_" + System.currentTimeMillis() + extension;

        try {
            // Copier le fichier dans le dossier uploads
            Path targetLocation = uploadDir.resolve(filename);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            
            // Retourner le chemin relatif (pour stockage en DB)
            return "/uploads/" + filename;
        } catch (IOException e) {
            throw new RuntimeException("Could not store file " + filename, e);
        }
    }

    /**
     * Supprime un fichier
     */
    public void deleteFile(String fileUrl) {
        if (fileUrl == null || fileUrl.isEmpty()) return;
        
        try {
            // Extraire le nom du fichier depuis l'URL
            String filename = fileUrl.substring(fileUrl.lastIndexOf("/") + 1);
            Path filePath = uploadDir.resolve(filename);
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            // Log l'erreur mais ne pas planter l'application
            System.err.println("Could not delete file: " + fileUrl);
        }
    }

    private boolean isValidImageType(String contentType) {
        return contentType.equals("image/jpeg") ||
               contentType.equals("image/jpg") ||
               contentType.equals("image/png") ||
               contentType.equals("image/gif") ||
               contentType.equals("image/webp");
    }

    private String getFileExtension(String filename) {
        if (filename == null) return "";
        int lastDot = filename.lastIndexOf(".");
        return lastDot == -1 ? "" : filename.substring(lastDot);
    }
}