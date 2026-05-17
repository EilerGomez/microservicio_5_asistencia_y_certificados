/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.microservicio_5_asistencias_certificados.modelos.tipoParticipacion;

/**
 *
 * @author eiler
 */

public enum TipoParticipacionEnum {
    ASISTENTE(1),
    PONENTE(2),
    TALLERISTA(3),
    PONENTE_INVITADO(4);

    private final Integer id;

    TipoParticipacionEnum(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }
}
