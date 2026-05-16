/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.eiler.microservicio_4_convocatorias_ponencias.repositorios.evaluacionPonencia;

/**
 *
 * @author eiler
 */


import com.eiler.microservicio_4_convocatorias_ponencias.modelos.evaluacionPonencia.EvaluacionPonencia;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface EvaluacionPonenciaRepositorio extends JpaRepository<EvaluacionPonencia, Long> {

    List<EvaluacionPonencia> findByPonencia_IdPonencia(Long idPonencia);

    List<EvaluacionPonencia> findByIdEvaluador(Long idEvaluador);

    Optional<EvaluacionPonencia> findFirstByPonencia_IdPonenciaOrderByEvaluadoEnDesc(
            Long idPonencia);

    boolean existsByPonencia_IdPonenciaAndIdEvaluador(Long idPonencia, Long idEvaluador);
}
