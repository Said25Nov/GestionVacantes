package com.said_jesus.Gestion_vacantes.services;

import com.said_jesus.Gestion_vacantes.models.*;
import com.said_jesus.Gestion_vacantes.repositories.AspiranteRepository;
import com.said_jesus.Gestion_vacantes.repositories.SolicitudRepository;
import com.said_jesus.Gestion_vacantes.repositories.VacanteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class SolicitudService {

    private final SolicitudRepository solicitudRepository;
    private final VacanteRepository vacanteRepository;
    private final AspiranteRepository aspiranteRepository;
    private final MailService mailService;

    public SolicitudService(SolicitudRepository solicitudRepository,
                            VacanteRepository vacanteRepository,
                            AspiranteRepository aspiranteRepository,
                            MailService mailService) {
        this.solicitudRepository = solicitudRepository;
        this.vacanteRepository = vacanteRepository;
        this.aspiranteRepository = aspiranteRepository;
        this.mailService = mailService;
    }

    public Optional<Solicitud> obtenerSolicitudPorId(Long id) {
        return solicitudRepository.findById(id);
    }

    public List<Solicitud> obtenerSolicitudesPorVacante(Long vacanteId) {
        return solicitudRepository.findByVacanteId(vacanteId);
    }

    public List<Solicitud> obtenerSolicitudesPorEmpleador(Long empleadorId) {
        return solicitudRepository.findByVacanteEmpleadorId(empleadorId);
    }

    public List<Solicitud> obtenerSolicitudesPorAspirante(Long aspiranteId) {
        return solicitudRepository.findByAspiranteId(aspiranteId);
    }

    @Transactional
    public Solicitud crearPostulacion(Long vacanteId, Long aspiranteId, String cvAdjunto) {
        Vacante vacante = vacanteRepository.findById(vacanteId)
                .orElseThrow(() -> new IllegalArgumentException("Vacante no encontrada"));
        Aspirante aspirante = aspiranteRepository.findById(aspiranteId)
                .orElseThrow(() -> new IllegalArgumentException("Aspirante no encontrado"));

        Optional<Solicitud> existente = solicitudRepository.findByAspiranteAndVacante(aspirante, vacante);
        if (existente.isPresent()) {
            throw new IllegalStateException("Ya existe una postulación para esta vacante.");
        }

        Solicitud s = new Solicitud();
        s.setVacante(vacante);
        s.setAspirante(aspirante);
        s.setEstado(EstadoSolicitud.PENDIENTE);
        s.setCvAdjunto(cvAdjunto);
        s = solicitudRepository.save(s);

        // Correo EMPLEADOR (nueva postulación)
        try {
            String correoEmpleador   = vacante.getEmpleador().getCorreo();
            String nombreEmpleador   = vacante.getEmpleador().getNombre();
            String nombreAspirante   = aspirante.getNombre();
            String tituloVacante     = vacante.getTitulo();
            String urlVerSolicitudes = "http://localhost:8080/empleador/solicitudes";
            mailService.notificarNuevaPostulacionAEmpleador(
                    correoEmpleador, nombreEmpleador, nombreAspirante, tituloVacante, urlVerSolicitudes
            );
        } catch (Exception ex) {
            System.err.println("WARN: fallo al enviar correo a empleador: " + ex.getMessage());
        }

        return s;
    }

    @Transactional
    public void actualizarEstadoSolicitud(Long solicitudId, EstadoSolicitud nuevoEstado, String nota) {
        Solicitud s = solicitudRepository.findById(solicitudId)
                .orElseThrow(() -> new IllegalArgumentException("Solicitud no encontrada"));

        s.setEstado(nuevoEstado);
        if (nota != null && !nota.isBlank()) {
            s.setNotaReclutador(nota);
        }
        solicitudRepository.save(s);

        // Correo ASPIRANTE (resultado)
        try {
            String correoAspirante = s.getAspirante().getCorreo();
            String nombreAspirante = s.getAspirante().getNombre();
            String tituloVacante   = s.getVacante().getTitulo();
            String empresa         = s.getVacante().getEmpleador().getEmpresa();
            boolean aceptada       = (nuevoEstado == EstadoSolicitud.ACEPTADA);
            String urlConfirmacion = aceptada ? ("http://localhost:8080/confirmar/" + s.getId()) : null;

            mailService.notificarResultadoAlAspirante(
                    correoAspirante, nombreAspirante, tituloVacante, empresa, aceptada, urlConfirmacion
            );
        } catch (Exception ex) {
            System.err.println("WARN: fallo al enviar correo a aspirante: " + ex.getMessage());
        }
    }

    @Transactional
    public void eliminarSolicitud(Long solicitudId) {
        solicitudRepository.deleteById(solicitudId);
    }

    @Transactional
    public void eliminarTodasSolicitudesPorVacante(Long vacanteId) {
        List<Solicitud> lista = solicitudRepository.findByVacanteId(vacanteId);
        if (!lista.isEmpty()) {
            solicitudRepository.deleteAll(lista);
        }
    }
}
