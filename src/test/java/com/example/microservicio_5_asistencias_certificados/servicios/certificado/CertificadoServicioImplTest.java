/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.microservicio_5_asistencias_certificados.servicios.certificado;

/**
 *
 * @author eiler
 */


import com.example.microservicio_5_asistencias_certificados.dtos.certificado.CertificadoRequest;
import com.example.microservicio_5_asistencias_certificados.dtos.certificado.CertificadoResponse;
import com.example.microservicio_5_asistencias_certificados.excepciones.RecursoNoEncontradoException;
import com.example.microservicio_5_asistencias_certificados.modelos.certificado.Certificado;
import com.example.microservicio_5_asistencias_certificados.modelos.certificado.TipoCertificadoEnum;
import com.example.microservicio_5_asistencias_certificados.repositorios.certificado.CertificadoRepositorio;
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
class CertificadoServicioImplTest {

    @Mock private CertificadoRepositorio repositorio;
    @InjectMocks private CertificadoServicioImpl servicio;

    private Certificado        certParticipacion;
    private Certificado        certPresentacion;
    private CertificadoRequest reqParticipacion;
    private CertificadoRequest reqPresentacion;

    @BeforeEach
    void setUp() {
        certParticipacion = Certificado.builder()
                .idCertificado(1L).idCongreso(10L).idUsuario(42L)
                .tipoCertificado(TipoCertificadoEnum.PARTICIPACION)
                .urlCertificado("https://cert.pdf").build();

        certPresentacion = Certificado.builder()
                .idCertificado(2L).idCongreso(10L).idUsuario(42L)
                .tipoCertificado(TipoCertificadoEnum.PRESENTACION)
                .idActividad(5L).urlCertificado("https://cert2.pdf").build();

        reqParticipacion = CertificadoRequest.builder()
                .idCongreso(10L).idUsuario(42L)
                .tipoCertificado(TipoCertificadoEnum.PARTICIPACION)
                .urlCertificado("https://cert.pdf").build();

        reqPresentacion = CertificadoRequest.builder()
                .idCongreso(10L).idUsuario(42L)
                .tipoCertificado(TipoCertificadoEnum.PRESENTACION)
                .idActividad(5L).urlCertificado("https://cert2.pdf").build();
    }


    @Test
    void generarParticipacionExitosoRetornaResponse()
            throws RecursoNoEncontradoException {
        when(repositorio.existsByIdCongresoAndIdUsuarioAndTipoCertificado(
                10L, 42L, TipoCertificadoEnum.PARTICIPACION)).thenReturn(false);
        when(repositorio.save(any())).thenReturn(certParticipacion);

        CertificadoResponse r = servicio.generar(reqParticipacion);

        assertNotNull(r);
        assertEquals(TipoCertificadoEnum.PARTICIPACION, r.getTipoCertificado());
        verify(repositorio).save(any());
    }

    @Test
    void generarPresentacionExitosoRetornaResponse()
            throws RecursoNoEncontradoException {
        when(repositorio.existsByIdActividadAndIdUsuarioAndTipoCertificado(
                5L, 42L, TipoCertificadoEnum.PRESENTACION)).thenReturn(false);
        when(repositorio.save(any())).thenReturn(certPresentacion);

        CertificadoResponse r = servicio.generar(reqPresentacion);

        assertNotNull(r);
        assertEquals(TipoCertificadoEnum.PRESENTACION, r.getTipoCertificado());
        assertEquals(5L, r.getIdActividad());
    }

    @Test
    void generarPresentacionSinActividadLanzaIllegalArgument() {
        reqPresentacion.setIdActividad(null);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> servicio.generar(reqPresentacion));
        assertTrue(ex.getMessage().contains("PRESENTACION"));
        verify(repositorio, never()).save(any());
    }

    @Test
    void generarParticipacionDuplicadaLanzaIllegalState() {
        when(repositorio.existsByIdCongresoAndIdUsuarioAndTipoCertificado(
                10L, 42L, TipoCertificadoEnum.PARTICIPACION)).thenReturn(true);

        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> servicio.generar(reqParticipacion));
        assertTrue(ex.getMessage().contains("PARTICIPACION"));
        verify(repositorio, never()).save(any());
    }

    @Test
    void generarPresentacionDuplicadaLanzaIllegalState() {
        when(repositorio.existsByIdActividadAndIdUsuarioAndTipoCertificado(
                5L, 42L, TipoCertificadoEnum.PRESENTACION)).thenReturn(true);

        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> servicio.generar(reqPresentacion));
        assertTrue(ex.getMessage().contains("PRESENTACION"));
        verify(repositorio, never()).save(any());
    }


    @Test
    void obtenerPorIdExisteRetornaResponse() throws RecursoNoEncontradoException {
        when(repositorio.findById(1L)).thenReturn(Optional.of(certParticipacion));

        assertEquals(1L, servicio.obtenerPorId(1L).getIdCertificado());
    }

    @Test
    void obtenerPorIdNoExisteLanzaExcepcion() {
        when(repositorio.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class,
                () -> servicio.obtenerPorId(99L));
    }


    @Test
    void listarPorUsuarioRetornaLista() {
        when(repositorio.findByIdUsuario(42L))
                .thenReturn(List.of(certParticipacion, certPresentacion));

        assertEquals(2, servicio.listarPorUsuario(42L).size());
    }

    @Test
    void listarPorCongresoRetornaLista() {
        when(repositorio.findByIdCongreso(10L))
                .thenReturn(List.of(certParticipacion));

        assertEquals(1, servicio.listarPorCongreso(10L).size());
    }

    @Test
    void listarPorCongresoYUsuarioRetornaLista() {
        when(repositorio.findByIdCongresoAndIdUsuario(10L, 42L))
                .thenReturn(List.of(certParticipacion, certPresentacion));

        assertEquals(2, servicio.listarPorCongresoYUsuario(10L, 42L).size());
    }

    @Test
    void listarPorUsuarioVacioRetornaListaVacia() {
        when(repositorio.findByIdUsuario(99L)).thenReturn(List.of());

        assertTrue(servicio.listarPorUsuario(99L).isEmpty());
    }
}