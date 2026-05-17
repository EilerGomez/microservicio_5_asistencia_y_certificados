/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.microservicio_5_asistencias_certificados.dtos.certificado;

/**
 *
 * @author eiler
 */


import com.example.microservicio_5_asistencias_certificados.modelos.certificado.TipoCertificadoEnum;
import com.example.microservicio_5_asistencias_certificados.modelos.certificado.Certificado;
import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CertificadoResponse {

    private Long               idCertificado;
    private Long               idCongreso;
    private Long               idUsuario;
    private TipoCertificadoEnum tipoCertificado;
    private Long               idActividad;
    private String             urlCertificado;
    private LocalDateTime      generadoEn;

    public CertificadoResponse(Certificado c) {
        this.idCertificado   = c.getIdCertificado();
        this.idCongreso      = c.getIdCongreso();
        this.idUsuario       = c.getIdUsuario();
        this.tipoCertificado = c.getTipoCertificado();
        this.idActividad     = c.getIdActividad();
        this.urlCertificado  = c.getUrlCertificado();
        this.generadoEn      = c.getGeneradoEn();
    }
}