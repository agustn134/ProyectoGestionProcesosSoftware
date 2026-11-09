package com.mx.cbtis.gestor.controller;

import java.util.ArrayList;
import java.util.List;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import com.mx.cbtis.gestor.dto.*;
import com.mx.cbtis.gestor.modelo.Proyectos;
import com.mx.cbtis.gestor.repository.ProtectosRepository;

@RestController
public class ProyectosController {
    private final ProtectosRepository proyectostable;

    public ProyectosController(ProtectosRepository repository) {
        this.proyectostable = repository;
    }

    @GetMapping("/buscarProyecto")
    public Proyectos getProyecto(@RequestParam int id_proyecto) {
        requireId(id_proyecto);
        return proyectostable.findById(id_proyecto)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Registro no encontrado."));
    }

    @PostMapping("/addProyecto")
    public int addProyecto(@Valid @RequestBody ProyectoRequest request) {
        if (request.id_proyecto() != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El alta no acepta un ID existente.");
        }
        validateDates(request);
        proyectostable.save(request.toEntity());
        return 1;
    }

    @PostMapping("/updateProyecto")
    public int updateProyecto(@Valid @RequestBody ProyectoRequest request) {
        requireExisting(request.id_proyecto());
        validateDates(request);
        proyectostable.save(request.toEntity());
        return 1;
    }

    @DeleteMapping("/deleteProyecto")
    public int deleteProyecto(@Valid @RequestBody ProyectoIdRequest request) {
        requireExisting(request.id_proyecto());
        proyectostable.deleteById(request.id_proyecto());
        return 1;
    }

    @PostMapping("/buscarProyectos")
    public List<Proyectos> getProyectos() {
        List<Proyectos> values = new ArrayList<>();
        proyectostable.findAll().forEach(values::add);
        return values;
    }

    private void validateDates(ProyectoRequest request) {
        if (request.fecha_finalizacion().isBefore(request.fecha_inicio())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La fecha final debe ser posterior o igual a la inicial.");
        }
    }

    private void requireExisting(Integer id) {
        requireId(id);
        if (!proyectostable.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Registro no encontrado.");
        }
    }

    private void requireId(Integer id) {
        if (id == null || id <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El ID debe ser positivo.");
        }
    }
}
