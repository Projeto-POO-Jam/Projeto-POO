package com.projetopoo.jam.util;

import org.apache.commons.io.FileUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

public class FileUtil {
    public static String createFile(MultipartFile file, String path, String url) throws IOException {
        String filePath = "";
        if (file != null && !file.isEmpty()) {
            Path uploadPath = Paths.get(path);

            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            String originalFilename = file.getOriginalFilename();
            assert originalFilename != null;

            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String uniqueFileName = UUID.randomUUID() + extension;

            Files.copy(file.getInputStream(), uploadPath.resolve(uniqueFileName));

            filePath = "http://localhost:8080" + url + uniqueFileName;
        }

        return filePath;
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

    public static void deleteFile(String FilePath) {
        String relativePath = extractRelativePath(FilePath);

        if (relativePath == null || relativePath.isBlank()) {
            return;
        }

        try {
            Path physicalPath = Paths.get("src/main/resources/static" + relativePath);
            Files.deleteIfExists(physicalPath);
        } catch (IOException e) {
            System.err.println("Erro ao deletar a imagem: " + relativePath);
        }
    }

    public static void deleteDirectory(String filePath) {
        String relativePath = extractRelativePath(filePath);

        if (relativePath == null || relativePath.isBlank()) {
            return;
        }

        try {
            Path physicalPathToFile = Paths.get("src/main/resources/static" + relativePath);
            File directoryToDelete = physicalPathToFile.getParent().toFile();

            FileUtils.deleteDirectory(directoryToDelete);
        } catch (IOException e) {
            System.err.println("Erro ao deletar o diretório para o caminho: " + filePath);
        } catch (IllegalArgumentException e) {
            System.out.println("Diretório não encontrado para exclusão (já pode ter sido removido): " + filePath);
        }
    }
}
