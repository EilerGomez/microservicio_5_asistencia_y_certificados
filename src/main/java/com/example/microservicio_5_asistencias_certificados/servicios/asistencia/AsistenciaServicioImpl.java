/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.microservicio_5_asistencias_certificados.servicios.asistencia;

import com.example.microservicio_5_asistencias_certificados.dtos.asistencia.AsistenciaRequest;
import com.example.microservicio_5_asistencias_certificados.dtos.asistencia.AsistenciaResponse;
import com.example.microservicio_5_asistencias_certificados.dtos.asistencia.AsistenciaUpdateRequest;
import com.example.microservicio_5_asistencias_certificados.excepciones.RecursoNoEncontradoException;
import com.example.microservicio_5_asistencias_certificados.modelos.asistencia.Asistencia;
import com.example.microservicio_5_asistencias_certificados.modelos.tipoParticipacion.TipoParticipacion;
import com.example.microservicio_5_asistencias_certificados.repositorios.asistencia.AsistenciaRepositorio;
import com.example.microservicio_5_asistencias_certificados.repositorios.tipoParticipacion.TipoParticipacionRepositorio;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author eiler
 */
@Service
public class AsistenciaServicioImpl implements AsistenciaServicio {

    private final AsistenciaRepositorio        asistenciaRepositorio;
    private final TipoParticipacionRepositorio tipoRepositorio;

    public AsistenciaServicioImpl(AsistenciaRepositorio asistenciaRepositorio,
                                  TipoParticipacionRepositorio tipoRepositorio) {
        this.asistenciaRepositorio = asistenciaRepositorio;
        this.tipoRepositorio       = tipoRepositorio;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AsistenciaResponse registrar(AsistenciaRequest request, Long registradoPor)
            throws RecursoNoEncontradoException {

        // Un usuario solo puede registrarse una vez por actividad
        if (asistenciaRepositorio.existsByIdActividadAndIdUsuario(
                request.getIdActividad(), request.getIdUsuario())) {
            throw new IllegalStateException(
                    "El usuario ya tiene asistencia registrada en esta actividad");
        }

        TipoParticipacion tipo = tipoRepositorio
                .findById(request.getIdTipoParticipacion())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Tipo de participación con ID " +
                        request.getIdTipoParticipacion() + " no encontrado"));

        Asistencia asistencia = Asistencia.builder()
                .idActividad(request.getIdActividad())
                .idUsuario(request.getIdUsuario())
                .tipoParticipacion(tipo)
                .registradoPor(registradoPor)
                .build();

        return new AsistenciaResponse(asistenciaRepositorio.save(asistencia));
    }

    @Override
    @Transactional(readOnly = true)
    public AsistenciaResponse obtenerPorId(Long id)
            throws RecursoNoEncontradoException {
        return asistenciaRepositorio.findById(id)
                .map(AsistenciaResponse::new)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Asistencia con ID " + id + " no encontrada"));
    }

    @Override
    @Transactional(readOnly = true)
    public List<AsistenciaResponse> listarPorActividad(Long idActividad) {
        return asistenciaRepositorio.findByIdActividad(idActividad)
                .stream()
                .map(AsistenciaResponse::new)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AsistenciaResponse> listarPorUsuario(Long idUsuario) {
        return asistenciaRepositorio.findByIdUsuario(idUsuario)
                .stream()
                .map(AsistenciaResponse::new)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existeAsistencia(Long idActividad, Long idUsuario) {
        return asistenciaRepositorio
                .existsByIdActividadAndIdUsuario(idActividad, idUsuario);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public AsistenciaResponse actualizar(Long id, AsistenciaUpdateRequest request)
            throws RecursoNoEncontradoException {

        Asistencia asistencia = asistenciaRepositorio.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Asistencia con ID " + id + " no encontrada"));

        TipoParticipacion tipo = tipoRepositorio
                .findById(request.getIdTipoParticipacion())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Tipo de participación con ID " +
                        request.getIdTipoParticipacion() + " no encontrado"));

        asistencia.setIdActividad(request.getIdActividad());
        asistencia.setIdUsuario(request.getIdUsuario());
        asistencia.setTipoParticipacion(tipo);

        return new AsistenciaResponse(asistenciaRepositorio.save(asistencia));
    }
}
