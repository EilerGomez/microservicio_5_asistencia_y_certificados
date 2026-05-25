package com.example.microservicio_5_asistencias_certificados.servicios.asistencia;

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
import org.mockito.ArgumentCaptor;
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

    @Mock
    private AsistenciaRepositorio asistenciaRepositorio;

    @Mock
    private TipoParticipacionRepositorio tipoRepositorio;

    @InjectMocks
    private AsistenciaServicioImpl servicio;

    private TipoParticipacion tipoAsistente;
    private Asistencia asistencia;
    private AsistenciaRequest request;

    @BeforeEach
    void setUp() {
        tipoAsistente = TipoParticipacion.builder()
                .idTipoParticipacion(1)
                .nombreTipo("ASISTENTE")
                .build();

        asistencia = Asistencia.builder()
                .idAsistencia(1L)
                .idActividad(10L)
                .idCongreso(100L)
                .idUsuario(42L)
                .tipoParticipacion(tipoAsistente)
                .registradoPor(2L)
                .build();

        request = AsistenciaRequest.builder()
                .idActividad(10L)
                .idCongreso(100L)
                .idUsuario(42L)
                .idTipoParticipacion(1)
                .build();
    }

    @Test
    void registrarAsistenciaExitosaRetornaResponse()
            throws RecursoNoEncontradoException {

        when(asistenciaRepositorio.existsByIdActividadAndIdUsuario(10L, 42L))
                .thenReturn(false);

        when(tipoRepositorio.findById(1))
                .thenReturn(Optional.of(tipoAsistente));

        when(asistenciaRepositorio.save(any(Asistencia.class)))
                .thenReturn(asistencia);

        AsistenciaResponse r = servicio.registrar(request, 2L);

        assertNotNull(r);
        assertEquals(42L, r.getIdUsuario());
        assertEquals("ASISTENTE", r.getNombreTipoParticipacion());

        verify(asistenciaRepositorio).save(any(Asistencia.class));
    }

    @Test
    void registrarAsistenciaGuardaIdCongreso()
            throws RecursoNoEncontradoException {

        when(asistenciaRepositorio.existsByIdActividadAndIdUsuario(10L, 42L))
                .thenReturn(false);

        when(tipoRepositorio.findById(1))
                .thenReturn(Optional.of(tipoAsistente));

        when(asistenciaRepositorio.save(any(Asistencia.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        servicio.registrar(request, 2L);

        ArgumentCaptor<Asistencia> captor =
                ArgumentCaptor.forClass(Asistencia.class);

        verify(asistenciaRepositorio).save(captor.capture());

        Asistencia asistenciaGuardada = captor.getValue();

        assertEquals(10L, asistenciaGuardada.getIdActividad());
        assertEquals(100L, asistenciaGuardada.getIdCongreso());
        assertEquals(42L, asistenciaGuardada.getIdUsuario());
        assertEquals(2L, asistenciaGuardada.getRegistradoPor());
        assertEquals(tipoAsistente, asistenciaGuardada.getTipoParticipacion());
    }

    @Test
    void registrarAsistenciaDuplicadaLanzaIllegalState() {
        when(asistenciaRepositorio.existsByIdActividadAndIdUsuario(10L, 42L))
                .thenReturn(true);

        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> servicio.registrar(request, 2L)
        );

        assertTrue(ex.getMessage().contains("ya tiene asistencia"));

        verify(tipoRepositorio, never()).findById(any());
        verify(asistenciaRepositorio, never()).save(any(Asistencia.class));
    }

    @Test
    void registrarTipoNoExisteLanzaExcepcion() {
        when(asistenciaRepositorio.existsByIdActividadAndIdUsuario(10L, 42L))
                .thenReturn(false);

        when(tipoRepositorio.findById(1))
                .thenReturn(Optional.empty());

        assertThrows(
                RecursoNoEncontradoException.class,
                () -> servicio.registrar(request, 2L)
        );

        verify(asistenciaRepositorio, never()).save(any(Asistencia.class));
    }

    @Test
    void obtenerPorIdExisteRetornaResponse()
            throws RecursoNoEncontradoException {

        when(asistenciaRepositorio.findById(1L))
                .thenReturn(Optional.of(asistencia));

        AsistenciaResponse r = servicio.obtenerPorId(1L);

        assertEquals(1L, r.getIdAsistencia());
        assertEquals(42L, r.getIdUsuario());
    }

    @Test
    void obtenerPorIdNoExisteLanzaExcepcion() {
        when(asistenciaRepositorio.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                RecursoNoEncontradoException.class,
                () -> servicio.obtenerPorId(99L)
        );
    }

    @Test
    void listarPorActividadRetornaLista() {
        when(asistenciaRepositorio.findByIdActividad(10L))
                .thenReturn(List.of(asistencia));

        List<AsistenciaResponse> r = servicio.listarPorActividad(10L);

        assertEquals(1, r.size());
        assertEquals(10L, r.get(0).getIdActividad());
    }

    @Test
    void listarPorActividadVaciaRetornaListaVacia() {
        when(asistenciaRepositorio.findByIdActividad(99L))
                .thenReturn(List.of());

        List<AsistenciaResponse> r = servicio.listarPorActividad(99L);

        assertTrue(r.isEmpty());
    }

    @Test
    void listarPorUsuarioRetornaLista() {
        when(asistenciaRepositorio.findByIdUsuario(42L))
                .thenReturn(List.of(asistencia));

        List<AsistenciaResponse> r = servicio.listarPorUsuario(42L);

        assertEquals(1, r.size());
        assertEquals(42L, r.get(0).getIdUsuario());
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
                .nombreTipo(TipoParticipacionEnum.PONENTE.name())
                .build();

        AsistenciaUpdateRequest updateRequest = AsistenciaUpdateRequest.builder()
                .idActividad(20L)
                .idUsuario(99L)
                .idTipoParticipacion(TipoParticipacionEnum.PONENTE.getId())
                .build();

        Asistencia asistenciaActualizada = Asistencia.builder()
                .idAsistencia(1L)
                .idActividad(20L)
                .idCongreso(100L)
                .idUsuario(99L)
                .tipoParticipacion(ponente)
                .registradoPor(2L)
                .build();

        when(asistenciaRepositorio.findById(1L))
                .thenReturn(Optional.of(asistencia));

        when(tipoRepositorio.findById(TipoParticipacionEnum.PONENTE.getId()))
                .thenReturn(Optional.of(ponente));

        when(asistenciaRepositorio.save(any(Asistencia.class)))
                .thenReturn(asistenciaActualizada);

        AsistenciaResponse r = servicio.actualizar(1L, updateRequest);

        assertNotNull(r);
        assertEquals(20L, r.getIdActividad());
        assertEquals(99L, r.getIdUsuario());
        assertEquals(TipoParticipacionEnum.PONENTE.name(),
                r.getNombreTipoParticipacion());

        verify(asistenciaRepositorio).save(any(Asistencia.class));
    }

    @Test
    void actualizarAsistenciaNoExisteLanzaExcepcion() {
        AsistenciaUpdateRequest updateRequest = AsistenciaUpdateRequest.builder()
                .idActividad(20L)
                .idUsuario(99L)
                .idTipoParticipacion(TipoParticipacionEnum.PONENTE.getId())
                .build();

        when(asistenciaRepositorio.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                RecursoNoEncontradoException.class,
                () -> servicio.actualizar(99L, updateRequest)
        );

        verify(tipoRepositorio, never()).findById(any());
        verify(asistenciaRepositorio, never()).save(any(Asistencia.class));
    }

    @Test
    void actualizarAsistenciaTipoNoExisteLanzaExcepcion() {
        AsistenciaUpdateRequest updateRequest = AsistenciaUpdateRequest.builder()
                .idActividad(20L)
                .idUsuario(99L)
                .idTipoParticipacion(99)
                .build();

        when(asistenciaRepositorio.findById(1L))
                .thenReturn(Optional.of(asistencia));

        when(tipoRepositorio.findById(99))
                .thenReturn(Optional.empty());

        assertThrows(
                RecursoNoEncontradoException.class,
                () -> servicio.actualizar(1L, updateRequest)
        );

        verify(asistenciaRepositorio, never()).save(any(Asistencia.class));
    }

    @Test
    void actualizarAsistenciaCambiaActividadYUsuario()
            throws RecursoNoEncontradoException {

        AsistenciaUpdateRequest updateRequest = AsistenciaUpdateRequest.builder()
                .idActividad(50L)
                .idUsuario(77L)
                .idTipoParticipacion(TipoParticipacionEnum.TALLERISTA.getId())
                .build();

        TipoParticipacion tallerista = TipoParticipacion.builder()
                .idTipoParticipacion(TipoParticipacionEnum.TALLERISTA.getId())
                .nombreTipo(TipoParticipacionEnum.TALLERISTA.name())
                .build();

        Asistencia actualizada = Asistencia.builder()
                .idAsistencia(1L)
                .idActividad(50L)
                .idCongreso(100L)
                .idUsuario(77L)
                .tipoParticipacion(tallerista)
                .registradoPor(2L)
                .build();

        when(asistenciaRepositorio.findById(1L))
                .thenReturn(Optional.of(asistencia));

        when(tipoRepositorio.findById(TipoParticipacionEnum.TALLERISTA.getId()))
                .thenReturn(Optional.of(tallerista));

        when(asistenciaRepositorio.save(any(Asistencia.class)))
                .thenReturn(actualizada);

        AsistenciaResponse r = servicio.actualizar(1L, updateRequest);

        assertEquals(50L, r.getIdActividad());
        assertEquals(77L, r.getIdUsuario());
        assertEquals(TipoParticipacionEnum.TALLERISTA.name(),
                r.getNombreTipoParticipacion());
    }

    @Test
    void actualizarAsistenciaConservaIdCongresoAnterior()
            throws RecursoNoEncontradoException {

        AsistenciaUpdateRequest updateRequest = AsistenciaUpdateRequest.builder()
                .idActividad(30L)
                .idUsuario(55L)
                .idTipoParticipacion(TipoParticipacionEnum.PONENTE.getId())
                .build();

        TipoParticipacion ponente = TipoParticipacion.builder()
                .idTipoParticipacion(TipoParticipacionEnum.PONENTE.getId())
                .nombreTipo(TipoParticipacionEnum.PONENTE.name())
                .build();

        when(asistenciaRepositorio.findById(1L))
                .thenReturn(Optional.of(asistencia));

        when(tipoRepositorio.findById(TipoParticipacionEnum.PONENTE.getId()))
                .thenReturn(Optional.of(ponente));

        when(asistenciaRepositorio.save(any(Asistencia.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        servicio.actualizar(1L, updateRequest);

        ArgumentCaptor<Asistencia> captor =
                ArgumentCaptor.forClass(Asistencia.class);

        verify(asistenciaRepositorio).save(captor.capture());

        Asistencia asistenciaGuardada = captor.getValue();

        assertEquals(30L, asistenciaGuardada.getIdActividad());
        assertEquals(55L, asistenciaGuardada.getIdUsuario());
        assertEquals(100L, asistenciaGuardada.getIdCongreso());
        assertEquals(ponente, asistenciaGuardada.getTipoParticipacion());
    }
}