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

/**
 * Classe com funções uteis para manipulação de arquivos
 */
public class FileUtil {
    /**
     * Função para criar um arquivo a partir de um MultipartFile
     * @param file Arquivo MultipartFile que será criado
     * @param path Local físico onde o arquivo será salvo
     * @param url Endereço pelo qual será possível acessar o arquivo no navegador
     * @return Url completa para acessar o arquivo
     * @throws IOException Pode gerar exceção no caso de erro ao salvar algum arquivo
     */
    public static String createFile(MultipartFile file, String path, String url) throws IOException {
        String filePath = "";

        // Verifica se o arquivo não é nulo ou vazio
        if (file != null && !file.isEmpty()) {

            // Transforma a String em Path
            Path uploadPath = Paths.get(path);

            // Verifica se o endereço existe
            if (!Files.exists(uploadPath)) {
                // Cria as pastas necessárias para o endereço existir
                Files.createDirectories(uploadPath);
            }

            // Pega o nome original do arquivo
            String originalFilename = file.getOriginalFilename();
            assert originalFilename != null;

            // Pega a extensão do nome original do arquivo e dá um novo nome usando UUID
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String uniqueFileName = UUID.randomUUID() + extension;

            // Grava o arquivo
            Files.copy(file.getInputStream(), uploadPath.resolve(uniqueFileName));

            // Constrói a url de acesso
            filePath = "http://localhost:8080" + url + uniqueFileName;
        }

        return filePath;
    }

    /**
     * Função para pegar apenas o endereço relativo de uma Url
     * @param fullUrl Url completa
     * @return Endereço relativo da Url
     */
    public static String extractRelativePath(String fullUrl) {
        // Verifica se o endereço é nulo ou vazio
        if (fullUrl == null || fullUrl.isBlank()) {
            return fullUrl;
        }

        try {
            // Transforma a String em URL
            URL url = new URL(fullUrl);

            // Pega o endereço
            return url.getPath();
        } catch (MalformedURLException e) {
            System.err.println("URL com formato inválido, não foi possível extrair o caminho: " + fullUrl);
            return fullUrl;
        }
    }

    /**
     * Função para excluir um arquivo
     * @param filePath Url do arquivo
     */
    public static void deleteFile(String filePath) {
        // Pega o endereço relativo da url
        String relativePath = extractRelativePath(filePath);

        // Verifica se o endereço relativo é nulo ou vazio
        if (relativePath == null || relativePath.isBlank()) {
            return;
        }

        try {
            // Cria um path com o endereço físico
            Path physicalPath = Paths.get("src/main/resources/static" + relativePath);

            // Exclui se existir o arquivo
            Files.deleteIfExists(physicalPath);
        } catch (IOException e) {
            System.err.println("Erro ao deletar a imagem: " + relativePath);
        }
    }

    /**
     * Função para excluir um arquivo
     * @param filePath Url do arquivo
     */
    public static void deleteDirectory(String filePath) {
        // Pega o endereço relativo da url
        String relativePath = extractRelativePath(filePath);

        // Verifica se o endereço relativo é nulo ou vazio
        if (relativePath == null || relativePath.isBlank()) {
            return;
        }

        try {
            // Cria um path com o endereço físico
            Path physicalPathToFile = Paths.get("src/main/resources/static" + relativePath);

            // Pega o diretório no qual o arquivo está
            File directoryToDelete = physicalPathToFile.getParent().toFile();

            // Exclui se existir o arquivo
            FileUtils.deleteDirectory(directoryToDelete);
        } catch (IOException e) {
            System.err.println("Erro ao deletar o diretório para o caminho: " + filePath);
        } catch (IllegalArgumentException e) {
            System.out.println("Diretório não encontrado para exclusão (já pode ter sido removido): " + filePath);
        }
    }
}
