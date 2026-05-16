/* 
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Other/SQLTemplate.sql to edit this template
 */
/**
 * Author:  eiler
 * Created: 29 abr 2026
 */

-- V2__crear_tabla_ponencia.sql

CREATE TABLE ponencia (
    id_ponencia       BIGINT        AUTO_INCREMENT PRIMARY KEY,
    id_convocatoria   BIGINT        NOT NULL,
    id_usuario        BIGINT        NOT NULL,
    id_tipo_actividad INT           NOT NULL,
    id_estado         BIGINT        DEFAULT 1,
    titulo_ponencia   VARCHAR(255)  NOT NULL,
    resumen           TEXT          NOT NULL,
    url_archivo       VARCHAR(500),
    enviado_en        TIMESTAMP     DEFAULT CURRENT_TIMESTAMP,
    actualizado_en    TIMESTAMP     DEFAULT CURRENT_TIMESTAMP
                      ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_ponencia_conv FOREIGN KEY (id_convocatoria)
        REFERENCES convocatoria(id_convocatoria),
    CONSTRAINT fk_ponencia_estado FOREIGN KEY (id_estado)
        REFERENCES estado_ponencia(id_estado)
);