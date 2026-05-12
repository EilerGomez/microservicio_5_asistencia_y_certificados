/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.example.microservicio_5_asistencias_certificados.repositorios.asistencia;

import com.example.microservicio_5_asistencias_certificados.modelos.asistencia.Asistencia;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author eiler
 */
public interface AsistenciaRepositorio extends JpaRepository<Asistencia, Long> {

    List<Asistencia> findByIdActividad(Long idActividad);

    List<Asistencia> findByIdUsuario(Long idUsuario);

    List<Asistencia> findByIdActividadAndIdUsuario(Long idActividad, Long idUsuario);

    Optional<Asistencia> findFirstByIdActividadAndIdUsuario(
            Long idActividad, Long idUsuario);

    boolean existsByIdActividadAndIdUsuario(Long idActividad, Long idUsuario);
}
