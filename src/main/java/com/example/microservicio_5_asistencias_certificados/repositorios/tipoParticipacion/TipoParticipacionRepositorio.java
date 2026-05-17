/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.example.microservicio_5_asistencias_certificados.repositorios.tipoParticipacion;

import com.example.microservicio_5_asistencias_certificados.modelos.tipoParticipacion.TipoParticipacion;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author eiler
 */

public interface TipoParticipacionRepositorio
        extends JpaRepository<TipoParticipacion, Integer> {
}
