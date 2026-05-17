/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.microservicio_5_asistencias_certificados.dtos.certificado;


import com.example.microservicio_5_asistencias_certificados.modelos.certificado.TipoCertificadoEnum;
import jakarta.validation.constraints.NotNull;
import lombok.*;
/**
 *
 * @author eiler
 */


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CertificadoUpdateRequest {

    @NotNull(message = "El id del congreso es obligatorio")
    private Long idCongreso;

    @NotNull(message = "El id del usuario es obligatorio")
    private Long idUsuario;

    @NotNull(message = "El tipo de certificado es obligatorio")
    private TipoCertificadoEnum tipoCertificado;

    // Solo requerido si tipoCertificado = PRESENTACION
    private Long idActividad;

    private String urlCertificado;
}
