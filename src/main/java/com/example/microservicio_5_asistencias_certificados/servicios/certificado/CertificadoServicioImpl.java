/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
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
import com.example.microservicio_5_asistencias_certificados.modelos.certificado.Certificado;
import com.example.microservicio_5_asistencias_certificados.modelos.certificado.TipoCertificadoEnum;
import com.example.microservicio_5_asistencias_certificados.repositorios.certificado.CertificadoRepositorio;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CertificadoServicioImpl implements CertificadoServicio {

    private final CertificadoRepositorio repositorio;

    public CertificadoServicioImpl(CertificadoRepositorio repositorio) {
        this.repositorio = repositorio;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CertificadoResponse generar(CertificadoRequest request)
            throws RecursoNoEncontradoException {

        // PRESENTACION requiere idActividad
        if (TipoCertificadoEnum.PRESENTACION.equals(request.getTipoCertificado())
                && request.getIdActividad() == null) {
            throw new IllegalArgumentException(
                    "El certificado de PRESENTACION requiere el id de la actividad");
        }

        // Verificar duplicado sehun el tipo
        if (TipoCertificadoEnum.PARTICIPACION.equals(request.getTipoCertificado())) {
            if (repositorio.existsByIdCongresoAndIdUsuarioAndTipoCertificado(
                    request.getIdCongreso(), request.getIdUsuario(),
                    TipoCertificadoEnum.PARTICIPACION)) {
                throw new IllegalStateException(
                        "El usuario ya tiene certificado de PARTICIPACION " +
                        "en este congreso");
            }
        } else {
            if (repositorio.existsByIdActividadAndIdUsuarioAndTipoCertificado(
                    request.getIdActividad(), request.getIdUsuario(),
                    TipoCertificadoEnum.PRESENTACION)) {
                throw new IllegalStateException(
                        "El usuario ya tiene certificado de PRESENTACION " +
                        "para esta actividad");
            }
        }

        Certificado certificado = Certificado.builder()
                .idCongreso(request.getIdCongreso())
                .idUsuario(request.getIdUsuario())
                .tipoCertificado(request.getTipoCertificado())
                .idActividad(request.getIdActividad())
                .urlCertificado(request.getUrlCertificado())
                .build();

        return new CertificadoResponse(repositorio.save(certificado));
    }

    @Override
    @Transactional(readOnly = true)
    public CertificadoResponse obtenerPorId(Long id)
            throws RecursoNoEncontradoException {
        return repositorio.findById(id)
                .map(CertificadoResponse::new)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Certificado con ID " + id + " no encontrado"));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CertificadoResponse> listarPorUsuario(Long idUsuario) {
        return repositorio.findByIdUsuario(idUsuario)
                .stream()
                .map(CertificadoResponse::new)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CertificadoResponse> listarPorCongreso(Long idCongreso) {
        return repositorio.findByIdCongreso(idCongreso)
                .stream()
                .map(CertificadoResponse::new)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CertificadoResponse> listarPorCongresoYUsuario(
            Long idCongreso, Long idUsuario) {
        return repositorio.findByIdCongresoAndIdUsuario(idCongreso, idUsuario)
                .stream()
                .map(CertificadoResponse::new)
                .collect(Collectors.toList());
    }
    

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CertificadoResponse actualizar(Long id, CertificadoUpdateRequest request)
            throws RecursoNoEncontradoException {

        Certificado certificado = repositorio.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Certificado con ID " + id + " no encontrado"));

        // PRESENTACION requiere idActividad
        if (TipoCertificadoEnum.PRESENTACION.equals(request.getTipoCertificado())
                && request.getIdActividad() == null) {
            throw new IllegalArgumentException(
                    "El certificado de PRESENTACION requiere el id de la actividad");
        }

        certificado.setIdCongreso(request.getIdCongreso());
        certificado.setIdUsuario(request.getIdUsuario());
        certificado.setTipoCertificado(request.getTipoCertificado());
        certificado.setIdActividad(request.getIdActividad());
        certificado.setUrlCertificado(request.getUrlCertificado());

        return new CertificadoResponse(repositorio.save(certificado));
    }
}
