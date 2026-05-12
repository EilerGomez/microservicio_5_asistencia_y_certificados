/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.microservicio_5_asistencias_certificados.excepciones;

/**
 *
 * @author eiler
 */

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ManejadorExcepcionesTest {

    private ManejadorExcepciones manejador;

    @BeforeEach
    void setUp() {
        manejador = new ManejadorExcepciones();
    }

    @Test
    void manejarNoEncontradoRetorna404() {
        RecursoNoEncontradoException ex =
                new RecursoNoEncontradoException("Recurso no encontrado");

        ResponseEntity<Map<String, Object>> r = manejador.manejarNoEncontrado(ex);

        assertEquals(HttpStatus.NOT_FOUND, r.getStatusCode());
        assertEquals("Recurso no encontrado", r.getBody().get("mensaje"));
        assertEquals(404, r.getBody().get("status"));
        assertNotNull(r.getBody().get("timestamp"));
    }

    @Test
    void manejarIllegalStateRetorna400() {
        IllegalStateException ex = new IllegalStateException("Estado inválido");

        ResponseEntity<Map<String, Object>> r = manejador.manejarIllegalState(ex);

        assertEquals(HttpStatus.BAD_REQUEST, r.getStatusCode());
        assertEquals("Estado inválido", r.getBody().get("mensaje"));
        assertEquals(400, r.getBody().get("status"));
    }

    @Test
    void manejarIllegalArgumentRetorna400() {
        IllegalArgumentException ex =
                new IllegalArgumentException("Argumento inválido");

        ResponseEntity<Map<String, Object>> r =
                manejador.manejarIllegalArgument(ex);

        assertEquals(HttpStatus.BAD_REQUEST, r.getStatusCode());
        assertEquals("Argumento inválido", r.getBody().get("mensaje"));
        assertEquals(400, r.getBody().get("status"));
    }

    @Test
    void manejarValidacionRetorna400ConMensajeCampo() {
        MethodArgumentNotValidException ex =
                mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError = new FieldError(
                "obj", "idActividad", "El id de la actividad es obligatorio");

        when(ex.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError));

        ResponseEntity<Map<String, Object>> r = manejador.manejarValidacion(ex);

        assertEquals(HttpStatus.BAD_REQUEST, r.getStatusCode());
        assertTrue(r.getBody().get("mensaje").toString()
                .contains("idActividad"));
        assertEquals(400, r.getBody().get("status"));
    }

    @Test
    void manejarValidacionSinErroresRetornaMensajeGenerico() {
        MethodArgumentNotValidException ex =
                mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);

        when(ex.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(List.of());

        ResponseEntity<Map<String, Object>> r = manejador.manejarValidacion(ex);

        assertEquals(HttpStatus.BAD_REQUEST, r.getStatusCode());
        assertEquals("Error de validación", r.getBody().get("mensaje"));
    }

    @Test
    void recursoNoEncontradoExceptionTienemensaje() {
        RecursoNoEncontradoException ex =
                new RecursoNoEncontradoException("Test mensaje");

        assertEquals("Test mensaje", ex.getMessage());
    }
}
