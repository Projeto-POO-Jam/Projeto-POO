package com.projetopoo.jam.util;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

public class ImageUtil {
    public static String createImage(MultipartFile image, String path, String url) throws IOException {
        String imagePath = "";
        if (image != null && !image.isEmpty()) {
            Path uploadPath = Paths.get(path);

            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            String originalFilename = image.getOriginalFilename();
            assert originalFilename != null;

            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String uniqueFileName = UUID.randomUUID() + extension;

            Files.copy(image.getInputStream(), uploadPath.resolve(uniqueFileName));

            imagePath = "http://localhost:8080" + url + uniqueFileName;
        }

        return imagePath;
    }

    public static String extractRelativePath(String fullUrl) {
        if (fullUrl == null || fullUrl.isBlank()) {
            return fullUrl;
        }

        try {
            URL url = new URL(fullUrl);
            return url.getPath();
        } catch (MalformedURLException e) {
            System.err.println("URL com formato inválido, não foi possível extrair o caminho: " + fullUrl);
            return fullUrl;
        }
    }

    public static void deleteImage(String imagePath) {
        String relativePath = extractRelativePath(imagePath);

        if (relativePath == null || relativePath.isBlank()) {
            return;
        }

        try {
            Path physicalPath = Paths.get("src/main/resources/static" + relativePath);
            Files.deleteIfExists(physicalPath);
        } catch (IOException e) {
            System.err.println("Erro ao deletar a imagem: " + relativePath);
            e.printStackTrace();
        }
    }
}
