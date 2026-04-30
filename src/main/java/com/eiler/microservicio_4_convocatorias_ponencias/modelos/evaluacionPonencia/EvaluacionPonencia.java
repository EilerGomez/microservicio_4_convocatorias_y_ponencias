/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.eiler.microservicio_4_convocatorias_ponencias.modelos.evaluacionPonencia;

/**
 *
 * @author eiler
 */
import com.eiler.microservicio_4_convocatorias_ponencias.modelos.ponencias.Ponencia;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "evaluacion_ponencia")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EvaluacionPonencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_evaluacion")
    private Long idEvaluacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_ponencia", nullable = false)
    private Ponencia ponencia;

    @Column(name = "id_evaluador", nullable = false)
    private Long idEvaluador;          // FK lógica a ms-auth

    @Column(name = "comentarios", columnDefinition = "TEXT")
    private String comentarios;

    @Column(name = "esta_aprobado", nullable = false)
    private Boolean estaAprobado;

    @Column(name = "evaluado_en", updatable = false,
            insertable = false,
            columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime evaluadoEn;
}
