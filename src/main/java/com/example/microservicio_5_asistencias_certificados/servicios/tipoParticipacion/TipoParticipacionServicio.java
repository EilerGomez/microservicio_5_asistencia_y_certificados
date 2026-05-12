/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.example.microservicio_5_asistencias_certificados.servicios.tipoParticipacion;

import com.example.microservicio_5_asistencias_certificados.dtos.tipoParticipacion.TipoParticipacionResponse;
import com.example.microservicio_5_asistencias_certificados.excepciones.RecursoNoEncontradoException;
import java.util.List;

/**
 *
 * @author eiler
 */
public interface TipoParticipacionServicio {
    List<TipoParticipacionResponse> listarTodos();
    TipoParticipacionResponse obtenerPorId(Integer id) throws RecursoNoEncontradoException;
}