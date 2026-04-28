/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.eiler.microservicio_4_convocatorias_ponencias.repositorios.estadoponencia;

/**
 *
 * @author eiler
 */

import com.eiler.microservicio_4_convocatorias_ponencias.modelos.estadoPonencia.EstadoPonencia;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EstadoPonenciaRepositorio extends JpaRepository<EstadoPonencia, Long> {
}