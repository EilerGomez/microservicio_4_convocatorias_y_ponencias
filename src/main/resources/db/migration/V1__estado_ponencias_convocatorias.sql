/* 
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Other/SQLTemplate.sql to edit this template
 */
/**
 * Author:  eiler
 * Created: 28 abr 2026
 */

-- V1__crear_tablas_iniciales.sql

-- ============================================
-- Catálogo de estados de ponencia
-- ============================================
CREATE TABLE estado_ponencia (
    id_estado     BIGINT  AUTO_INCREMENT PRIMARY KEY,
    nombre_estado VARCHAR(50)  NOT NULL UNIQUE
);

-- Datos iniciales del catálogo
INSERT INTO estado_ponencia (nombre_estado) VALUES
    ('PENDIENTE'),
    ('APROBADO'),
    ('RECHAZADO');

-- ============================================
-- Convocatorias
-- FK lógica id_congreso → ms-congresos (sin FOREIGN KEY real)
-- ============================================
CREATE TABLE convocatoria (
    id_convocatoria     BIGINT       AUTO_INCREMENT PRIMARY KEY,
    id_congreso         BIGINT       NOT NULL,
    nombre_convocatoria VARCHAR(255) NOT NULL,
    descripcion         TEXT,
    fecha_apertura      DATETIME     NOT NULL,
    fecha_cierre        DATETIME     NOT NULL,
    esta_abierta        BOOLEAN      DEFAULT TRUE,
    creado_en           TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    actualizado_en      TIMESTAMP    DEFAULT CURRENT_TIMESTAMP
                        ON UPDATE CURRENT_TIMESTAMP
);