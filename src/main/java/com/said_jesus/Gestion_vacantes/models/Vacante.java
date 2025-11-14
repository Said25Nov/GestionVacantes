package com.said_jesus.Gestion_vacantes.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "vacantes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Vacante {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "empleador_id", nullable = false)
    private Empleador empleador;

    @NotBlank(message = "El título es obligatorio")
    @Column(nullable = false)
    private String titulo;

    @NotBlank(message = "La descripción es obligatoria")
    @Column(length = 2000, nullable = false)
    private String descripcion;

    @NotBlank(message = "Los requisitos son obligatorios")
    @Column(length = 2000, nullable = false)
    private String requisitos;

    @NotBlank(message = "La ubicación es obligatoria")
    @Column(nullable = false)
    private String ubicacion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoTrabajo tipoTrabajo;

    @NotNull(message = "El salario es obligatorio")
    @Column(nullable = false)
    private BigDecimal salario;

    @Column(name = "fecha_publicacion")
    private LocalDateTime fechaPublicacion;

    @Column(name = "fecha_cierre")
    private LocalDateTime fechaCierre;

    @Column(name = "fecha_vencimiento")
    private LocalDateTime fechaVencimiento;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoVacante estado = EstadoVacante.BORRADOR;

    @PrePersist
    protected void onCreate() {
        fechaPublicacion = LocalDateTime.now();
    }

    public boolean isVencida() {
        return fechaVencimiento != null && LocalDateTime.now().isAfter(fechaVencimiento);
    }

    public boolean isActiva() {
        return estado == EstadoVacante.PUBLICADA && !isVencida();
    }
}