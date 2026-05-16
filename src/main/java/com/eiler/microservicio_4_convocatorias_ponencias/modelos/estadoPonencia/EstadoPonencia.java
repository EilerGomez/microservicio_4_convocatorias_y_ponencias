/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.eiler.microservicio_4_convocatorias_ponencias.modelos.estadoPonencia;

/**
 *
 * @author eiler
 */

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "estado_ponencia")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EstadoPonencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_estado")
    private Long idEstado;

    @Column(name = "nombre_estado", nullable = false, unique = true, length = 50)
    private String nombreEstado;
}
