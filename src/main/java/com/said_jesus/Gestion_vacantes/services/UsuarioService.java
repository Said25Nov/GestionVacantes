package com.said_jesus.Gestion_vacantes.services;

import com.said_jesus.Gestion_vacantes.models.Aspirante;
import com.said_jesus.Gestion_vacantes.models.Empleador;
import com.said_jesus.Gestion_vacantes.repositories.AspiranteRepository;
import com.said_jesus.Gestion_vacantes.repositories.EmpleadorRepository;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import java.util.Optional;

@Service
public class UsuarioService {

    private final AspiranteRepository aspiranteRepository;
    private final EmpleadorRepository empleadorRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public UsuarioService(AspiranteRepository aspiranteRepository,
                          EmpleadorRepository empleadorRepository) {
        this.aspiranteRepository = aspiranteRepository;
        this.empleadorRepository = empleadorRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    public Object registrarUsuario(String nombre, String correo, String contrasena,
                                   String tipo, String empresa, String habilidades) {
        // Verificar si el correo ya existe en aspirantes o empleadores
        if (aspiranteRepository.existsByCorreo(correo) || empleadorRepository.existsByCorreo(correo)) {
            throw new RuntimeException("El correo ya está registrado");
        }

        // Validar que si es EMPLEADOR, tenga empresa
        if ("EMPLEADOR".equals(tipo) && (empresa == null || empresa.trim().isEmpty())) {
            throw new RuntimeException("El nombre de la empresa es obligatorio para empleadores");
        }

        String contrasenaHash = passwordEncoder.encode(contrasena);

        if ("ASPIRANTE".equals(tipo)) {
            Aspirante aspirante = new Aspirante();
            aspirante.setNombre(nombre);
            aspirante.setCorreo(correo);
            aspirante.setContrasenaHash(contrasenaHash);
            aspirante.setHabilidades(habilidades != null ? habilidades : "");
            return aspiranteRepository.save(aspirante);
        } else if ("EMPLEADOR".equals(tipo)) {
            Empleador empleador = new Empleador();
            empleador.setNombre(nombre);
            empleador.setCorreo(correo);
            empleador.setContrasenaHash(contrasenaHash);
            empleador.setEmpresa(empresa);
            return empleadorRepository.save(empleador);
        }

        throw new RuntimeException("Tipo de usuario no válido");
    }

    public Object login(String correo, String contrasena) {
        // Buscar primero en aspirantes
        Optional<Aspirante> aspiranteOpt = aspiranteRepository.findByCorreo(correo);
        if (aspiranteOpt.isPresent() && passwordEncoder.matches(contrasena, aspiranteOpt.get().getContrasenaHash())) {
            return aspiranteOpt.get();
        }

        // Buscar en empleadores
        Optional<Empleador> empleadorOpt = empleadorRepository.findByCorreo(correo);
        if (empleadorOpt.isPresent() && passwordEncoder.matches(contrasena, empleadorOpt.get().getContrasenaHash())) {
            return empleadorOpt.get();
        }

        return null;
    }

    public Aspirante actualizarAspirante(Long id, Aspirante aspiranteActualizado) {
        Optional<Aspirante> aspiranteOpt = aspiranteRepository.findById(id);
        if (aspiranteOpt.isPresent()) {
            Aspirante aspirante = aspiranteOpt.get();
            aspirante.setNombre(aspiranteActualizado.getNombre());
            aspirante.setCorreo(aspiranteActualizado.getCorreo());
            aspirante.setHabilidades(aspiranteActualizado.getHabilidades());

            // Si se proporciona una nueva contraseña, actualizarla
            if (aspiranteActualizado.getContrasenaHash() != null &&
                    !aspiranteActualizado.getContrasenaHash().isEmpty()) {
                aspirante.setContrasenaHash(passwordEncoder.encode(aspiranteActualizado.getContrasenaHash()));
            }

            return aspiranteRepository.save(aspirante);
        }
        throw new RuntimeException("Aspirante no encontrado");
    }

    public Empleador actualizarEmpleador(Long id, Empleador empleadorActualizado) {
        Optional<Empleador> empleadorOpt = empleadorRepository.findById(id);
        if (empleadorOpt.isPresent()) {
            Empleador empleador = empleadorOpt.get();
            empleador.setNombre(empleadorActualizado.getNombre());
            empleador.setCorreo(empleadorActualizado.getCorreo());
            empleador.setEmpresa(empleadorActualizado.getEmpresa());

            // Si se proporciona una nueva contraseña, actualizarla
            if (empleadorActualizado.getContrasenaHash() != null &&
                    !empleadorActualizado.getContrasenaHash().isEmpty()) {
                empleador.setContrasenaHash(passwordEncoder.encode(empleadorActualizado.getContrasenaHash()));
            }

            return empleadorRepository.save(empleador);
        }
        throw new RuntimeException("Empleador no encontrado");
    }
}