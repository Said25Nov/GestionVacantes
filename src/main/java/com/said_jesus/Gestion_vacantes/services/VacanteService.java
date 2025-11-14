package com.said_jesus.Gestion_vacantes.services;

import com.said_jesus.Gestion_vacantes.models.*;
import com.said_jesus.Gestion_vacantes.repositories.VacanteRepository;
import com.said_jesus.Gestion_vacantes.repositories.SolicitudRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class VacanteService {

    private final VacanteRepository vacanteRepository;
    private final SolicitudRepository solicitudRepository;

    public VacanteService(VacanteRepository vacanteRepository, SolicitudRepository solicitudRepository) {
        this.vacanteRepository = vacanteRepository;
        this.solicitudRepository = solicitudRepository;
    }

    public List<Vacante> listarTodasVacantes() {
        return vacanteRepository.findAll();
    }

    public List<Vacante> listarVacantesPublicadas() {
        return vacanteRepository.findByEstado(EstadoVacante.PUBLICADA)
                .stream()
                .filter(Vacante::isActiva)
                .collect(Collectors.toList());
    }

    public List<Vacante> buscarPorUbicacion(String ubicacion) {
        return vacanteRepository.findByUbicacionContainingIgnoreCase(ubicacion)
                .stream()
                .filter(v -> v.getEstado() == EstadoVacante.PUBLICADA && v.isActiva())
                .collect(Collectors.toList());
    }

    public List<Vacante> buscarPorTipoTrabajo(TipoTrabajo tipoTrabajo) {
        return vacanteRepository.findByTipoTrabajoAndEstado(tipoTrabajo, EstadoVacante.PUBLICADA)
                .stream()
                .filter(Vacante::isActiva)
                .collect(Collectors.toList());
    }

    public List<Vacante> obtenerVacantesPorEmpleador(Long empleadorId) {
        return vacanteRepository.findByEmpleadorId(empleadorId);
    }

    public Vacante crearVacante(Vacante vacante) {
        return vacanteRepository.save(vacante);
    }

    public Optional<Vacante> obtenerPorId(Long id) {
        return vacanteRepository.findById(id);
    }

    public Vacante actualizarVacante(Vacante vacante) {
        return vacanteRepository.save(vacante);
    }

    @Transactional
    public void eliminarVacante(Long id) {
        Optional<Vacante> vacanteOpt = vacanteRepository.findById(id);
        if (vacanteOpt.isPresent()) {
            // Primero eliminar todas las solicitudes asociadas a esta vacante
            List<Solicitud> solicitudes = solicitudRepository.findByVacanteId(id);
            if (!solicitudes.isEmpty()) {
                solicitudRepository.deleteAll(solicitudes);
            }

            // Luego eliminar la vacante
            vacanteRepository.deleteById(id);
        }
    }

    public Optional<Vacante> obtenerVacantePorId(Long id) {
        return vacanteRepository.findById(id);
    }

    // Método para obtener vacantes vencidas
    public List<Vacante> obtenerVacantesVencidas() {
        return vacanteRepository.findAll()
                .stream()
                .filter(Vacante::isVencida)
                .collect(Collectors.toList());
    }
}