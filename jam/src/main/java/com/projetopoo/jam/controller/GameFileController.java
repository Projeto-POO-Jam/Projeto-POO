package com.projetopoo.jam.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

@Controller
public class GameFileController {

    private final Path uploadLocation = Paths.get("src", "main", "resources", "static", "upload");

    @GetMapping("/upload/game/file/**")
    public void serveGameFile(HttpServletRequest request, HttpServletResponse response) throws IOException {

        String requestURI = request.getRequestURI();
        String filePathString = requestURI.substring("/upload/game/file/".length());

        Path filePath = this.uploadLocation.resolve(Paths.get("game", "file", filePathString)).normalize();
        File file = filePath.toFile();

        if (!file.exists() || !file.getCanonicalPath().startsWith(this.uploadLocation.toFile().getCanonicalPath())) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        String filename = file.getName();

        if (filename.endsWith(".gz")) {
            response.setHeader("Content-Encoding", "gzip");

            if (filename.endsWith(".js.gz")) {
                response.setContentType("application/javascript");
            } else if (filename.endsWith(".wasm.gz")) {
                response.setContentType("application/wasm");
            } else if (filename.endsWith(".data.gz")) {
                response.setContentType("application/octet-stream");
            }
        } else {
            String mimeType = request.getServletContext().getMimeType(file.getAbsolutePath());
            response.setContentType(mimeType != null ? mimeType : "application/octet-stream");
        }

        response.setContentLength((int) file.length());
        try (InputStream inputStream = new FileInputStream(file)) {
            FileCopyUtils.copy(inputStream, response.getOutputStream());
        }
    }
}