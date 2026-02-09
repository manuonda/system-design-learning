package com.manuonda.library.shared;


import java.time.LocalDateTime;

/**
 * Clase para representar errores de API de manera consistente.
 * Permite incluir un código de error específico, un mensaje legible para humanos,
 * un timestamp, el endpoint que causó el error y detalles adicionales (como errores de valid
 * @param code
 * @param message
 * @param timestamp
 * @param path
 * @param details
 */
public record ApiErrorResponse(
        String code,
        String message,
        LocalDateTime timestamp,
        String path,
        Object details
) {}