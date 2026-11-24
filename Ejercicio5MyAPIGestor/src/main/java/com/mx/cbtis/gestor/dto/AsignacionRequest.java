package com.mx.cbtis.gestor.dto;

import java.time.LocalDate;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import com.mx.cbtis.gestor.modelo.Asignaciones;

/** Explicit writable fields; persistence entities are not HTTP input contracts. */
public record AsignacionRequest(Integer id_asignacion, @NotNull @Positive Integer id_usuario,
        @NotNull @Positive Integer id_tarea, @NotNull LocalDate fecha_asignacion) {
    public Asignaciones toEntity() {
        return new Asignaciones(id_asignacion, id_usuario, id_tarea, fecha_asignacion);
    }
}
