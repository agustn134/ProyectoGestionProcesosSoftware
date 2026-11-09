package com.mx.cbtis.gestor.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Email;
import com.mx.cbtis.gestor.modelo.Usuario;

/** Explicit writable fields; persistence entities are not HTTP input contracts. */
public record UsuarioRequest(Integer id_usuario, @NotBlank @Size(max=100) String nombre,
        @NotBlank @Email @Size(max=100) String correo_electronico,
        @NotBlank String contrasena, @Size(max=255) String imagen_usuario) {
    public Usuario toEntity() {
        return new Usuario(id_usuario, nombre, correo_electronico, contrasena, "Usuario", imagen_usuario);
    }
}
