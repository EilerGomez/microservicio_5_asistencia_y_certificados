/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.microservicio_5_asistencias_certificados.controladores.asistencia;

import com.example.microservicio_5_asistencias_certificados.dtos.asistencia.AsistenciaRequest;
import com.example.microservicio_5_asistencias_certificados.dtos.asistencia.AsistenciaResponse;
import com.example.microservicio_5_asistencias_certificados.dtos.asistencia.AsistenciaUpdateRequest;
import com.example.microservicio_5_asistencias_certificados.excepciones.RecursoNoEncontradoException;
import com.example.microservicio_5_asistencias_certificados.servicios.asistencia.AsistenciaServicio;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author eiler
 */
@RestController
@RequestMapping("/api/v1/asistencias")
public class AsistenciaControlador {

    private final AsistenciaServicio servicio;

    public AsistenciaControlador(AsistenciaServicio servicio) {
        this.servicio = servicio;
    }

    private Long resolverIdUsuario() {
        String principal = (String) SecurityContextHolder
                .getContext().getAuthentication().getPrincipal();
        return Long.parseLong(principal);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN_CONGRESO') or hasRole('ADMIN_SISTEMA')")
    public ResponseEntity<AsistenciaResponse> registrar(
            @Valid @RequestBody AsistenciaRequest request)
            throws RecursoNoEncontradoException {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(servicio.registrar(request, resolverIdUsuario()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AsistenciaResponse> obtenerPorId(
            @PathVariable Long id) throws RecursoNoEncontradoException {
        return ResponseEntity.ok(servicio.obtenerPorId(id));
    }

    @GetMapping("/actividad/{idActividad}")
    public ResponseEntity<List<AsistenciaResponse>> listarPorActividad(
            @PathVariable Long idActividad) {
        return ResponseEntity.ok(servicio.listarPorActividad(idActividad));
    }

    @GetMapping("/mis-asistencias")
    public ResponseEntity<List<AsistenciaResponse>> listarMisAsistencias() {
        return ResponseEntity.ok(servicio.listarPorUsuario(resolverIdUsuario()));
    }

    @GetMapping("/existe")
    public ResponseEntity<Boolean> existeAsistencia(
            @RequestParam Long idActividad,
            @RequestParam Long idUsuario) {
        return ResponseEntity.ok(servicio.existeAsistencia(idActividad, idUsuario));
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN_CONGRESO') or hasRole('ADMIN_SISTEMA')")
    public ResponseEntity<AsistenciaResponse> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody AsistenciaUpdateRequest request)
            throws RecursoNoEncontradoException {
        return ResponseEntity.ok(servicio.actualizar(id, request));
    }
}