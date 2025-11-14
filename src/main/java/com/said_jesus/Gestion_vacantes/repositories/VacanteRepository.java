package com.said_jesus.Gestion_vacantes.repositories;

import com.said_jesus.Gestion_vacantes.models.Vacante;
import com.said_jesus.Gestion_vacantes.models.EstadoVacante;
import com.said_jesus.Gestion_vacantes.models.TipoTrabajo;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface VacanteRepository extends JpaRepository<Vacante, Long> {
    List<Vacante> findByEstado(EstadoVacante estado);
    List<Vacante> findByUbicacionContainingIgnoreCase(String ubicacion);
    List<Vacante> findByTipoTrabajoAndEstado(TipoTrabajo tipoTrabajo, EstadoVacante estado);
    List<Vacante> findByEmpleadorId(Long empleadorId);
}