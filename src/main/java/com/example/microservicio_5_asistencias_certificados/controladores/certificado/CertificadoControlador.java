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
import com.example.microservicio_5_asistencias_certificados.dtos.certificado.CertificadoUpdateRequest;
import com.example.microservicio_5_asistencias_certificados.excepciones.RecursoNoEncontradoException;
import com.example.microservicio_5_asistencias_certificados.servicios.certificado.CertificadoServicio;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/certificados")
public class CertificadoControlador {

    private final CertificadoServicio servicio;

    public CertificadoControlador(CertificadoServicio servicio) {
        this.servicio = servicio;
    }

    private Long resolverIdUsuario() {
        String principal = (String) SecurityContextHolder
                .getContext().getAuthentication().getPrincipal();
        return Long.parseLong(principal);
    }

    // POST — generar certificado (ADMIN_CONGRESO / ADMIN_SISTEMA)
    @PostMapping
    @PreAuthorize("hasRole('ADMIN_CONGRESO') or hasRole('ADMIN_SISTEMA')")
    public ResponseEntity<CertificadoResponse> generar(
            @Valid @RequestBody CertificadoRequest request)
            throws RecursoNoEncontradoException {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(servicio.generar(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CertificadoResponse> obtenerPorId(
            @PathVariable Long id) throws RecursoNoEncontradoException {
        return ResponseEntity.ok(servicio.obtenerPorId(id));
    }

    @GetMapping("/mis-certificados")
    public ResponseEntity<List<CertificadoResponse>> listarMisCertificados() {
        return ResponseEntity.ok(servicio.listarPorUsuario(resolverIdUsuario()));
    }

    @GetMapping("/congreso/{idCongreso}")
    public ResponseEntity<List<CertificadoResponse>> listarPorCongreso(
            @PathVariable Long idCongreso) {
        return ResponseEntity.ok(servicio.listarPorCongreso(idCongreso));
    }

    @GetMapping("/congreso/{idCongreso}/usuario/{idUsuario}")
    public ResponseEntity<List<CertificadoResponse>> listarPorCongresoYUsuario(
            @PathVariable Long idCongreso,
            @PathVariable Long idUsuario) {
        return ResponseEntity.ok(
                servicio.listarPorCongresoYUsuario(idCongreso, idUsuario));
    }
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN_CONGRESO') or hasRole('ADMIN_SISTEMA')")
    public ResponseEntity<CertificadoResponse> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody CertificadoUpdateRequest request)
            throws RecursoNoEncontradoException {
        return ResponseEntity.ok(servicio.actualizar(id, request));
    }
}