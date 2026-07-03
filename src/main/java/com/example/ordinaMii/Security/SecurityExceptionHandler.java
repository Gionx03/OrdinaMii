package com.example.ordinaMii.Security;

import com.example.ordinaMii.Exceptions.ErrorResponse;
import tools.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.example.ordinaMii.Exceptions.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class SecurityExceptionHandler implements AuthenticationEntryPoint, AccessDeniedHandler {

    private final ObjectMapper objectMapper;

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException
    ) throws IOException {

        log.warn("Accesso non autenticato. Metodo: {}, Path: {}, messaggio: {}",
                request.getMethod(),
                request.getRequestURI(),
                authException.getMessage()
        );

        writeErrorResponse(
                response,
                request,
                HttpStatus.UNAUTHORIZED,
                "Token mancante, scaduto o non valido"
        );
    }

    @Override
    public void handle(
            HttpServletRequest request,
            HttpServletResponse response,
            AccessDeniedException accessDeniedException
    ) throws IOException {

        log.warn("Accesso negato. Metodo: {}, Path: {}, messaggio: {}",
                request.getMethod(),
                request.getRequestURI(),
                accessDeniedException.getMessage()
        );

        writeErrorResponse(
                response,
                request,
                HttpStatus.FORBIDDEN,
                "Non hai i permessi per accedere a questa risorsa"
        );
    }

    private void writeErrorResponse(
            HttpServletResponse response,
            HttpServletRequest request,
            HttpStatus status,
            String message
    ) throws IOException {

        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                message,
                request.getRequestURI()
        );

        objectMapper.writeValue(response.getOutputStream(), errorResponse);
    }
}