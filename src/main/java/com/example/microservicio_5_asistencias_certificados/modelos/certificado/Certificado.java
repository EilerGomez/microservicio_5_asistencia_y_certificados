/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.microservicio_5_asistencias_certificados.modelos.certificado;

/**
 *
 * @author eiler
 */


import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "certificado")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Certificado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_certificado")
    private Long idCertificado;

    @Column(name = "id_congreso", nullable = false)
    private Long idCongreso;        // FK lógica a ms-congresos

    @Column(name = "id_usuario", nullable = false)
    private Long idUsuario;         // FK lógica a ms-auth

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_certificado", nullable = false, length = 50)
    private TipoCertificadoEnum tipoCertificado;

    @Column(name = "id_actividad")
    private Long idActividad;       // NULL si es diploma general FK lógica a ms-actividades

    @Column(name = "url_certificado", length = 500)
    private String urlCertificado;

    @Column(name = "generado_en", updatable = false, insertable = false,
            columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime generadoEn;
}
