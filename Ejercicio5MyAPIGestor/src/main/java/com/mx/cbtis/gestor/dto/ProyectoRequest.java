package com.mx.cbtis.gestor.dto;

import java.time.LocalDate;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;
import com.mx.cbtis.gestor.modelo.Proyectos;

/** Explicit writable fields; persistence entities are not HTTP input contracts. */
public record ProyectoRequest(Integer id_proyecto, @NotBlank @Size(max=100) String nombre,
        @NotBlank @Size(max=100) String descripcion, @NotNull LocalDate fecha_inicio,
        @NotNull LocalDate fecha_finalizacion, @NotBlank @Pattern(regexp="En proceso|Finalizado") String estado_proyecto,
        @Size(max=100) String fondo) {
    public Proyectos toEntity() {
        return new Proyectos(id_proyecto, nombre, descripcion, fecha_inicio, fecha_finalizacion, estado_proyecto, fondo);
    }
}
