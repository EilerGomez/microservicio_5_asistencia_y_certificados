/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.microservicio_5_asistencias_certificados.servicios.asistencia;

/**
 *
 * @author eiler
 */

import com.example.microservicio_5_asistencias_certificados.dtos.asistencia.AsistenciaRequest;
import com.example.microservicio_5_asistencias_certificados.dtos.asistencia.AsistenciaResponse;
import com.example.microservicio_5_asistencias_certificados.dtos.asistencia.AsistenciaUpdateRequest;
import com.example.microservicio_5_asistencias_certificados.excepciones.RecursoNoEncontradoException;
import com.example.microservicio_5_asistencias_certificados.modelos.asistencia.Asistencia;
import com.example.microservicio_5_asistencias_certificados.modelos.tipoParticipacion.TipoParticipacion;
import com.example.microservicio_5_asistencias_certificados.modelos.tipoParticipacion.TipoParticipacionEnum;
import com.example.microservicio_5_asistencias_certificados.repositorios.asistencia.AsistenciaRepositorio;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AsistenciaServicioImplTest {

    @Mock private AsistenciaRepositorio        asistenciaRepositorio;
    @Mock private TipoParticipacionRepositorio tipoRepositorio;

    @InjectMocks private AsistenciaServicioImpl servicio;

    private TipoParticipacion tipoAsistente;
    private Asistencia        asistencia;
    private AsistenciaRequest request;

    @BeforeEach
    void setUp() {
        tipoAsistente = TipoParticipacion.builder()
                .idTipoParticipacion(1).nombreTipo("ASISTENTE").build();

        asistencia = Asistencia.builder()
                .idAsistencia(1L).idActividad(10L).idUsuario(42L)
                .tipoParticipacion(tipoAsistente).registradoPor(2L).build();

        request = AsistenciaRequest.builder()
                .idActividad(10L).idUsuario(42L).idTipoParticipacion(1).build();
    }


    @Test
    void registrarAsistenciaExitosaRetornaResponse()
            throws RecursoNoEncontradoException {
        when(asistenciaRepositorio.existsByIdActividadAndIdUsuario(10L, 42L))
                .thenReturn(false);
        when(tipoRepositorio.findById(1)).thenReturn(Optional.of(tipoAsistente));
        when(asistenciaRepositorio.save(any())).thenReturn(asistencia);

        AsistenciaResponse r = servicio.registrar(request, 2L);

        assertNotNull(r);
        assertEquals(42L, r.getIdUsuario());
        assertEquals("ASISTENTE", r.getNombreTipoParticipacion());
        verify(asistenciaRepositorio).save(any());
    }

    @Test
    void registrarAsistenciaDuplicadaLanzaIllegalState() {
        when(asistenciaRepositorio.existsByIdActividadAndIdUsuario(10L, 42L))
                .thenReturn(true);

        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> servicio.registrar(request, 2L));
        assertTrue(ex.getMessage().contains("ya tiene asistencia"));
        verify(asistenciaRepositorio, never()).save(any());
    }

    @Test
    void registrarTipoNoExisteLanzaExcepcion() {
        when(asistenciaRepositorio.existsByIdActividadAndIdUsuario(10L, 42L))
                .thenReturn(false);
        when(tipoRepositorio.findById(1)).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class,
                () -> servicio.registrar(request, 2L));
        verify(asistenciaRepositorio, never()).save(any());
    }


    @Test
    void obtenerPorIdExisteRetornaResponse() throws RecursoNoEncontradoException {
        when(asistenciaRepositorio.findById(1L)).thenReturn(Optional.of(asistencia));

        assertEquals(1L, servicio.obtenerPorId(1L).getIdAsistencia());
    }

    @Test
    void obtenerPorIdNoExisteLanzaExcepcion() {
        when(asistenciaRepositorio.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class,
                () -> servicio.obtenerPorId(99L));
    }


    @Test
    void listarPorActividadRetornaLista() {
        when(asistenciaRepositorio.findByIdActividad(10L))
                .thenReturn(List.of(asistencia));

        assertEquals(1, servicio.listarPorActividad(10L).size());
    }

    @Test
    void listarPorActividadVaciaRetornaListaVacia() {
        when(asistenciaRepositorio.findByIdActividad(99L)).thenReturn(List.of());

        assertTrue(servicio.listarPorActividad(99L).isEmpty());
    }

    @Test
    void listarPorUsuarioRetornaLista() {
        when(asistenciaRepositorio.findByIdUsuario(42L))
                .thenReturn(List.of(asistencia));

        assertEquals(1, servicio.listarPorUsuario(42L).size());
    }


    @Test
    void existeAsistenciaTrueRetornaTrue() {
        when(asistenciaRepositorio.existsByIdActividadAndIdUsuario(10L, 42L))
                .thenReturn(true);

        assertTrue(servicio.existeAsistencia(10L, 42L));
    }

    @Test
    void existeAsistenciaFalseRetornaFalse() {
        when(asistenciaRepositorio.existsByIdActividadAndIdUsuario(10L, 99L))
                .thenReturn(false);

        assertFalse(servicio.existeAsistencia(10L, 99L));
    }
    

    @Test
    void actualizarAsistenciaExitosoRetornaResponse()
            throws RecursoNoEncontradoException {

        TipoParticipacion ponente = TipoParticipacion.builder()
                .idTipoParticipacion(TipoParticipacionEnum.PONENTE.getId())
                .nombreTipo(TipoParticipacionEnum.PONENTE.name()).build();

        AsistenciaUpdateRequest updateRequest = AsistenciaUpdateRequest.builder()
                .idActividad(20L).idUsuario(99L)
                .idTipoParticipacion(TipoParticipacionEnum.PONENTE.getId()).build();

        Asistencia asistenciaActualizada = Asistencia.builder()
                .idAsistencia(1L).idActividad(20L).idUsuario(99L)
                .tipoParticipacion(ponente).registradoPor(2L).build();

        when(asistenciaRepositorio.findById(1L))
                .thenReturn(Optional.of(asistencia));
        when(tipoRepositorio.findById(TipoParticipacionEnum.PONENTE.getId()))
                .thenReturn(Optional.of(ponente));
        when(asistenciaRepositorio.save(any()))
                .thenReturn(asistenciaActualizada);

        AsistenciaResponse r = servicio.actualizar(1L, updateRequest);

        assertNotNull(r);
        assertEquals(20L, r.getIdActividad());
        assertEquals(99L, r.getIdUsuario());
        assertEquals(TipoParticipacionEnum.PONENTE.name(),
                r.getNombreTipoParticipacion());
        verify(asistenciaRepositorio).save(any());
    }

    @Test
    void actualizarAsistenciaNoExisteLanzaExcepcion() {
        AsistenciaUpdateRequest updateRequest = AsistenciaUpdateRequest.builder()
                .idActividad(20L).idUsuario(99L)
                .idTipoParticipacion(TipoParticipacionEnum.PONENTE.getId()).build();

        when(asistenciaRepositorio.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class,
                () -> servicio.actualizar(99L, updateRequest));
        verify(asistenciaRepositorio, never()).save(any());
    }

    @Test
    void actualizarAsistenciaTipoNoExisteLanzaExcepcion() {
        AsistenciaUpdateRequest updateRequest = AsistenciaUpdateRequest.builder()
                .idActividad(20L).idUsuario(99L)
                .idTipoParticipacion(99).build();

        when(asistenciaRepositorio.findById(1L))
                .thenReturn(Optional.of(asistencia));
        when(tipoRepositorio.findById(99)).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class,
                () -> servicio.actualizar(1L, updateRequest));
        verify(asistenciaRepositorio, never()).save(any());
    }

    @Test
    void actualizarAsistenciaCambiaActividadYUsuario()
            throws RecursoNoEncontradoException {

        AsistenciaUpdateRequest updateRequest = AsistenciaUpdateRequest.builder()
                .idActividad(50L).idUsuario(77L)
                .idTipoParticipacion(TipoParticipacionEnum.TALLERISTA.getId()).build();

        TipoParticipacion tallerista = TipoParticipacion.builder()
                .idTipoParticipacion(TipoParticipacionEnum.TALLERISTA.getId())
                .nombreTipo(TipoParticipacionEnum.TALLERISTA.name()).build();

        Asistencia actualizada = Asistencia.builder()
                .idAsistencia(1L).idActividad(50L).idUsuario(77L)
                .tipoParticipacion(tallerista).registradoPor(2L).build();

        when(asistenciaRepositorio.findById(1L))
                .thenReturn(Optional.of(asistencia));
        when(tipoRepositorio.findById(TipoParticipacionEnum.TALLERISTA.getId()))
                .thenReturn(Optional.of(tallerista));
        when(asistenciaRepositorio.save(any())).thenReturn(actualizada);

        AsistenciaResponse r = servicio.actualizar(1L, updateRequest);

        assertEquals(50L, r.getIdActividad());
        assertEquals(77L, r.getIdUsuario());
        assertEquals(TipoParticipacionEnum.TALLERISTA.name(),
                r.getNombreTipoParticipacion());
    }
}
