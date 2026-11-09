package com.mx.cbtis.gestor.controller;

import java.util.ArrayList;
import java.util.List;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import com.mx.cbtis.gestor.dto.*;
import com.mx.cbtis.gestor.modelo.Tareas;
import com.mx.cbtis.gestor.repository.TareasRepository;

@RestController
public class TareasController {
    private final TareasRepository tareastable;

    public TareasController(TareasRepository repository) {
        this.tareastable = repository;
    }

    @PostMapping("/getTarea")
    public List<Tareas> getTarea(@Valid @RequestBody ProyectoFiltroRequest request) {
        return tareastable.findByIdProyecto(request.id_proyecto());
    }

    @GetMapping("/buscarTarea")
    public Tareas getTarea(@RequestParam int id_tarea) {
        requireId(id_tarea);
        return tareastable.findById(id_tarea)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Registro no encontrado."));
    }

    @PostMapping("/addTareas")
    public int addTareas(@Valid @RequestBody TareaRequest request) {
        if (request.id_tarea() != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El alta no acepta un ID existente.");
        }
        tareastable.save(request.toEntity());
        return 1;
    }

    @PostMapping("/updateTareas")
    public int updateTareas(@Valid @RequestBody TareaRequest request) {
        requireExisting(request.id_tarea());
        tareastable.save(request.toEntity());
        return 1;
    }

    @DeleteMapping("/deleteTareas")
    public int deleteTareas(@Valid @RequestBody TareaIdRequest request) {
        requireExisting(request.id_tarea());
        tareastable.deleteById(request.id_tarea());
        return 1;
    }

    @PostMapping("/buscarTareas")
    public List<Tareas> getTareas() {
        List<Tareas> values = new ArrayList<>();
        tareastable.findAll().forEach(values::add);
        return values;
    }

    private void requireExisting(Integer id) {
        requireId(id);
        if (!tareastable.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Registro no encontrado.");
        }
    }

    private void requireId(Integer id) {
        if (id == null || id <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El ID debe ser positivo.");
        }
    }
}
