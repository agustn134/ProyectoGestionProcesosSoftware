package com.mx.cbtis.controller;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.*;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import com.mx.cbtis.modelo.Proyectos;

/** Tests the client MVC layer and rendered Thymeleaf, with HTTP backend simulated. */
@WebMvcTest(GestorController.class)
class ProyectosClienteTests {
    @Autowired MockMvc mvc;

    private Proyectos proyecto() {
        return new Proyectos(9001, "AUT_CLIENTE", "Proyecto de prueba",
            "2026-10-16", "2026-10-20", "En proceso", "/Images/animacion1.gif");
    }

    @Test @DisplayName("AUT-E01: consulta valida procesa los datos del backend")
    void consultaValida() throws Exception {
        try (MockedConstruction<RestTemplate> http = mockConstruction(RestTemplate.class,
            (mock, context) -> when(mock.postForObject(eq("http://localhost:8081/buscarProyectos"),
                any(), eq(Proyectos[].class))).thenReturn(new Proyectos[]{proyecto()}))) {
            mvc.perform(post("/consultarProyectos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id_proyecto").value(9001))
                .andExpect(jsonPath("$[0].nombre").value("AUT_CLIENTE"));
            verify(http.constructed().get(0)).postForObject(
                eq("http://localhost:8081/buscarProyectos"), any(), eq(Proyectos[].class));
        }
    }

    @Test @DisplayName("AUT-E02: formulario valido envia datos y confirma la operacion")
    void formularioValido() throws Exception {
        try (MockedConstruction<RestTemplate> http = mockConstruction(RestTemplate.class,
            (mock, context) -> when(mock.postForEntity(eq("http://localhost:8081/addProyecto"),
                any(Proyectos.class), eq(Integer.class))).thenReturn(ResponseEntity.ok(1)))) {
            mvc.perform(post("/saveProyecto").param("nombre", "AUT_CLIENTE")
                .param("descripcion", "Proyecto de prueba").param("fecha_inicio", "2026-10-16")
                .param("fecha_finalizacion", "2026-10-20").param("estado_proyecto", "En proceso")
                .param("fondo", "/Images/animacion1.gif"))
                .andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/inicio"));
            verify(http.constructed().get(0)).postForEntity(eq("http://localhost:8081/addProyecto"),
                argThat((Proyectos p) -> "AUT_CLIENTE".equals(p.getNombre())
                    && "2026-10-16".equals(p.getFecha_inicio())), eq(Integer.class));
        }
    }

    @Test @DisplayName("AUT-E03: vista muestra el proyecto recibido en el HTML")
    void vistaPresentaDatos() throws Exception {
        try (MockedConstruction<RestTemplate> http = mockConstruction(RestTemplate.class,
            (mock, context) -> when(mock.postForObject(eq("http://localhost:8081/buscarProyectos"),
                any(), eq(Proyectos[].class))).thenReturn(new Proyectos[]{proyecto()}))) {
            mvc.perform(get("/inicio"))
                .andExpect(status().isOk()).andExpect(view().name("inicio"))
                .andExpect(content().string(containsString("AUT_CLIENTE")))
                .andExpect(content().string(containsString("Proyecto de prueba")));
        }
    }

    @Test @DisplayName("AUT-F01: error del backend se controla y comunica")
    void errorBackendControlado() throws Exception {
        try (MockedConstruction<RestTemplate> http = mockConstruction(RestTemplate.class,
            (mock, context) -> when(mock.postForObject(eq("http://localhost:8081/buscarProyectos"),
                any(), eq(Proyectos[].class)))
                .thenThrow(new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR)))) {
            mvc.perform(post("/consultarProyectos"))
                .andExpect(status().isBadGateway())
                .andExpect(jsonPath("$.error").value("No se pudieron cargar los proyectos. Intenta nuevamente."));
            mvc.perform(get("/inicio"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("No se pudieron cargar los proyectos.")));
        }
    }

    @Test @DisplayName("AUT-F02: formulario incompleto se rechaza sin llamar al backend")
    void formularioInvalidoNoSeEnvia() throws Exception {
        try (MockedConstruction<RestTemplate> http = mockConstruction(RestTemplate.class)) {
            mvc.perform(post("/saveProyecto").param("nombre", " ")
                .param("descripcion", "Proyecto incompleto"))
                .andExpect(status().isBadRequest())
                .andExpect(view().name("inicio"))
                .andExpect(content().string(containsString("El nombre es obligatorio.")));
            assertTrue(http.constructed().isEmpty(), "Un formulario invalido no debe enviarse al backend");
        }
    }

    @Test @DisplayName("AUT-F03: proyecto inexistente devuelve 404 y mensaje controlado")
    void proyectoInexistente() throws Exception {
        try (MockedConstruction<RestTemplate> http = mockConstruction(RestTemplate.class,
            (mock, context) -> when(mock.getForObject(
                eq("http://localhost:8081/buscarProyecto?id_proyecto={id_proyecto}"),
                eq(Proyectos.class), anyMap())).thenReturn(new Proyectos()))) {
            mvc.perform(post("/actualizarProyecto").contentType(MediaType.APPLICATION_JSON)
                .content("{\"id_proyecto\":9001}"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("El proyecto solicitado no existe."));
        }
    }
}
