package com.universidad.sistema.service;

import com.universidad.sistema.entity.Carrera;
import com.universidad.sistema.entity.Division;
import com.universidad.sistema.repository.CarreraRepository;
import com.universidad.sistema.repository.DivisionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Capa de servicio (lógica de negocio) para la administración de carreras.
 * Separa las reglas de negocio del controlador y del repositorio.
 */
@Service
public class CarreraService {

    private final CarreraRepository repo;
    private final DivisionRepository divisionRepo;

    public CarreraService(CarreraRepository repo, DivisionRepository divisionRepo) {
        this.repo = repo;
        this.divisionRepo = divisionRepo;
    }

    /** Lista todas las carreras, o solo las que cumplan el estado indicado. */
    @Transactional(readOnly = true)
    public List<Carrera> listar(String estado) {
        if (estado != null && !estado.isBlank()) {
            return repo.findByEstado(estado);
        }
        return repo.findAll();
    }

    /** Obtiene una carrera por su id. */
    @Transactional(readOnly = true)
    public Optional<Carrera> obtener(Long id) {
        return repo.findById(id);
    }

    /** Crea una carrera; si no llega estado, se guarda como BORRADOR. */
    @Transactional
    public Carrera crear(Carrera carrera) {
        if (carrera.getEstado() == null || carrera.getEstado().isBlank()) {
            carrera.setEstado("BORRADOR");
        }
        // Regla de la capa service: verificar que la división exista y
        // asociar la entidad completa (con nombre) a la carrera.
        Division division = carrera.getDivision();
        if (division != null && division.getId() != null) {
            Division existente = divisionRepo.findById(division.getId())
                    .orElseThrow(() -> new CarreraDivisionInexistenteException(
                            "No se puede crear la carrera porque la división con id "
                            + division.getId() + " no existe."));
            carrera.setDivision(existente);
        }
        return repo.save(carrera);
    }

    /** Actualiza los campos proporcionados de una carrera existente. */
    @Transactional
    public Carrera actualizar(Long id, Carrera datos) {
        Carrera carrera = repo.findById(id).orElse(null);
        if (carrera == null) {
            return null;
        }
        if (datos.getNombre() != null) carrera.setNombre(datos.getNombre());
        if (datos.getFacultad() != null) carrera.setFacultad(datos.getFacultad());
        if (datos.getDescripcion() != null) carrera.setDescripcion(datos.getDescripcion());
        if (datos.getEstado() != null) carrera.setEstado(datos.getEstado());
        return repo.save(carrera);
    }

    /** Elimina una carrera por su id. Devuelve true si existía. */
    @Transactional
    public boolean eliminar(Long id) {
        if (!repo.existsById(id)) {
            return false;
        }
        repo.deleteById(id);
        return true;
    }

    /** Cambia el estado de una carrera a PUBLICADA. */
    @Transactional
    public Carrera publicar(Long id) {
        Carrera carrera = repo.findById(id).orElse(null);
        if (carrera == null) {
            return null;
        }
        carrera.setEstado("PUBLICADA");
        return repo.save(carrera);
    }

    /** Excepción de negocio: intento de crear una carrera con una división inexistente. */
    public static class CarreraDivisionInexistenteException extends RuntimeException {
        public CarreraDivisionInexistenteException(String mensaje) {
            super(mensaje);
        }
    }
}
