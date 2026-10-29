package com.universidad.sistema.controller;

import com.universidad.sistema.entity.Carrera;
import com.universidad.sistema.service.CarreraService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/carreras")
@CrossOrigin(origins = "http://localhost:5173")
public class CarreraController {

    private final CarreraService service;

    public CarreraController(CarreraService service) {
        this.service = service;
    }

    // Lista las carreras; si se envía "estado", filtra por ese estado
    @GetMapping
    public List<Carrera> listar(@RequestParam(required = false) String estado) {
        return service.listar(estado);
    }

    // Obtiene una carrera por id; responde 404 si no existe
    @GetMapping("/{id}")
    public ResponseEntity<Carrera> obtener(@PathVariable Long id) {
        return service.obtener(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());  // corregido: 404 cuando no existe 
    }

    // Crea una carrera nueva; responde 201 (CREATED) con la carrera creada
    @PostMapping
    public ResponseEntity<Carrera> crear(@RequestBody Carrera carrera) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(carrera)); // corregido: 201
    }

    @PutMapping("/{id}")
    public ResponseEntity<Carrera> actualizar(@PathVariable Long id, @RequestBody Carrera datos) {
        Carrera carrera = service.actualizar(id, datos);
        if (carrera == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(carrera);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        if (!service.eliminar(id)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/publicar")
    public ResponseEntity<Carrera> publicar(@PathVariable Long id) {
        Carrera carrera = service.publicar(id);
        if (carrera == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(carrera);
    }

    @ExceptionHandler(CarreraService.CarreraDivisionInexistenteException.class)
    public ResponseEntity<Map<String, String>> divisionInexistente(
            CarreraService.CarreraDivisionInexistenteException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of("error", ex.getMessage()));
    }
}
