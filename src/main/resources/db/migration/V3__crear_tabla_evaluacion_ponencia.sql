/* 
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Other/SQLTemplate.sql to edit this template
 */
/**
 * Author:  eiler
 * Created: 29 abr 2026
 */

-- V3__crear_tabla_evaluacion_ponencia.sql

CREATE TABLE evaluacion_ponencia (
    id_evaluacion  BIGINT    AUTO_INCREMENT PRIMARY KEY,
    id_ponencia    BIGINT    NOT NULL,
    id_evaluador   BIGINT    NOT NULL,
    comentarios    TEXT,
    esta_aprobado  BOOLEAN   NOT NULL,
    evaluado_en    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_eval_ponencia FOREIGN KEY (id_ponencia)
        REFERENCES ponencia(id_ponencia)
);