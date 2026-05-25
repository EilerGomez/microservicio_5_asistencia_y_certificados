package com.example.microservicio_5_asistencias_certificados.servicios.certificado;

import com.example.microservicio_5_asistencias_certificados.dtos.certificado.CertificadoRequest;
import com.example.microservicio_5_asistencias_certificados.dtos.certificado.CertificadoResponse;
import com.example.microservicio_5_asistencias_certificados.dtos.certificado.CertificadoUpdateRequest;
import com.example.microservicio_5_asistencias_certificados.excepciones.RecursoNoEncontradoException;
import com.example.microservicio_5_asistencias_certificados.modelos.certificado.Certificado;
import com.example.microservicio_5_asistencias_certificados.modelos.certificado.TipoCertificadoEnum;
import com.example.microservicio_5_asistencias_certificados.repositorios.asistencia.AsistenciaRepositorio;
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

    @Mock
    private CertificadoRepositorio repositorio;

    @Mock
    private AsistenciaRepositorio asistenciaRepositorio;

    @InjectMocks
    private CertificadoServicioImpl servicio;

    private Certificado certParticipacion;
    private Certificado certPresentacion;
    private CertificadoRequest reqParticipacion;
    private CertificadoRequest reqPresentacion;

    @BeforeEach
    void setUp() {
        certParticipacion = Certificado.builder()
                .idCertificado(1L)
                .idCongreso(10L)
                .idUsuario(42L)
                .tipoCertificado(TipoCertificadoEnum.PARTICIPACION)
                .urlCertificado("https://cert.pdf")
                .build();

        certPresentacion = Certificado.builder()
                .idCertificado(2L)
                .idCongreso(10L)
                .idUsuario(42L)
                .tipoCertificado(TipoCertificadoEnum.PRESENTACION)
                .idActividad(5L)
                .urlCertificado("https://cert2.pdf")
                .build();

        reqParticipacion = CertificadoRequest.builder()
                .idCongreso(10L)
                .idUsuario(42L)
                .tipoCertificado(TipoCertificadoEnum.PARTICIPACION)
                .urlCertificado("https://cert.pdf")
                .build();

        reqPresentacion = CertificadoRequest.builder()
                .idCongreso(10L)
                .idUsuario(42L)
                .tipoCertificado(TipoCertificadoEnum.PRESENTACION)
                .idActividad(5L)
                .urlCertificado("https://cert2.pdf")
                .build();
    }

    @Test
    void generarParticipacionExitosoRetornaResponse()
            throws RecursoNoEncontradoException {

        when(repositorio.existsByIdCongresoAndIdUsuarioAndTipoCertificado(
                10L, 42L, TipoCertificadoEnum.PARTICIPACION
        )).thenReturn(false);

        when(asistenciaRepositorio.countActividadesAsistidasPorCongresoYUsuario(
                10L, 42L
        )).thenReturn(3L);

        when(repositorio.save(any(Certificado.class)))
                .thenReturn(certParticipacion);

        CertificadoResponse r = servicio.generar(reqParticipacion);

        assertNotNull(r);
        assertEquals(TipoCertificadoEnum.PARTICIPACION, r.getTipoCertificado());

        verify(repositorio).existsByIdCongresoAndIdUsuarioAndTipoCertificado(
                10L, 42L, TipoCertificadoEnum.PARTICIPACION
        );
        verify(asistenciaRepositorio).countActividadesAsistidasPorCongresoYUsuario(
                10L, 42L
        );
        verify(repositorio).save(any(Certificado.class));
    }

    @Test
    void generarParticipacionConMenosDeTresAsistenciasLanzaIllegalState() {
        when(repositorio.existsByIdCongresoAndIdUsuarioAndTipoCertificado(
                10L, 42L, TipoCertificadoEnum.PARTICIPACION
        )).thenReturn(false);

        when(asistenciaRepositorio.countActividadesAsistidasPorCongresoYUsuario(
                10L, 42L
        )).thenReturn(2L);

        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> servicio.generar(reqParticipacion)
        );

        assertTrue(ex.getMessage().contains("al menos 3 actividades"));

        verify(repositorio).existsByIdCongresoAndIdUsuarioAndTipoCertificado(
                10L, 42L, TipoCertificadoEnum.PARTICIPACION
        );
        verify(asistenciaRepositorio).countActividadesAsistidasPorCongresoYUsuario(
                10L, 42L
        );
        verify(repositorio, never()).save(any(Certificado.class));
    }

    @Test
    void generarParticipacionDuplicadaLanzaIllegalState() {
        when(repositorio.existsByIdCongresoAndIdUsuarioAndTipoCertificado(
                10L, 42L, TipoCertificadoEnum.PARTICIPACION
        )).thenReturn(true);

        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> servicio.generar(reqParticipacion)
        );

        assertTrue(ex.getMessage().contains("PARTICIPACION"));

        verify(repositorio).existsByIdCongresoAndIdUsuarioAndTipoCertificado(
                10L, 42L, TipoCertificadoEnum.PARTICIPACION
        );
        verifyNoInteractions(asistenciaRepositorio);
        verify(repositorio, never()).save(any(Certificado.class));
    }

    @Test
    void generarPresentacionExitosoRetornaResponse()
            throws RecursoNoEncontradoException {

        when(repositorio.existsByIdActividadAndIdUsuarioAndTipoCertificado(
                5L, 42L, TipoCertificadoEnum.PRESENTACION
        )).thenReturn(false);

        when(repositorio.save(any(Certificado.class)))
                .thenReturn(certPresentacion);

        CertificadoResponse r = servicio.generar(reqPresentacion);

        assertNotNull(r);
        assertEquals(TipoCertificadoEnum.PRESENTACION, r.getTipoCertificado());
        assertEquals(5L, r.getIdActividad());

        verify(repositorio).existsByIdActividadAndIdUsuarioAndTipoCertificado(
                5L, 42L, TipoCertificadoEnum.PRESENTACION
        );
        verifyNoInteractions(asistenciaRepositorio);
        verify(repositorio).save(any(Certificado.class));
    }

    @Test
    void generarPresentacionSinActividadLanzaIllegalArgument() {
        reqPresentacion.setIdActividad(null);

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> servicio.generar(reqPresentacion)
        );

        assertTrue(ex.getMessage().contains("PRESENTACION"));

        verifyNoInteractions(asistenciaRepositorio);
        verify(repositorio, never()).save(any(Certificado.class));
    }

    @Test
    void generarPresentacionDuplicadaLanzaIllegalState() {
        when(repositorio.existsByIdActividadAndIdUsuarioAndTipoCertificado(
                5L, 42L, TipoCertificadoEnum.PRESENTACION
        )).thenReturn(true);

        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> servicio.generar(reqPresentacion)
        );

        assertTrue(ex.getMessage().contains("PRESENTACION"));

        verifyNoInteractions(asistenciaRepositorio);
        verify(repositorio, never()).save(any(Certificado.class));
    }

    @Test
    void obtenerPorIdExisteRetornaResponse()
            throws RecursoNoEncontradoException {

        when(repositorio.findById(1L))
                .thenReturn(Optional.of(certParticipacion));

        CertificadoResponse r = servicio.obtenerPorId(1L);

        assertEquals(1L, r.getIdCertificado());
    }

    @Test
    void obtenerPorIdNoExisteLanzaExcepcion() {
        when(repositorio.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                RecursoNoEncontradoException.class,
                () -> servicio.obtenerPorId(99L)
        );
    }

    @Test
    void listarPorUsuarioRetornaLista() {
        when(repositorio.findByIdUsuario(42L))
                .thenReturn(List.of(certParticipacion, certPresentacion));

        List<CertificadoResponse> r = servicio.listarPorUsuario(42L);

        assertEquals(2, r.size());
    }

    @Test
    void listarPorCongresoRetornaLista() {
        when(repositorio.findByIdCongreso(10L))
                .thenReturn(List.of(certParticipacion));

        List<CertificadoResponse> r = servicio.listarPorCongreso(10L);

        assertEquals(1, r.size());
    }

    @Test
    void listarPorCongresoYUsuarioRetornaLista() {
        when(repositorio.findByIdCongresoAndIdUsuario(10L, 42L))
                .thenReturn(List.of(certParticipacion, certPresentacion));

        List<CertificadoResponse> r =
                servicio.listarPorCongresoYUsuario(10L, 42L);

        assertEquals(2, r.size());
    }

    @Test
    void actualizarCertificadoParticipacionExitosoRetornaResponse()
            throws RecursoNoEncontradoException {

        CertificadoUpdateRequest updateRequest = CertificadoUpdateRequest.builder()
                .idCongreso(20L)
                .idUsuario(99L)
                .tipoCertificado(TipoCertificadoEnum.PARTICIPACION)
                .urlCertificado("https://nueva-url.pdf")
                .build();

        Certificado certActualizado = Certificado.builder()
                .idCertificado(1L)
                .idCongreso(20L)
                .idUsuario(99L)
                .tipoCertificado(TipoCertificadoEnum.PARTICIPACION)
                .urlCertificado("https://nueva-url.pdf")
                .build();

        when(repositorio.findById(1L))
                .thenReturn(Optional.of(certParticipacion));

        when(repositorio.save(any(Certificado.class)))
                .thenReturn(certActualizado);

        CertificadoResponse r = servicio.actualizar(1L, updateRequest);

        assertNotNull(r);
        assertEquals(20L, r.getIdCongreso());
        assertEquals(99L, r.getIdUsuario());
        assertEquals("https://nueva-url.pdf", r.getUrlCertificado());

        verify(repositorio).save(any(Certificado.class));
    }

    @Test
    void actualizarCertificadoPresentacionConActividadExitoso()
            throws RecursoNoEncontradoException {

        CertificadoUpdateRequest updateRequest = CertificadoUpdateRequest.builder()
                .idCongreso(10L)
                .idUsuario(42L)
                .tipoCertificado(TipoCertificadoEnum.PRESENTACION)
                .idActividad(7L)
                .urlCertificado("https://nueva-url.pdf")
                .build();

        Certificado certActualizado = Certificado.builder()
                .idCertificado(2L)
                .idCongreso(10L)
                .idUsuario(42L)
                .tipoCertificado(TipoCertificadoEnum.PRESENTACION)
                .idActividad(7L)
                .urlCertificado("https://nueva-url.pdf")
                .build();

        when(repositorio.findById(2L))
                .thenReturn(Optional.of(certPresentacion));

        when(repositorio.save(any(Certificado.class)))
                .thenReturn(certActualizado);

        CertificadoResponse r = servicio.actualizar(2L, updateRequest);

        assertNotNull(r);
        assertEquals(TipoCertificadoEnum.PRESENTACION, r.getTipoCertificado());
        assertEquals(7L, r.getIdActividad());
    }

    @Test
    void actualizarCertificadoPresentacionSinActividadLanzaIllegalArgument() {
        CertificadoUpdateRequest updateRequest = CertificadoUpdateRequest.builder()
                .idCongreso(10L)
                .idUsuario(42L)
                .tipoCertificado(TipoCertificadoEnum.PRESENTACION)
                .idActividad(null)
                .build();

        when(repositorio.findById(1L))
                .thenReturn(Optional.of(certParticipacion));

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> servicio.actualizar(1L, updateRequest)
        );

        assertTrue(ex.getMessage().contains("PRESENTACION"));

        verify(repositorio, never()).save(any(Certificado.class));
    }

    @Test
    void actualizarCertificadoNoExisteLanzaExcepcion() {
        CertificadoUpdateRequest updateRequest = CertificadoUpdateRequest.builder()
                .idCongreso(10L)
                .idUsuario(42L)
                .tipoCertificado(TipoCertificadoEnum.PARTICIPACION)
                .build();

        when(repositorio.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                RecursoNoEncontradoException.class,
                () -> servicio.actualizar(99L, updateRequest)
        );

        verify(repositorio, never()).save(any(Certificado.class));
    }

    @Test
    void actualizarCertificadoUrlNullRetornaResponse()
            throws RecursoNoEncontradoException {

        CertificadoUpdateRequest updateRequest = CertificadoUpdateRequest.builder()
                .idCongreso(10L)
                .idUsuario(42L)
                .tipoCertificado(TipoCertificadoEnum.PARTICIPACION)
                .urlCertificado(null)
                .build();

        when(repositorio.findById(1L))
                .thenReturn(Optional.of(certParticipacion));

        when(repositorio.save(any(Certificado.class)))
                .thenReturn(certParticipacion);

        CertificadoResponse r = servicio.actualizar(1L, updateRequest);

        assertNotNull(r);
        verify(repositorio).save(any(Certificado.class));
    }
}