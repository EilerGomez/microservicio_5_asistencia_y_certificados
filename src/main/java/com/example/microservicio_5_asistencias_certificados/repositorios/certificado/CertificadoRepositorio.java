/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.example.microservicio_5_asistencias_certificados.repositorios.certificado;

/**
 *
 * @author eiler
 */


import com.example.microservicio_5_asistencias_certificados.modelos.certificado.Certificado;
import com.example.microservicio_5_asistencias_certificados.modelos.certificado.TipoCertificadoEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface CertificadoRepositorio extends JpaRepository<Certificado, Long> {

    List<Certificado> findByIdUsuario(Long idUsuario);

    List<Certificado> findByIdCongreso(Long idCongreso);

    List<Certificado> findByIdCongresoAndIdUsuario(Long idCongreso, Long idUsuario);

    Optional<Certificado> findByIdCongresoAndIdUsuarioAndTipoCertificado(
            Long idCongreso, Long idUsuario, TipoCertificadoEnum tipo);

    boolean existsByIdCongresoAndIdUsuarioAndTipoCertificado(
            Long idCongreso, Long idUsuario, TipoCertificadoEnum tipo);

    boolean existsByIdActividadAndIdUsuarioAndTipoCertificado(
            Long idActividad, Long idUsuario, TipoCertificadoEnum tipo);
}