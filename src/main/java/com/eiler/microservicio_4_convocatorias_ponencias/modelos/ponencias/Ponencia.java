/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.eiler.microservicio_4_convocatorias_ponencias.modelos.ponencias;

/**
 *
 * @author eiler
 */
import com.eiler.microservicio_4_convocatorias_ponencias.modelos.convocatoria.Convocatoria;
import com.eiler.microservicio_4_convocatorias_ponencias.modelos.estadoPonencia.EstadoPonencia;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "ponencia")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Ponencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_ponencia")
    private Long idPonencia;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_convocatoria", nullable = false)
    private Convocatoria convocatoria;

    @Column(name = "id_usuario", nullable = false)
    private Long idUsuario;                    // FK lógica a ms-auth

    @Column(name = "id_tipo_actividad", nullable = false)
    private Integer idTipoActividad;           // FK lógica a ms-actividades

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_estado")
    private EstadoPonencia estado;

    @Column(name = "titulo_ponencia", nullable = false, length = 255)
    private String tituloPonencia;

    @Column(name = "resumen", nullable = false, columnDefinition = "TEXT")
    private String resumen;

    @Column(name = "url_archivo", length = 500)
    private String urlArchivo;

    @Column(name = "enviado_en", updatable = false,
            insertable = false,
            columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime enviadoEn;

    @Column(name = "actualizado_en",
            insertable = false,
            columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    private LocalDateTime actualizadoEn;
}
