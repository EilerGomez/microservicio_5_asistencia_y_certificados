/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.microservicio_5_asistencias_certificados.dtos.asistencia;

/**
 *
 * @author eiler
 */
import com.example.microservicio_5_asistencias_certificados.modelos.asistencia.Asistencia;
import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AsistenciaResponse {

    private Long          idAsistencia;
    private Long          idActividad;
    private Long          idUsuario;
    private Integer       idTipoParticipacion;
    private String        nombreTipoParticipacion;
    private Long          registradoPor;
    private LocalDateTime registradoEn;

    public AsistenciaResponse(Asistencia a) {
        this.idAsistencia            = a.getIdAsistencia();
        this.idActividad             = a.getIdActividad();
        this.idUsuario               = a.getIdUsuario();
        this.idTipoParticipacion     = a.getTipoParticipacion().getIdTipoParticipacion();
        this.nombreTipoParticipacion = a.getTipoParticipacion().getNombreTipo();
        this.registradoPor           = a.getRegistradoPor();
        this.registradoEn            = a.getRegistradoEn();
    }
}
