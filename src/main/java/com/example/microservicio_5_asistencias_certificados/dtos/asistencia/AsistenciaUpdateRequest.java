/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.microservicio_5_asistencias_certificados.dtos.asistencia;


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
public class AsistenciaUpdateRequest {

    @NotNull(message = "El id de la actividad es obligatorio")
    private Long idActividad;

    @NotNull(message = "El id del usuario es obligatorio")
    private Long idUsuario;

    @NotNull(message = "El tipo de participacion es obligatorio")
    private Integer idTipoParticipacion;
}
