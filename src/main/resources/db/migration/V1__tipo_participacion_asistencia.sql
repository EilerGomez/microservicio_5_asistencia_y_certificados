/* 
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Other/SQLTemplate.sql to edit this template
 */
/**
 * Author:  eiler
 * Created: 11 may 2026
 */

CREATE TABLE tipo_participacion (
    id_tipo_participacion INT AUTO_INCREMENT PRIMARY KEY,
    nombre_tipo           VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE asistencia (
    id_asistencia         BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_actividad          BIGINT NOT NULL,
    id_usuario            BIGINT NOT NULL,
    id_tipo_participacion INT    NOT NULL,
    registrado_por        BIGINT NOT NULL,
    registrado_en         TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_asist_tipo FOREIGN KEY (id_tipo_participacion)
        REFERENCES tipo_participacion(id_tipo_participacion)
);

INSERT INTO tipo_participacion (nombre_tipo) VALUES
('ASISTENTE'),
('PONENTE'),
('TALLERISTA'),
('PONENTE_INVITADO');
