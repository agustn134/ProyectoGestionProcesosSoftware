package com.mx.cbtis.gestor.dto;

import java.time.LocalDateTime;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import com.mx.cbtis.gestor.modelo.Comentarios;

/** Explicit writable fields; persistence entities are not HTTP input contracts. */
public record ComentarioRequest(Integer id_comentario, @NotBlank @Size(max=100) String id_usuario,
        @NotNull @Positive Integer id_tarea, @NotBlank @Size(max=250) String texto_comentario,
        @NotNull LocalDateTime fecha_hora) {
    public Comentarios toEntity() {
        return new Comentarios(id_comentario, id_usuario, id_tarea, texto_comentario, fecha_hora);
    }
}
