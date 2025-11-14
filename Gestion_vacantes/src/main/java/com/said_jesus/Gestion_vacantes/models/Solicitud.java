package com.said_jesus.Gestion_vacantes.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "solicitudes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Solicitud {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "aspirante_id", nullable = false)
    private Aspirante aspirante;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "vacante_id", nullable = false)
    private Vacante vacante;

    @Column(name = "fecha_solicitud")
    private LocalDateTime fechaSolicitud;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoSolicitud estado = EstadoSolicitud.PENDIENTE;

    @Column(name = "nota_reclutador", length = 1000)
    private String notaReclutador;

    @Column(name = "cv_adjunto")
    private String cvAdjunto;

    @Column(nullable = false)
    private boolean activa = true;

    @PrePersist
    protected void onCreate() {
        fechaSolicitud = LocalDateTime.now();
    }
}