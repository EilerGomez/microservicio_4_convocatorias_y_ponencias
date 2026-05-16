/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.eiler.microservicio_4_convocatorias_ponencias.repositorios.convocatoria;

/**
 *
 * @author eiler
 */

import com.eiler.microservicio_4_convocatorias_ponencias.modelos.convocatoria.Convocatoria;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ConvocatoriaRepositorio extends JpaRepository<Convocatoria, Long> {

    List<Convocatoria> findByIdCongreso(Long idCongreso);

    List<Convocatoria> findByIdCongresoAndEstaAbierta(Long idCongreso, Boolean estaAbierta);
}
