package com.mx.cbtis.gestor.controller;

import java.util.ArrayList;
import java.util.List;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import com.mx.cbtis.gestor.dto.*;
import com.mx.cbtis.gestor.modelo.Comentarios;
import com.mx.cbtis.gestor.repository.ComentariosRepository;

@RestController
public class ComentariosController {
    private final ComentariosRepository comentariostable;

    public ComentariosController(ComentariosRepository repository) {
        this.comentariostable = repository;
    }

    @PostMapping("/getComentario")
    public List<Comentarios> getComentario(@Valid @RequestBody TareaFiltroRequest request) {
        return comentariostable.findByTarea(request.id_tarea());
    }

    @GetMapping("/buscarComentario")
    public Comentarios getComentario(@RequestParam int id) {
        requireId(id);
        return comentariostable.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Registro no encontrado."));
    }

    @PostMapping("/addComentario")
    public int addComentario(@Valid @RequestBody ComentarioRequest request) {
        if (request.id_comentario() != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El alta no acepta un ID existente.");
        }
        comentariostable.save(request.toEntity());
        return 1;
    }

    @PostMapping("/updateComentario")
    public int updateComentario(@Valid @RequestBody ComentarioRequest request) {
        requireExisting(request.id_comentario());
        comentariostable.save(request.toEntity());
        return 1;
    }

    @DeleteMapping("/deleteComentario")
    public int deleteComentario(@Valid @RequestBody ComentarioIdRequest request) {
        requireExisting(request.id_comentario());
        comentariostable.deleteById(request.id_comentario());
        return 1;
    }

    @PostMapping("/buscarComentarios")
    public List<Comentarios> getComentarios() {
        List<Comentarios> values = new ArrayList<>();
        comentariostable.findAll().forEach(values::add);
        return values;
    }

    private void requireExisting(Integer id) {
        requireId(id);
        if (!comentariostable.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Registro no encontrado.");
        }
    }

    private void requireId(Integer id) {
        if (id == null || id <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El ID debe ser positivo.");
        }
    }
}
