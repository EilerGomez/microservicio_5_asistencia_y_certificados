/* 
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Other/SQLTemplate.sql to edit this template
 */
/**
 * Author:  eiler
 * Created: 12 may 2026
 */


CREATE TABLE certificado (
    id_certificado  BIGINT        AUTO_INCREMENT PRIMARY KEY,
    id_congreso     BIGINT        NOT NULL,
    id_usuario      BIGINT        NOT NULL,
    tipo_certificado VARCHAR(50)  NOT NULL,
    id_actividad    BIGINT,
    url_certificado VARCHAR(500),
    generado_en     TIMESTAMP     DEFAULT CURRENT_TIMESTAMP
);