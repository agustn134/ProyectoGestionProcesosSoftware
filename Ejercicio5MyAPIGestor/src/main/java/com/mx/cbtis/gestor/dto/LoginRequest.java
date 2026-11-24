package com.mx.cbtis.gestor.dto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Email;
public record LoginRequest(@NotBlank @Email String correo_electronico, @NotBlank String contrasena) {}
