package com.projetopoo.jam.dto.error;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Classe para retornar erros
 */
public class ErrorResponseDTO {
    private LocalDateTime timestamp;
    private int status;
    private String reason;
    private String message;
    private String path;
    private List<String> errors;

    // Construtor para erros gerais
    public ErrorResponseDTO(int status, String reason, String message, String path) {
        this.timestamp = LocalDateTime.now();
        this.status = status;
        this.reason = reason;
        this.message = message;
        this.path = path;
        this.errors = null; // Garante que seja nulo
    }

    // Construtor para erros de validação
    public ErrorResponseDTO(int status, String reason, String message, String path, List<String> errors) {
        this.timestamp = LocalDateTime.now();
        this.status = status;
        this.reason = reason;
        this.message = message;
        this.path = path;
        this.errors = errors;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }
}
