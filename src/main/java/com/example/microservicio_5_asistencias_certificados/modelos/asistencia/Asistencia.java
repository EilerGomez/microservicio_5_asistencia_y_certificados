/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.microservicio_5_asistencias_certificados.modelos.asistencia;

/**
 *
 * @author eiler
 */

import com.example.microservicio_5_asistencias_certificados.modelos.tipoParticipacion.TipoParticipacion;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "asistencia")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Asistencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_asistencia")
    private Long idAsistencia;

    @Column(name = "id_actividad", nullable = false)
    private Long idActividad;       // FK lógica a ms-actividades

    @Column(name = "id_usuario", nullable = false)
    private Long idUsuario;         // FK lógica a ms-auth

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_tipo_participacion", nullable = false)
    private TipoParticipacion tipoParticipacion;

    @Column(name = "registrado_por", nullable = false)
    private Long registradoPor;     // FK lógica a ms-auth

    @Column(name = "registrado_en", updatable = false, insertable = false,
            columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime registradoEn;
}