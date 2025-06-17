package ru.gb.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
public class ImageService {
    @Value("${image.upload.dir}")
    private String imageUploadDir;

    private final List<String> allowedFormats = Arrays.asList("png", "jpg", "jpeg", "tiff", "tif", "webp", "heif", "heic");
    private final long maxFileSize = 5 * 1024 * 1024;

    public String uploadImage(MultipartFile image) {
        try {
            if (image == null || image.isEmpty()) {
                return null;
            }

            String originalImageName = image.getOriginalFilename();
            if (originalImageName == null) {
                return null;
            }

            int lastDotIndex = originalImageName.lastIndexOf(".");
            if (lastDotIndex == -1) {
                return null;
            }

            String extension = originalImageName.substring(lastDotIndex + 1).toLowerCase();
            if (!allowedFormats.contains(extension)) {
                return null;
            }

            if (image.getSize() > maxFileSize) {
                return null;
            }

            File uploadDir = new File(imageUploadDir);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }

            String fileName = UUID.randomUUID().toString() + "_" + originalImageName + System.currentTimeMillis();
            File destFile = new File(uploadDir, fileName);

            image.transferTo(destFile);
            return fileName; // Fayl nomini qaytarish
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}