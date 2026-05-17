/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.microservicio_5_asistencias_certificados.servicios.asistencia;

import com.example.microservicio_5_asistencias_certificados.dtos.asistencia.AsistenciaRequest;
import com.example.microservicio_5_asistencias_certificados.dtos.asistencia.AsistenciaResponse;
import com.example.microservicio_5_asistencias_certificados.excepciones.RecursoNoEncontradoException;
import java.util.List;

/**
 *
 * @author eiler
 */
public interface AsistenciaServicio {

    AsistenciaResponse registrar(AsistenciaRequest request, Long registradoPor)
            throws RecursoNoEncontradoException;

    AsistenciaResponse obtenerPorId(Long id) throws RecursoNoEncontradoException;

    List<AsistenciaResponse> listarPorActividad(Long idActividad);

    List<AsistenciaResponse> listarPorUsuario(Long idUsuario);

    boolean existeAsistencia(Long idActividad, Long idUsuario);
}