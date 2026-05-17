/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.microservicio_5_asistencias_certificados.servicios.tipoParticipacion;

import com.example.microservicio_5_asistencias_certificados.dtos.tipoParticipacion.TipoParticipacionResponse;
import com.example.microservicio_5_asistencias_certificados.excepciones.RecursoNoEncontradoException;
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
public class TipoParticipacionServicioImpl implements TipoParticipacionServicio {

    private final TipoParticipacionRepositorio repositorio;

    public TipoParticipacionServicioImpl(TipoParticipacionRepositorio repositorio) {
        this.repositorio = repositorio;
    }

    @Override
    @Transactional(readOnly = true)
    public List<TipoParticipacionResponse> listarTodos() {
        return repositorio.findAll()
                .stream()
                .map(TipoParticipacionResponse::new)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public TipoParticipacionResponse obtenerPorId(Integer id)
            throws RecursoNoEncontradoException {
        return repositorio.findById(id)
                .map(TipoParticipacionResponse::new)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Tipo de participación con ID " + id + " no encontrado"));
    }
}