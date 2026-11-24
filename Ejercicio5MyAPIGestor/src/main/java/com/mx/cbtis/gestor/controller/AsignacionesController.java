package com.mx.cbtis.gestor.controller;

import java.util.ArrayList;
import java.util.List;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import com.mx.cbtis.gestor.dto.*;
import com.mx.cbtis.gestor.modelo.Asignaciones;
import com.mx.cbtis.gestor.repository.AsignacionesRepository;

@RestController
public class AsignacionesController {
    private final AsignacionesRepository asignacionestable;

    public AsignacionesController(AsignacionesRepository repository) {
        this.asignacionestable = repository;
    }

    @GetMapping("/buscarAsignacion")
    public Asignaciones getAsignacion(@RequestParam int id) {
        requireId(id);
        return asignacionestable.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Registro no encontrado."));
    }

    @PostMapping("/addAsignacion")
    public int addAsignacion(@Valid @RequestBody AsignacionRequest request) {
        if (request.id_asignacion() != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El alta no acepta un ID existente.");
        }
        asignacionestable.save(request.toEntity());
        return 1;
    }

    @PostMapping("/updateAsignacion")
    public int updateAsignacion(@Valid @RequestBody AsignacionRequest request) {
        requireExisting(request.id_asignacion());
        asignacionestable.save(request.toEntity());
        return 1;
    }

    @DeleteMapping("/deleteAsignacion")
    public int deleteAsignacion(@Valid @RequestBody AsignacionIdRequest request) {
        requireExisting(request.id_asignacion());
        asignacionestable.deleteById(request.id_asignacion());
        return 1;
    }

    @PostMapping("/buscarAsignaciones")
    public List<Asignaciones> getAsignaciones() {
        List<Asignaciones> values = new ArrayList<>();
        asignacionestable.findAll().forEach(values::add);
        return values;
    }

    private void requireExisting(Integer id) {
        requireId(id);
        if (!asignacionestable.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Registro no encontrado.");
        }
    }

    private void requireId(Integer id) {
        if (id == null || id <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El ID debe ser positivo.");
        }
    }
}
