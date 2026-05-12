/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.microservicio_5_asistencias_certificados.controladores.certificado;

/**
 *
 * @author eiler
 */


import com.example.microservicio_5_asistencias_certificados.dtos.certificado.CertificadoRequest;
import com.example.microservicio_5_asistencias_certificados.dtos.certificado.CertificadoResponse;
import com.example.microservicio_5_asistencias_certificados.excepciones.RecursoNoEncontradoException;
import com.example.microservicio_5_asistencias_certificados.modelos.certificado.TipoCertificadoEnum;
import com.example.microservicio_5_asistencias_certificados.servicios.certificado.CertificadoServicio;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CertificadoControladorTest {

    @Mock private CertificadoServicio servicio;
    @InjectMocks private CertificadoControlador controlador;

    private CertificadoRequest  reqParticipacion;
    private CertificadoResponse resParticipacion;
    private CertificadoResponse resPresentacion;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        "2", null,
                        List.of(new SimpleGrantedAuthority("ROLE_ADMIN_CONGRESO"))
                )
        );

        reqParticipacion = CertificadoRequest.builder()
                .idCongreso(10L).idUsuario(42L)
                .tipoCertificado(TipoCertificadoEnum.PARTICIPACION)
                .urlCertificado("https://cert.pdf").build();

        resParticipacion = CertificadoResponse.builder()
                .idCertificado(1L).idCongreso(10L).idUsuario(42L)
                .tipoCertificado(TipoCertificadoEnum.PARTICIPACION)
                .urlCertificado("https://cert.pdf").build();

        resPresentacion = CertificadoResponse.builder()
                .idCertificado(2L).idCongreso(10L).idUsuario(42L)
                .tipoCertificado(TipoCertificadoEnum.PRESENTACION)
                .idActividad(5L).urlCertificado("https://cert2.pdf").build();
    }

    @AfterEach
    void limpiarContexto() {
        SecurityContextHolder.clearContext();
    }


    @Test
    void generarRetorna201() throws RecursoNoEncontradoException {
        when(servicio.generar(any())).thenReturn(resParticipacion);

        ResponseEntity<CertificadoResponse> r =
                controlador.generar(reqParticipacion);

        assertEquals(HttpStatus.CREATED, r.getStatusCode());
        assertEquals(TipoCertificadoEnum.PARTICIPACION,
                r.getBody().getTipoCertificado());
        verify(servicio).generar(any());
    }

    @Test
    void generarDuplicadoPropagaIllegalState() throws RecursoNoEncontradoException {
        when(servicio.generar(any()))
                .thenThrow(new IllegalStateException("ya tiene certificado"));

        assertThrows(IllegalStateException.class,
                () -> controlador.generar(reqParticipacion));
    }

    @Test
    void generarPresentacionSinActividadPropagaIllegalArgument()
            throws RecursoNoEncontradoException {
        when(servicio.generar(any()))
                .thenThrow(new IllegalArgumentException("PRESENTACION requiere actividad"));

        assertThrows(IllegalArgumentException.class,
                () -> controlador.generar(reqParticipacion));
    }


    @Test
    void obtenerPorIdExisteRetorna200() throws RecursoNoEncontradoException {
        when(servicio.obtenerPorId(1L)).thenReturn(resParticipacion);

        ResponseEntity<CertificadoResponse> r = controlador.obtenerPorId(1L);

        assertEquals(HttpStatus.OK, r.getStatusCode());
        assertEquals(1L, r.getBody().getIdCertificado());
    }

    @Test
    void obtenerPorIdNoExisteLanzaExcepcion() throws RecursoNoEncontradoException {
        when(servicio.obtenerPorId(99L))
                .thenThrow(new RecursoNoEncontradoException("99"));

        assertThrows(RecursoNoEncontradoException.class,
                () -> controlador.obtenerPorId(99L));
    }


    @Test
    void listarMisCertificadosRetorna200() {
        when(servicio.listarPorUsuario(2L))
                .thenReturn(List.of(resParticipacion, resPresentacion));

        ResponseEntity<List<CertificadoResponse>> r =
                controlador.listarMisCertificados();

        assertEquals(HttpStatus.OK, r.getStatusCode());
        assertEquals(2, r.getBody().size());
    }

    @Test
    void listarMisCertificadosVacioRetornaListaVacia() {
        when(servicio.listarPorUsuario(2L)).thenReturn(List.of());

        assertTrue(controlador.listarMisCertificados().getBody().isEmpty());
    }

    @Test
    void listarPorCongresoRetorna200() {
        when(servicio.listarPorCongreso(10L)).thenReturn(List.of(resParticipacion));

        ResponseEntity<List<CertificadoResponse>> r =
                controlador.listarPorCongreso(10L);

        assertEquals(HttpStatus.OK, r.getStatusCode());
        assertEquals(1, r.getBody().size());
    }

    @Test
    void listarPorCongresoYUsuarioRetorna200() {
        when(servicio.listarPorCongresoYUsuario(10L, 42L))
                .thenReturn(List.of(resParticipacion, resPresentacion));

        ResponseEntity<List<CertificadoResponse>> r =
                controlador.listarPorCongresoYUsuario(10L, 42L);

        assertEquals(HttpStatus.OK, r.getStatusCode());
        assertEquals(2, r.getBody().size());
    }

    @Test
    void listarPorCongresoVacioRetornaListaVacia() {
        when(servicio.listarPorCongreso(99L)).thenReturn(List.of());

        assertTrue(controlador.listarPorCongreso(99L).getBody().isEmpty());
    }
}