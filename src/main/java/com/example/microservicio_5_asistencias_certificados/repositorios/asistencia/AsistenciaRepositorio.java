/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.example.microservicio_5_asistencias_certificados.repositorios.asistencia;

import com.example.microservicio_5_asistencias_certificados.modelos.asistencia.Asistencia;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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

    List<Asistencia> findByIdCongreso(Long idCongreso);

    List<Asistencia> findByIdCongresoAndIdUsuario(Long idCongreso, Long idUsuario);

    boolean existsByIdCongresoAndIdUsuario(Long idCongreso, Long idUsuario);

    @Query("""
           SELECT COUNT(DISTINCT a.idActividad)
           FROM Asistencia a
           WHERE a.idCongreso = :idCongreso
           AND a.idUsuario = :idUsuario
           """)
    long countActividadesAsistidasPorCongresoYUsuario(
            @Param("idCongreso") Long idCongreso,
            @Param("idUsuario") Long idUsuario
    );
}
