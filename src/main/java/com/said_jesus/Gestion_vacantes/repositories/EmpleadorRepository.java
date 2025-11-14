package com.said_jesus.Gestion_vacantes.repositories;

import com.said_jesus.Gestion_vacantes.models.Empleador;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface EmpleadorRepository extends JpaRepository<Empleador, Long> {
    Optional<Empleador> findByCorreo(String correo);
    boolean existsByCorreo(String correo);
}