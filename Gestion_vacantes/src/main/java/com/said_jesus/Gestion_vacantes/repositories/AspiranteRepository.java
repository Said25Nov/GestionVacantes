package com.said_jesus.Gestion_vacantes.repositories;

import com.said_jesus.Gestion_vacantes.models.Aspirante;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface AspiranteRepository extends JpaRepository<Aspirante, Long> {
    Optional<Aspirante> findByCorreo(String correo);
    boolean existsByCorreo(String correo);
}
