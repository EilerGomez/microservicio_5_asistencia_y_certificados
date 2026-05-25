package com.example.microservicio_5_asistencias_certificados.controladores.asistencia;

import com.example.microservicio_5_asistencias_certificados.dtos.asistencia.AsistenciaRequest;
import com.example.microservicio_5_asistencias_certificados.dtos.asistencia.AsistenciaResponse;
import com.example.microservicio_5_asistencias_certificados.dtos.asistencia.AsistenciaUpdateRequest;
import com.example.microservicio_5_asistencias_certificados.excepciones.RecursoNoEncontradoException;
import com.example.microservicio_5_asistencias_certificados.modelos.tipoParticipacion.TipoParticipacionEnum;
import com.example.microservicio_5_asistencias_certificados.servicios.asistencia.AsistenciaServicio;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
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

    @Mock
    private AsistenciaServicio servicio;

    @InjectMocks
    private AsistenciaControlador controlador;

    private AsistenciaRequest request;
    private AsistenciaResponse response;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        "2",
                        null,
                        List.of(new SimpleGrantedAuthority("ROLE_ADMIN_CONGRESO"))
                )
        );

        request = AsistenciaRequest.builder()
                .idActividad(10L)
                .idCongreso(100L)
                .idUsuario(42L)
                .idTipoParticipacion(1)
                .build();

        response = AsistenciaResponse.builder()
                .idAsistencia(1L)
                .idActividad(10L)
                .idUsuario(42L)
                .idTipoParticipacion(1)
                .nombreTipoParticipacion("ASISTENTE")
                .registradoPor(2L)
                .build();
    }

    @AfterEach
    void limpiarContexto() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void registrarRetorna201() throws RecursoNoEncontradoException {
        when(servicio.registrar(any(AsistenciaRequest.class), eq(2L)))
                .thenReturn(response);

        ResponseEntity<AsistenciaResponse> r = controlador.registrar(request);

        assertEquals(HttpStatus.CREATED, r.getStatusCode());
        assertNotNull(r.getBody());
        assertEquals(42L, r.getBody().getIdUsuario());

        verify(servicio).registrar(any(AsistenciaRequest.class), eq(2L));
    }

    @Test
    void registrarEnviaIdCongresoAlServicio() throws RecursoNoEncontradoException {
        when(servicio.registrar(any(AsistenciaRequest.class), eq(2L)))
                .thenReturn(response);

        controlador.registrar(request);

        ArgumentCaptor<AsistenciaRequest> captor =
                ArgumentCaptor.forClass(AsistenciaRequest.class);

        verify(servicio).registrar(captor.capture(), eq(2L));

        AsistenciaRequest requestEnviado = captor.getValue();

        assertEquals(10L, requestEnviado.getIdActividad());
        assertEquals(100L, requestEnviado.getIdCongreso());
        assertEquals(42L, requestEnviado.getIdUsuario());
        assertEquals(1, requestEnviado.getIdTipoParticipacion());
    }

    @Test
    void registrarDuplicadoPropagaIllegalState() throws RecursoNoEncontradoException {
        when(servicio.registrar(any(AsistenciaRequest.class), eq(2L)))
                .thenThrow(new IllegalStateException("ya tiene asistencia"));

        assertThrows(
                IllegalStateException.class,
                () -> controlador.registrar(request)
        );

        verify(servicio).registrar(any(AsistenciaRequest.class), eq(2L));
    }

    @Test
    void registrarTipoNoExisteLanzaExcepcion() throws RecursoNoEncontradoException {
        when(servicio.registrar(any(AsistenciaRequest.class), eq(2L)))
                .thenThrow(new RecursoNoEncontradoException("Tipo 99 no encontrado"));

        assertThrows(
                RecursoNoEncontradoException.class,
                () -> controlador.registrar(request)
        );

        verify(servicio).registrar(any(AsistenciaRequest.class), eq(2L));
    }

    @Test
    void obtenerPorIdExisteRetorna200() throws RecursoNoEncontradoException {
        when(servicio.obtenerPorId(1L))
                .thenReturn(response);

        ResponseEntity<AsistenciaResponse> r = controlador.obtenerPorId(1L);

        assertEquals(HttpStatus.OK, r.getStatusCode());
        assertNotNull(r.getBody());
        assertEquals(1L, r.getBody().getIdAsistencia());

        verify(servicio).obtenerPorId(1L);
    }

    @Test
    void obtenerPorIdNoExisteLanzaExcepcion() throws RecursoNoEncontradoException {
        when(servicio.obtenerPorId(99L))
                .thenThrow(new RecursoNoEncontradoException("99"));

        assertThrows(
                RecursoNoEncontradoException.class,
                () -> controlador.obtenerPorId(99L)
        );

        verify(servicio).obtenerPorId(99L);
    }

    @Test
    void listarPorActividadRetorna200() {
        when(servicio.listarPorActividad(10L))
                .thenReturn(List.of(response));

        ResponseEntity<List<AsistenciaResponse>> r =
                controlador.listarPorActividad(10L);

        assertEquals(HttpStatus.OK, r.getStatusCode());
        assertNotNull(r.getBody());
        assertEquals(1, r.getBody().size());

        verify(servicio).listarPorActividad(10L);
    }

    @Test
    void listarPorActividadVaciaRetornaListaVacia() {
        when(servicio.listarPorActividad(99L))
                .thenReturn(List.of());

        ResponseEntity<List<AsistenciaResponse>> r =
                controlador.listarPorActividad(99L);

        assertEquals(HttpStatus.OK, r.getStatusCode());
        assertNotNull(r.getBody());
        assertTrue(r.getBody().isEmpty());

        verify(servicio).listarPorActividad(99L);
    }

    @Test
    void listarMisAsistenciasRetorna200() {
        when(servicio.listarPorUsuario(2L))
                .thenReturn(List.of(response));

        ResponseEntity<List<AsistenciaResponse>> r =
                controlador.listarMisAsistencias();

        assertEquals(HttpStatus.OK, r.getStatusCode());
        assertNotNull(r.getBody());
        assertEquals(1, r.getBody().size());

        verify(servicio).listarPorUsuario(2L);
    }

    @Test
    void existeAsistenciaTrueRetorna200() {
        when(servicio.existeAsistencia(10L, 42L))
                .thenReturn(true);

        ResponseEntity<Boolean> r =
                controlador.existeAsistencia(10L, 42L);

        assertEquals(HttpStatus.OK, r.getStatusCode());
        assertTrue(r.getBody());

        verify(servicio).existeAsistencia(10L, 42L);
    }

    @Test
    void existeAsistenciaFalseRetorna200() {
        when(servicio.existeAsistencia(10L, 99L))
                .thenReturn(false);

        ResponseEntity<Boolean> r =
                controlador.existeAsistencia(10L, 99L);

        assertEquals(HttpStatus.OK, r.getStatusCode());
        assertFalse(r.getBody());

        verify(servicio).existeAsistencia(10L, 99L);
    }

    @Test
    void actualizarRetorna200() throws RecursoNoEncontradoException {
        AsistenciaUpdateRequest updateRequest = AsistenciaUpdateRequest.builder()
                .idActividad(20L)
                .idUsuario(99L)
                .idTipoParticipacion(TipoParticipacionEnum.PONENTE.getId())
                .build();

        AsistenciaResponse resActualizada = AsistenciaResponse.builder()
                .idAsistencia(1L)
                .idActividad(20L)
                .idUsuario(99L)
                .idTipoParticipacion(TipoParticipacionEnum.PONENTE.getId())
                .nombreTipoParticipacion(TipoParticipacionEnum.PONENTE.name())
                .registradoPor(2L)
                .build();

        when(servicio.actualizar(eq(1L), any(AsistenciaUpdateRequest.class)))
                .thenReturn(resActualizada);

        ResponseEntity<AsistenciaResponse> r =
                controlador.actualizar(1L, updateRequest);

        assertEquals(HttpStatus.OK, r.getStatusCode());
        assertNotNull(r.getBody());
        assertEquals(20L, r.getBody().getIdActividad());
        assertEquals(99L, r.getBody().getIdUsuario());
        assertEquals(
                TipoParticipacionEnum.PONENTE.name(),
                r.getBody().getNombreTipoParticipacion()
        );

        verify(servicio).actualizar(eq(1L), any(AsistenciaUpdateRequest.class));
    }

    @Test
    void actualizarNoExisteLanzaExcepcion() throws RecursoNoEncontradoException {
        AsistenciaUpdateRequest updateRequest = AsistenciaUpdateRequest.builder()
                .idActividad(20L)
                .idUsuario(99L)
                .idTipoParticipacion(TipoParticipacionEnum.PONENTE.getId())
                .build();

        when(servicio.actualizar(eq(99L), any(AsistenciaUpdateRequest.class)))
                .thenThrow(new RecursoNoEncontradoException("99"));

        assertThrows(
                RecursoNoEncontradoException.class,
                () -> controlador.actualizar(99L, updateRequest)
        );

        verify(servicio).actualizar(eq(99L), any(AsistenciaUpdateRequest.class));
    }

    @Test
    void actualizarTipoNoExisteLanzaExcepcion() throws RecursoNoEncontradoException {
        AsistenciaUpdateRequest updateRequest = AsistenciaUpdateRequest.builder()
                .idActividad(20L)
                .idUsuario(99L)
                .idTipoParticipacion(99)
                .build();

        when(servicio.actualizar(eq(1L), any(AsistenciaUpdateRequest.class)))
                .thenThrow(new RecursoNoEncontradoException("Tipo 99 no encontrado"));

        assertThrows(
                RecursoNoEncontradoException.class,
                () -> controlador.actualizar(1L, updateRequest)
        );

        verify(servicio).actualizar(eq(1L), any(AsistenciaUpdateRequest.class));
    }
}