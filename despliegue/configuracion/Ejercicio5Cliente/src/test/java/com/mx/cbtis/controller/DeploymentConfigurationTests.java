package com.mx.cbtis.controller;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.any;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;
import com.mx.cbtis.modelo.Proyectos;
import com.mx.cbtis.modelo.Usuario;

@WebMvcTest(value = GestorController.class,
    properties = "api.base-url=https://api.example.invalid")
class DeploymentConfigurationTests {
    @Autowired MockMvc mvc;

    @Test
    void enviaLaConsultaAlServidorConfigurado() throws Exception {
        try (var http = mockConstruction(RestTemplate.class, (mock, context) ->
            when(mock.postForObject(eq("https://api.example.invalid/buscarProyectos"),
                any(HttpHeaders.class), eq(Proyectos[].class))).thenReturn(new Proyectos[0]))) {
            mvc.perform(post("/consultarProyectos")).andExpect(status().isOk());
            var headers = ArgumentCaptor.forClass(HttpHeaders.class);
            verify(http.constructed().get(0)).postForObject(
                eq("https://api.example.invalid/buscarProyectos"), headers.capture(), eq(Proyectos[].class));
            assertEquals(MediaType.APPLICATION_JSON, headers.getValue().getContentType());
        }
    }

    @Test
    void loginNoReutilizaLaLongitudDelCuerpoDeLaApi() throws Exception {
        var upstream = new HttpHeaders();
        upstream.setContentLength(1);
        var user = new Usuario(1, "Prueba", "qa@example.invalid", "no-debe-salir", "Usuario", null);
        try (var http = mockConstruction(RestTemplate.class, (mock, context) ->
            when(mock.postForEntity(eq("https://api.example.invalid/getUsuarioLogin"),
                any(Usuario.class), eq(Usuario.class)))
                .thenReturn(new ResponseEntity<>(user, upstream, HttpStatus.OK)))) {
            mvc.perform(post("/verificarLogin").contentType(MediaType.APPLICATION_JSON)
                .content("{\"correo_electronico\":\"qa@example.invalid\",\"contrasena\":\"clave-de-prueba\"}"))
                .andExpect(status().isOk())
                .andExpect(header().doesNotExist(HttpHeaders.CONTENT_LENGTH))
                .andExpect(jsonPath("$.id_usuario").value(1))
                .andExpect(jsonPath("$.contrasena").isEmpty());
        }
    }
}
