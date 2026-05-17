/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.microservicio_5_asistencias_certificados.controladores.tipoParticipacion;

/**
 *
 * @author eiler
 */

import com.example.microservicio_5_asistencias_certificados.dtos.tipoParticipacion.TipoParticipacionResponse;
import com.example.microservicio_5_asistencias_certificados.excepciones.RecursoNoEncontradoException;
import com.example.microservicio_5_asistencias_certificados.modelos.tipoParticipacion.TipoParticipacionEnum;
import com.example.microservicio_5_asistencias_certificados.servicios.tipoParticipacion.TipoParticipacionServicio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TipoParticipacionControladorTest {

    @Mock private TipoParticipacionServicio servicio;
    @InjectMocks private TipoParticipacionControlador controlador;

    private TipoParticipacionResponse resAsistente;
    private TipoParticipacionResponse resPonente;
    private TipoParticipacionResponse resTallerista;
    private TipoParticipacionResponse resPonenteInvitado;

    @BeforeEach
    void setUp() {
        resAsistente = TipoParticipacionResponse.builder()
                .idTipoParticipacion(TipoParticipacionEnum.ASISTENTE.getId())
                .nombreTipo(TipoParticipacionEnum.ASISTENTE.name()).build();

        resPonente = TipoParticipacionResponse.builder()
                .idTipoParticipacion(TipoParticipacionEnum.PONENTE.getId())
                .nombreTipo(TipoParticipacionEnum.PONENTE.name()).build();

        resTallerista = TipoParticipacionResponse.builder()
                .idTipoParticipacion(TipoParticipacionEnum.TALLERISTA.getId())
                .nombreTipo(TipoParticipacionEnum.TALLERISTA.name()).build();

        resPonenteInvitado = TipoParticipacionResponse.builder()
                .idTipoParticipacion(TipoParticipacionEnum.PONENTE_INVITADO.getId())
                .nombreTipo(TipoParticipacionEnum.PONENTE_INVITADO.name()).build();
    }

    @Test
    void listarTodosRetorna200ConCuatroTipos() {
        when(servicio.listarTodos()).thenReturn(
                List.of(resAsistente, resPonente, resTallerista, resPonenteInvitado));

        ResponseEntity<List<TipoParticipacionResponse>> r =
                controlador.listarTodos();

        assertEquals(HttpStatus.OK, r.getStatusCode());
        assertEquals(4, r.getBody().size());
        verify(servicio).listarTodos();
    }

    @Test
    void listarTodosRetornaNombresCorrectamente() {
        when(servicio.listarTodos()).thenReturn(List.of(resAsistente, resPonente));

        List<TipoParticipacionResponse> body =
                controlador.listarTodos().getBody();

        assertEquals(TipoParticipacionEnum.ASISTENTE.name(),
                body.get(0).getNombreTipo());
        assertEquals(TipoParticipacionEnum.PONENTE.name(),
                body.get(1).getNombreTipo());
    }

    @Test
    void listarTodosVacioRetornaListaVacia() {
        when(servicio.listarTodos()).thenReturn(List.of());

        assertTrue(controlador.listarTodos().getBody().isEmpty());
    }

    @Test
    void obtenerPorIdExisteRetorna200() throws RecursoNoEncontradoException {
        when(servicio.obtenerPorId(TipoParticipacionEnum.ASISTENTE.getId()))
                .thenReturn(resAsistente);

        ResponseEntity<TipoParticipacionResponse> r =
                controlador.obtenerPorId(TipoParticipacionEnum.ASISTENTE.getId());

        assertEquals(HttpStatus.OK, r.getStatusCode());
        assertEquals(TipoParticipacionEnum.ASISTENTE.name(),
                r.getBody().getNombreTipo());
        assertEquals(TipoParticipacionEnum.ASISTENTE.getId(),
                r.getBody().getIdTipoParticipacion());
    }

    @Test
    void obtenerPorIdPonenteRetorna200() throws RecursoNoEncontradoException {
        when(servicio.obtenerPorId(TipoParticipacionEnum.PONENTE.getId()))
                .thenReturn(resPonente);

        ResponseEntity<TipoParticipacionResponse> r =
                controlador.obtenerPorId(TipoParticipacionEnum.PONENTE.getId());

        assertEquals(HttpStatus.OK, r.getStatusCode());
        assertEquals(TipoParticipacionEnum.PONENTE.name(),
                r.getBody().getNombreTipo());
    }

    @Test
    void obtenerPorIdTalleristaRetorna200() throws RecursoNoEncontradoException {
        when(servicio.obtenerPorId(TipoParticipacionEnum.TALLERISTA.getId()))
                .thenReturn(resTallerista);

        ResponseEntity<TipoParticipacionResponse> r =
                controlador.obtenerPorId(TipoParticipacionEnum.TALLERISTA.getId());

        assertEquals(HttpStatus.OK, r.getStatusCode());
        assertEquals(TipoParticipacionEnum.TALLERISTA.name(),
                r.getBody().getNombreTipo());
    }

    @Test
    void obtenerPorIdPonenteInvitadoRetorna200() throws RecursoNoEncontradoException {
        when(servicio.obtenerPorId(TipoParticipacionEnum.PONENTE_INVITADO.getId()))
                .thenReturn(resPonenteInvitado);

        ResponseEntity<TipoParticipacionResponse> r =
                controlador.obtenerPorId(
                        TipoParticipacionEnum.PONENTE_INVITADO.getId());

        assertEquals(HttpStatus.OK, r.getStatusCode());
        assertEquals(TipoParticipacionEnum.PONENTE_INVITADO.name(),
                r.getBody().getNombreTipo());
    }

    @Test
    void obtenerPorIdNoExisteLanzaExcepcion() throws RecursoNoEncontradoException {
        when(servicio.obtenerPorId(99))
                .thenThrow(new RecursoNoEncontradoException("99"));

        assertThrows(RecursoNoEncontradoException.class,
                () -> controlador.obtenerPorId(99));
    }
}