/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.eiler.microservicio_4_convocatorias_ponencias.repositorios.ponencias;

/**
 *
 * @author eiler
 */

import com.eiler.microservicio_4_convocatorias_ponencias.modelos.ponencias.Ponencia;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PonenciaRepositorio extends JpaRepository<Ponencia, Long> {

    List<Ponencia> findByConvocatoriaIIdConvocatoria(Long idConvocatoria);

    List<Ponencia> findByIdUsuario(Long idUsuario);

    List<Ponencia> findByConvocatoriaIdConvocatoriaAndIdUsuario(
            Long idConvocatoria, Long idUsuario);
}
