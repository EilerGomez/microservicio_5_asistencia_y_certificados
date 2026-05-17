/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.microservicio_5_asistencias_certificados.servicios.tipoParticipacion;

/**
 *
 * @author eiler
 */

import com.example.microservicio_5_asistencias_certificados.dtos.tipoParticipacion.TipoParticipacionResponse;
import com.example.microservicio_5_asistencias_certificados.excepciones.RecursoNoEncontradoException;
import com.example.microservicio_5_asistencias_certificados.modelos.tipoParticipacion.TipoParticipacion;
import com.example.microservicio_5_asistencias_certificados.modelos.tipoParticipacion.TipoParticipacionEnum;
import com.example.microservicio_5_asistencias_certificados.repositorios.tipoParticipacion.TipoParticipacionRepositorio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TipoParticipacionServicioImplTest {

    @Mock private TipoParticipacionRepositorio repositorio;
    @InjectMocks private TipoParticipacionServicioImpl servicio;

    private TipoParticipacion asistente;
    private TipoParticipacion ponente;
    private TipoParticipacion tallerista;
    private TipoParticipacion ponenteInvitado;

    @BeforeEach
    void setUp() {
        asistente = TipoParticipacion.builder()
                .idTipoParticipacion(TipoParticipacionEnum.ASISTENTE.getId())
                .nombreTipo(TipoParticipacionEnum.ASISTENTE.name()).build();

        ponente = TipoParticipacion.builder()
                .idTipoParticipacion(TipoParticipacionEnum.PONENTE.getId())
                .nombreTipo(TipoParticipacionEnum.PONENTE.name()).build();

        tallerista = TipoParticipacion.builder()
                .idTipoParticipacion(TipoParticipacionEnum.TALLERISTA.getId())
                .nombreTipo(TipoParticipacionEnum.TALLERISTA.name()).build();

        ponenteInvitado = TipoParticipacion.builder()
                .idTipoParticipacion(TipoParticipacionEnum.PONENTE_INVITADO.getId())
                .nombreTipo(TipoParticipacionEnum.PONENTE_INVITADO.name()).build();
    }

    @Test
    void listarTodosRetornaCuatroTipos() {
        when(repositorio.findAll())
                .thenReturn(List.of(asistente, ponente, tallerista, ponenteInvitado));

        List<TipoParticipacionResponse> resultado = servicio.listarTodos();

        assertEquals(4, resultado.size());
        verify(repositorio).findAll();
    }

    @Test
    void listarTodosRetornaNombresCorrectamente() {
        when(repositorio.findAll()).thenReturn(List.of(asistente, ponente));

        List<TipoParticipacionResponse> resultado = servicio.listarTodos();

        assertEquals(TipoParticipacionEnum.ASISTENTE.name(),
                resultado.get(0).getNombreTipo());
        assertEquals(TipoParticipacionEnum.PONENTE.name(),
                resultado.get(1).getNombreTipo());
    }

    @Test
    void listarTodosVacioRetornaListaVacia() {
        when(repositorio.findAll()).thenReturn(List.of());

        assertTrue(servicio.listarTodos().isEmpty());
    }

    @Test
    void obtenerPorIdExisteRetornaResponse() throws RecursoNoEncontradoException {
        when(repositorio.findById(TipoParticipacionEnum.ASISTENTE.getId()))
                .thenReturn(Optional.of(asistente));

        TipoParticipacionResponse r =
                servicio.obtenerPorId(TipoParticipacionEnum.ASISTENTE.getId());

        assertNotNull(r);
        assertEquals(TipoParticipacionEnum.ASISTENTE.name(), r.getNombreTipo());
        assertEquals(TipoParticipacionEnum.ASISTENTE.getId(),
                r.getIdTipoParticipacion());
    }

    @Test
    void obtenerPorIdPonenteRetornaResponse() throws RecursoNoEncontradoException {
        when(repositorio.findById(TipoParticipacionEnum.PONENTE.getId()))
                .thenReturn(Optional.of(ponente));

        TipoParticipacionResponse r =
                servicio.obtenerPorId(TipoParticipacionEnum.PONENTE.getId());

        assertEquals(TipoParticipacionEnum.PONENTE.name(), r.getNombreTipo());
    }

    @Test
    void obtenerPorIdNoExisteLanzaExcepcion() {
        when(repositorio.findById(99)).thenReturn(Optional.empty());

        RecursoNoEncontradoException ex = assertThrows(
                RecursoNoEncontradoException.class,
                () -> servicio.obtenerPorId(99));
        assertTrue(ex.getMessage().contains("99"));
    }
}