package com.universidad.sistema.controller;

import com.universidad.sistema.dto.DivisionRequest;
import com.universidad.sistema.dto.DivisionResponse;
import com.universidad.sistema.service.DivisionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Rutas de la API para la administración de divisiones.
 * Solo delega la lógica al servicio; no consulta la base de datos.
 */
@RestController
@RequestMapping("/api/divisiones")
@CrossOrigin(origins = "http://localhost:5173")
public class DivisionController {

    private final DivisionService service;

    public DivisionController(DivisionService service) {
        this.service = service;
    }

    @GetMapping
    public List<DivisionResponse> listar() {
        return service.listarDivisiones();
    }

    @GetMapping("/{id}")
    public ResponseEntity<DivisionResponse> obtener(@PathVariable Long id) {
        return service.obtenerDivision(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<DivisionResponse> crear(@RequestBody DivisionRequest datos) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crearDivision(datos));
    }

    // Atiende solicitudes HTTP PUT en la ruta /divisiones/{id}.
    @PutMapping("/{id}")
    public ResponseEntity<DivisionResponse> actualizar(
            @PathVariable Long id,
            @RequestBody DivisionRequest datos) {

        // Busca la división por su ID y actualiza sus datos.
        return service.actualizarDivision(id, datos)

                // Si la división existe y se actualizó correctamente,
                // devuelve HTTP 200 OK junto con la división actualizada.
                .map(ResponseEntity::ok)

                // Si no existe una división con ese ID,
                // devuelve HTTP 404 Not Found.
                .orElse(ResponseEntity.notFound().build());
    }

    // Atiende solicitudes HTTP DELETE en la ruta /divisiones/{id}.
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {

        // Intenta eliminar la división mediante el servicio.
        if (!service.eliminarDivision(id)) {

            // Si la división no existe, devuelve HTTP 404 Not Found.
            return ResponseEntity.notFound().build();
        }

        // Si la eliminación fue correcta, devuelve HTTP 204 No Content,
        // indicando que la operación terminó correctamente y no hay contenido que
        // devolver.
        return ResponseEntity.noContent().build();
    }

    
    @ExceptionHandler(DivisionService.DivisionConCarrerasException.class)
    public ResponseEntity<Map<String, String>> divisionConCarreras(
            DivisionService.DivisionConCarrerasException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of("error", ex.getMessage()));
    }
}
