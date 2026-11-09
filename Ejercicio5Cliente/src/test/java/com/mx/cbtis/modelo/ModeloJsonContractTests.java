package com.mx.cbtis.modelo;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

/** Verifies the DTO contract exchanged with the API, including every writable JSON field. */
class ModeloJsonContractTests {
    private final ObjectMapper mapper = new ObjectMapper();

    private <T> void assertRoundTrip(T value, Class<T> type, String expectedJson) throws Exception {
        JsonNode expected = mapper.readTree(expectedJson);
        assertEquals(expected, mapper.valueToTree(value), "Constructor values must reach the API JSON");
        T decoded = mapper.readValue(expectedJson, type);
        assertEquals(expected, mapper.valueToTree(decoded), "JSON setters/getters must preserve the complete response");
    }

    @Test void proyectoPreservaIdentificadorFechasYPresentacion() throws Exception {
        assertRoundTrip(new Proyectos(7, "Proyecto", "Descripción", "2026-11-01", "2026-11-30", "En proceso", "/Images/blanco.png"),
            Proyectos.class, """
            {"id_proyecto":7,"nombre":"Proyecto","descripcion":"Descripción","fecha_inicio":"2026-11-01",
             "fecha_finalizacion":"2026-11-30","estado_proyecto":"En proceso","fondo":"/Images/blanco.png"}
            """);
    }

    @Test void tareaPreservaRelacionConProyectoYEstado() throws Exception {
        assertRoundTrip(new Tareas(11, "Tarea", "Entregar", "2026-11-10", "Pendiente", "Abierta", 7), Tareas.class, """
            {"id_tarea":11,"nombre":"Tarea","descripcion":"Entregar","fecha_vencimiento":"2026-11-10",
             "lista":"Pendiente","estado":"Abierta","id_proyecto":7}
            """);
    }

    @Test void comentarioPreservaAutorTextoYFecha() throws Exception {
        assertRoundTrip(new Comentarios(5, "usuario-9", 11, "Revisar cambios", "2026-11-08T10:30:00"), Comentarios.class, """
            {"id_comentario":5,"id_usuario":"usuario-9","id_tarea":11,"texto_comentario":"Revisar cambios","fecha_hora":"2026-11-08T10:30:00"}
            """);
    }

    @Test void asignacionPreservaAmbasReferencias() throws Exception {
        assertRoundTrip(new Asignaciones(4, 9, 11, "2026-11-08"), Asignaciones.class, """
            {"id_asignacion":4,"id_usuario":9,"id_tarea":11,"fecha_asignacion":"2026-11-08"}
            """);
    }

    @Test void usuarioPreservaContratoDeRegistro() throws Exception {
        assertRoundTrip(new Usuario(9, "Persona prueba", "prueba@example.invalid", "solo-prueba", "usuario", "/Images/avatar.png"), Usuario.class, """
            {"id_usuario":9,"nombre":"Persona prueba","correo_electronico":"prueba@example.invalid","contrasena":"solo-prueba",
             "rol":"usuario","imagen_usuario":"/Images/avatar.png"}
            """);
    }

    @Test void identificadoresOpcionalesPermanecenNulosAlCrearUnProyecto() throws Exception {
        Proyectos decoded = mapper.readValue("{\"nombre\":\"Nuevo\",\"id_proyecto\":null}", Proyectos.class);
        assertEquals(mapper.readTree("null"), mapper.valueToTree(decoded).get("id_proyecto"));
        assertEquals("Nuevo", decoded.getNombre());
    }
}
