package com.mx.cbtis.gestor.dto;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
public record TareaFiltroRequest(@NotNull @Positive Integer id_tarea) {}
