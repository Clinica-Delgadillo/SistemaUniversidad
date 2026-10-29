package com.universidad.sistema.service;

import com.universidad.sistema.dto.DivisionRequest;
import com.universidad.sistema.dto.DivisionResponse;
import com.universidad.sistema.entity.Division;
import com.universidad.sistema.repository.DivisionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Capa de servicio (lógica de negocio) para la administración de divisiones.
 * Separa las reglas de negocio del controlador y del repositorio.
 */
@Service
public class DivisionService {

    private final DivisionRepository repo;

    public DivisionService(DivisionRepository repo) {
        this.repo = repo;
    }

    /** Lista todas las divisiones registradas. */
    @Transactional(readOnly = true)
    public List<DivisionResponse> listarDivisiones() {
        return repo.findAll().stream().map(this::aResponse).toList();
    }

    /** Obtiene una división por su id. */
    @Transactional(readOnly = true)
    public Optional<DivisionResponse> obtenerDivision(Long id) {
        return repo.findById(id).map(this::aResponse);
    }

    /** Registra una nueva división. */
    @Transactional
    public DivisionResponse crearDivision(DivisionRequest datos) {
        Division division = new Division();
        division.setNombre(datos.getNombre());
        return aResponse(repo.save(division));
    }

    /** Actualiza el nombre de una división existente. */
    @Transactional
    public Optional<DivisionResponse> actualizarDivision(Long id, DivisionRequest datos) {
        return repo.findById(id).map(division -> {
            division.setNombre(datos.getNombre());
            return aResponse(repo.save(division));
        });
    }

    /**
     * Elimina una división por su id.
     * No se permite eliminar una división que tenga carreras registradas.
     * Devuelve true si se eliminó, false si no existía.
     */
    @Transactional
    public boolean eliminarDivision(Long id) {
        Division division = repo.findById(id).orElse(null);
        if (division == null) {
            return false;
        }
        if (!division.getCarreras().isEmpty()) {
            throw new DivisionConCarrerasException(
                    "No se puede eliminar la división con id " + id
                    + " porque tiene carreras registradas.");
        }
        repo.deleteById(id);
        return true;
    }

    private DivisionResponse aResponse(Division division) {
        DivisionResponse response = new DivisionResponse();
        response.setId(division.getId());
        response.setNombre(division.getNombre());
        return response;
    }

    /** Excepción de negocio: intento de eliminar una división con carreras. */
    public static class DivisionConCarrerasException extends RuntimeException {
        public DivisionConCarrerasException(String mensaje) {
            super(mensaje);
        }
    }
}
