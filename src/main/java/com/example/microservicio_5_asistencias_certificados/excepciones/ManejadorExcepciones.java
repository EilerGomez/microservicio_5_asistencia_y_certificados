/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.microservicio_5_asistencias_certificados.excepciones;

/**
 *
 * @author eiler
 */

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class ManejadorExcepciones {

    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<Map<String, Object>> manejarNoEncontrado(
            RecursoNoEncontradoException ex) {
        return construirRespuesta(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String, Object>> manejarIllegalState(
            IllegalStateException ex) {
        return construirRespuesta(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> manejarIllegalArgument(
            IllegalArgumentException ex) {
        return construirRespuesta(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> manejarValidacion(
            MethodArgumentNotValidException ex) {
        String mensaje = ex.getBindingResult().getFieldErrors()
                .stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .findFirst()
                .orElse("Error de validación");
        return construirRespuesta(mensaje, HttpStatus.BAD_REQUEST);
    }

    private ResponseEntity<Map<String, Object>> construirRespuesta(
            String mensaje, HttpStatus status) {
        Map<String, Object> cuerpo = new HashMap<>();
        cuerpo.put("mensaje", mensaje);
        cuerpo.put("timestamp", LocalDateTime.now().toString());
        cuerpo.put("status", status.value());
        return ResponseEntity.status(status).body(cuerpo);
    }
}