/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.microservicio_5_asistencias_certificados.controladores.asistencia;

/**
 *
 * @author eiler
 */


import com.example.microservicio_5_asistencias_certificados.dtos.asistencia.AsistenciaRequest;
import com.example.microservicio_5_asistencias_certificados.dtos.asistencia.AsistenciaResponse;
import com.example.microservicio_5_asistencias_certificados.excepciones.RecursoNoEncontradoException;
import com.example.microservicio_5_asistencias_certificados.servicios.asistencia.AsistenciaServicio;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AsistenciaControladorTest {

    @Mock private AsistenciaServicio servicio;
    @InjectMocks private AsistenciaControlador controlador;

    private AsistenciaRequest  request;
    private AsistenciaResponse response;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        "2", null,
                        List.of(new SimpleGrantedAuthority("ROLE_ADMIN_CONGRESO"))
                )
        );

        request = AsistenciaRequest.builder()
                .idActividad(10L).idUsuario(42L).idTipoParticipacion(1).build();

        response = AsistenciaResponse.builder()
                .idAsistencia(1L).idActividad(10L).idUsuario(42L)
                .idTipoParticipacion(1).nombreTipoParticipacion("ASISTENTE")
                .registradoPor(2L).build();
    }

    @AfterEach
    void limpiarContexto() {
        SecurityContextHolder.clearContext();
    }


    @Test
    void registrarRetorna201() throws RecursoNoEncontradoException {
        when(servicio.registrar(any(), eq(2L))).thenReturn(response);

        ResponseEntity<AsistenciaResponse> r = controlador.registrar(request);

        assertEquals(HttpStatus.CREATED, r.getStatusCode());
        assertEquals(42L, r.getBody().getIdUsuario());
        verify(servicio).registrar(any(), eq(2L));
    }

    @Test
    void registrarDuplicadoPropagaIllegalState() throws RecursoNoEncontradoException {
        when(servicio.registrar(any(), eq(2L)))
                .thenThrow(new IllegalStateException("ya tiene asistencia"));

        assertThrows(IllegalStateException.class,
                () -> controlador.registrar(request));
    }

    @Test
    void registrarTipoNoExisteLanzaExcepcion() throws RecursoNoEncontradoException {
        when(servicio.registrar(any(), eq(2L)))
                .thenThrow(new RecursoNoEncontradoException("Tipo 99 no encontrado"));

        assertThrows(RecursoNoEncontradoException.class,
                () -> controlador.registrar(request));
    }


    @Test
    void obtenerPorIdExisteRetorna200() throws RecursoNoEncontradoException {
        when(servicio.obtenerPorId(1L)).thenReturn(response);

        ResponseEntity<AsistenciaResponse> r = controlador.obtenerPorId(1L);

        assertEquals(HttpStatus.OK, r.getStatusCode());
        assertEquals(1L, r.getBody().getIdAsistencia());
    }

    @Test
    void obtenerPorIdNoExisteLanzaExcepcion() throws RecursoNoEncontradoException {
        when(servicio.obtenerPorId(99L))
                .thenThrow(new RecursoNoEncontradoException("99"));

        assertThrows(RecursoNoEncontradoException.class,
                () -> controlador.obtenerPorId(99L));
    }


    @Test
    void listarPorActividadRetorna200() {
        when(servicio.listarPorActividad(10L)).thenReturn(List.of(response));

        ResponseEntity<List<AsistenciaResponse>> r =
                controlador.listarPorActividad(10L);

        assertEquals(HttpStatus.OK, r.getStatusCode());
        assertEquals(1, r.getBody().size());
    }

    @Test
    void listarPorActividadVaciaRetornaListaVacia() {
        when(servicio.listarPorActividad(99L)).thenReturn(List.of());

        assertTrue(controlador.listarPorActividad(99L).getBody().isEmpty());
    }

    @Test
    void listarMisAsistenciasRetorna200() {
        when(servicio.listarPorUsuario(2L)).thenReturn(List.of(response));

        ResponseEntity<List<AsistenciaResponse>> r =
                controlador.listarMisAsistencias();

        assertEquals(HttpStatus.OK, r.getStatusCode());
        assertEquals(1, r.getBody().size());
    }


    @Test
    void existeAsistenciaTrueRetorna200() {
        when(servicio.existeAsistencia(10L, 42L)).thenReturn(true);

        ResponseEntity<Boolean> r = controlador.existeAsistencia(10L, 42L);

        assertEquals(HttpStatus.OK, r.getStatusCode());
        assertTrue(r.getBody());
    }

    @Test
    void existeAsistenciaFalseRetorna200() {
        when(servicio.existeAsistencia(10L, 99L)).thenReturn(false);

        assertFalse(controlador.existeAsistencia(10L, 99L).getBody());
    }
}
