/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.example.microservicio_5_asistencias_certificados.servicios.certificado;

/**
 *
 * @author eiler
 */


import com.example.microservicio_5_asistencias_certificados.dtos.certificado.CertificadoRequest;
import com.example.microservicio_5_asistencias_certificados.dtos.certificado.CertificadoResponse;
import com.example.microservicio_5_asistencias_certificados.dtos.certificado.CertificadoUpdateRequest;
import com.example.microservicio_5_asistencias_certificados.excepciones.RecursoNoEncontradoException;
import java.util.List;

public interface CertificadoServicio {

    CertificadoResponse generar(CertificadoRequest request)
            throws RecursoNoEncontradoException;

    CertificadoResponse obtenerPorId(Long id)
            throws RecursoNoEncontradoException;

    List<CertificadoResponse> listarPorUsuario(Long idUsuario);

    List<CertificadoResponse> listarPorCongreso(Long idCongreso);

    List<CertificadoResponse> listarPorCongresoYUsuario(Long idCongreso, Long idUsuario);
    
    CertificadoResponse actualizar(Long id, CertificadoUpdateRequest request)
            throws RecursoNoEncontradoException;
}
