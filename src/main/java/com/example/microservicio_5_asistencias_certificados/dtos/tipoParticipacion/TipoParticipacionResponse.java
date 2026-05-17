/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.microservicio_5_asistencias_certificados.dtos.tipoParticipacion;
import com.example.microservicio_5_asistencias_certificados.modelos.tipoParticipacion.TipoParticipacion;

/**
 *
 * @author eiler
 */
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TipoParticipacionResponse {

    private Integer idTipoParticipacion;
    private String  nombreTipo;

    public TipoParticipacionResponse(TipoParticipacion t) {
        this.idTipoParticipacion = t.getIdTipoParticipacion();
        this.nombreTipo          = t.getNombreTipo();
    }
}