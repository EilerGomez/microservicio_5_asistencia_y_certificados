/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.microservicio_5_asistencias_certificados.controladores.tipoParticipacion;

import com.example.microservicio_5_asistencias_certificados.dtos.tipoParticipacion.TipoParticipacionResponse;
import com.example.microservicio_5_asistencias_certificados.excepciones.RecursoNoEncontradoException;
import com.example.microservicio_5_asistencias_certificados.servicios.tipoParticipacion.TipoParticipacionServicio;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author eiler
 */
@RestController
@RequestMapping("/api/v1/tipos-participacion")
public class TipoParticipacionControlador {

    private final TipoParticipacionServicio servicio;

    public TipoParticipacionControlador(TipoParticipacionServicio servicio) {
        this.servicio = servicio;
    }

    @GetMapping
    public ResponseEntity<List<TipoParticipacionResponse>> listarTodos() {
        return ResponseEntity.ok(servicio.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TipoParticipacionResponse> obtenerPorId(
            @PathVariable Integer id) throws RecursoNoEncontradoException {
        return ResponseEntity.ok(servicio.obtenerPorId(id));
    }
}
