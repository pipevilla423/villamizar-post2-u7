
package com.universidad.tareas.controller;

import com.universidad.tareas.model.Prioridad;
import com.universidad.tareas.model.Tarea;
import com.universidad.tareas.service.TareaService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/tareas")
public class TareaApiController {

    private final TareaService servicio;

    // Inyección del mismo servicio de la Parte 1
    public TareaApiController(TareaService servicio) {
        this.servicio = servicio;
    }

    // GET: listar tareas con filtros opcionales
    @GetMapping
    public ResponseEntity<List<Tarea>> listar(
            @RequestParam(required = false) Prioridad prioridad,
            @RequestParam(required = false) Boolean completada) {

        return ResponseEntity.ok(servicio.filtrar(prioridad, completada));
    }

    // GET: buscar una tarea por ID
    @GetMapping("/{id}")
    public ResponseEntity<Tarea> buscar(@PathVariable Long id) {

        return servicio.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // POST: crear una tarea
    @PostMapping
    public ResponseEntity<Tarea> crear(@Valid @RequestBody Tarea tarea) {

        Tarea creada = servicio.guardar(tarea);

        URI ubicacion = URI.create("/api/tareas/" + creada.getId());

        return ResponseEntity.created(ubicacion).body(creada);
    }

    // PUT: reemplazar completamente una tarea
    @PutMapping("/{id}")
    public ResponseEntity<Tarea> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody Tarea tarea) {

        return servicio.buscarPorId(id)
                .map(existente -> {
                    tarea.setId(id);
                    return ResponseEntity.ok(servicio.guardar(tarea));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // PATCH: marcar una tarea como completada
    @PatchMapping("/{id}/completar")
    public ResponseEntity<Tarea> completar(@PathVariable Long id) {

        return servicio.marcarCompletada(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // DELETE: eliminar una tarea
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {

        if (!servicio.eliminar(id)) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.noContent().build();
    }
}

