package com.mx.cbtis.gestor.dto;

import java.time.LocalDate;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import com.mx.cbtis.gestor.modelo.Tareas;

/** Explicit writable fields; persistence entities are not HTTP input contracts. */
public record TareaRequest(Integer id_tarea, @NotBlank @Size(max=100) String nombre,
        @NotBlank @Size(max=100) String descripcion, @NotNull LocalDate fecha_vencimiento,
        @NotBlank @Size(max=255) String lista, @NotBlank @Size(max=255) String estado,
        @NotNull @Positive Integer id_proyecto) {
    public Tareas toEntity() {
        return new Tareas(id_tarea, nombre, descripcion, fecha_vencimiento, lista, estado, id_proyecto);
    }
}
