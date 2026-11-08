package com.mx.cbtis.gestor.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.mx.cbtis.gestor.modelo.Proyectos;
import com.mx.cbtis.gestor.repository.ProtectosRepository;

/** HTTP controller tests with an isolated repository; never writes to MariaDB. */
@WebMvcTest(ProyectosController.class)
class ProyectosControllerTests {
    @Autowired MockMvc mvc;
    @MockBean ProtectosRepository repository;

    private Proyectos proyecto() {
        return new Proyectos(9001, "AUT_PROYECTO", "Proyecto de prueba aislado",
            LocalDate.of(2026, 10, 16), LocalDate.of(2026, 10, 20),
            "En proceso", "/Images/animacion1.gif");
    }

    @Test @DisplayName("API-E01: consultar ID existente devuelve JSON y fechas correctos")
    void consultarProyectoExistente() throws Exception {
        when(repository.findById(9001)).thenReturn(Optional.of(proyecto()));
        mvc.perform(get("/buscarProyecto").param("id_proyecto", "9001"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id_proyecto").value(9001))
            .andExpect(jsonPath("$.nombre").value("AUT_PROYECTO"))
            .andExpect(jsonPath("$.fecha_inicio").value("2026-10-16"));
        verify(repository).findById(9001);
    }

    @Test @DisplayName("API-E02: alta valida entrega los datos al repositorio y responde 1")
    void crearProyectoValido() throws Exception {
        when(repository.save(any(Proyectos.class))).thenReturn(proyecto());
        mvc.perform(post("/addProyecto").contentType(MediaType.APPLICATION_JSON)
            .content("{\"nombre\":\"AUT_PROYECTO\",\"descripcion\":\"Proyecto de prueba aislado\","
                + "\"fecha_inicio\":\"2026-10-16\",\"fecha_finalizacion\":\"2026-10-20\","
                + "\"estado_proyecto\":\"En proceso\",\"fondo\":\"/Images/animacion1.gif\"}"))
            .andExpect(status().isOk()).andExpect(content().string("1"));
        verify(repository).save(argThat(p -> p.getId_proyecto() == null
            && "AUT_PROYECTO".equals(p.getNombre())
            && LocalDate.of(2026, 10, 16).equals(p.getFecha_inicio())));
    }

    @Test @DisplayName("API-E03: listado devuelve los proyectos como arreglo JSON")
    void listarProyectos() throws Exception {
        when(repository.findAll()).thenReturn(Collections.singletonList(proyecto()));
        mvc.perform(post("/buscarProyectos"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(1))
            .andExpect(jsonPath("$[0].id_proyecto").value(9001));
        verify(repository).findAll();
    }

    @Test @DisplayName("API-F01: ID no numerico devuelve 400 sin consultar el repositorio")
    void rechazarIdNoNumerico() throws Exception {
        mvc.perform(get("/buscarProyecto").param("id_proyecto", "abc"))
            .andExpect(status().isBadRequest());
        verifyNoInteractions(repository);
    }

    @Test @DisplayName("API-F02: JSON incompleto devuelve 400 sin guardar")
    void rechazarJsonMalformado() throws Exception {
        mvc.perform(post("/addProyecto").contentType(MediaType.APPLICATION_JSON)
            .content("{\"nombre\":"))
            .andExpect(status().isBadRequest());
        verifyNoInteractions(repository);
    }

    @Test @DisplayName("API-F03: fecha imposible devuelve 400 sin guardar")
    void rechazarFechaInvalida() throws Exception {
        mvc.perform(post("/addProyecto").contentType(MediaType.APPLICATION_JSON)
            .content("{\"nombre\":\"AUT_PROYECTO\",\"fecha_inicio\":\"2026-02-30\"}"))
            .andExpect(status().isBadRequest());
        verifyNoInteractions(repository);
    }
}
