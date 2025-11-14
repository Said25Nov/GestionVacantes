package com.said_jesus.Gestion_vacantes.repositories;

import com.said_jesus.Gestion_vacantes.models.Solicitud;
import com.said_jesus.Gestion_vacantes.models.Vacante;
import com.said_jesus.Gestion_vacantes.models.Aspirante;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface SolicitudRepository extends JpaRepository<Solicitud, Long> {
    List<Solicitud> findByVacanteId(Long vacanteId);
    List<Solicitud> findByAspiranteId(Long aspiranteId);
    Optional<Solicitud> findByAspiranteAndVacante(Aspirante aspirante, Vacante vacante);
    List<Solicitud> findByVacanteEmpleadorId(Long empleadorId);
    List<Solicitud> findTop5ByVacanteEmpleadorIdOrderByFechaSolicitudDesc(Long empleadorId);
}