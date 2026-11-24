package com.mx.cbtis.gestor.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.repository.CrudRepository;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mx.cbtis.gestor.modelo.*;
import com.mx.cbtis.gestor.repository.*;
import com.mx.cbtis.gestor.service.PasswordService;

@WebMvcTest({ProyectosController.class, TareasController.class, ComentariosController.class,
    AsignacionesController.class, UsuarioController.class})
class ApiCrudTests {
    @Autowired MockMvc mvc;
    @Autowired ObjectMapper json;
    @MockBean ProtectosRepository proyectos;
    @MockBean TareasRepository tareas;
    @MockBean ComentariosRepository comentarios;
    @MockBean AsignacionesRepository asignaciones;
    @MockBean UsuarioRepository usuarios;
    @MockBean PasswordService passwords;

    record Resource(String singular, String plural, String id, String queryId, String payload, Class<?> type) {}
    static Stream<Resource> resources() {
        return Stream.of(
            new Resource("Proyecto", "Proyectos", "id_proyecto", "id_proyecto", """
                {"nombre":"Proyecto seguro","descripcion":"Contrato de prueba","fecha_inicio":"2026-11-08",
                 "fecha_finalizacion":"2026-11-10","estado_proyecto":"En proceso","fondo":"/Images/blanco.png"}
                """, Proyectos.class),
            new Resource("Tarea", "Tareas", "id_tarea", "id_tarea", """
                {"nombre":"Revisar contratos","descripcion":"Validar solicitud","fecha_vencimiento":"2026-11-10",
                 "lista":"Pendientes","estado":"En proceso","id_proyecto":2}
                """, Tareas.class),
            new Resource("Comentario", "Comentarios", "id_comentario", "id", """
                {"id_usuario":"3","id_tarea":4,"texto_comentario":"Comentario verificado","fecha_hora":"2026-11-08T09:30:00"}
                """, Comentarios.class),
            new Resource("Asignacion", "Asignaciones", "id_asignacion", "id", """
                {"id_usuario":3,"id_tarea":4,"fecha_asignacion":"2026-11-08"}
                """, Asignaciones.class));
    }
    @SuppressWarnings("unchecked")
    private CrudRepository<Object, Integer> repository(Resource resource) {
        Object selected = switch (resource.singular()) {
            case "Proyecto" -> proyectos;
            case "Tarea" -> tareas;
            case "Comentario" -> comentarios;
            case "Asignacion" -> asignaciones;
            default -> throw new IllegalArgumentException("Unknown test resource");
        };
        return (CrudRepository<Object, Integer>) selected;
    }
    private String route(Resource r, String operation) {
        String noun = r.singular().equals("Tarea") && !operation.equals("buscar") ? r.plural() : r.singular();
        return "/" + operation + noun;
    }
    private String withId(Resource r, int id) {
        return "{\"" + r.id() + "\":" + id + "," + r.payload().strip().substring(1);
    }

    @ParameterizedTest @MethodSource("resources")
    void createMapsOnlyExplicitFieldsAndRejectsInvalidInputs(Resource r) throws Exception {
        CrudRepository<Object,Integer> repo = repository(r);
        mvc.perform(post(route(r,"add")).contentType(MediaType.APPLICATION_JSON).content(r.payload()))
            .andExpect(status().isOk()).andExpect(content().string("1"));
        var captor = org.mockito.ArgumentCaptor.forClass(Object.class);
        verify(repo).save(captor.capture());
        var captured = (com.fasterxml.jackson.databind.node.ObjectNode) json.valueToTree(captor.getValue());
        captured.remove(r.id());
        assertEquals(json.readTree(r.payload()), captured);
        clearInvocations(repo);
        mvc.perform(post(route(r,"add")).contentType(MediaType.APPLICATION_JSON).content(withId(r,99)))
            .andExpect(status().isBadRequest());
        mvc.perform(post(route(r,"add")).contentType(MediaType.APPLICATION_JSON).content("{}"))
            .andExpect(status().isBadRequest());
        mvc.perform(post(route(r,"add")).contentType(MediaType.APPLICATION_JSON).content("null"))
            .andExpect(status().isBadRequest());
        verifyNoInteractions(repo);
    }

    @ParameterizedTest @MethodSource("resources")
    void queryReturnsDataOr404AndRejectsInvalidId(Resource r) throws Exception {
        CrudRepository<Object,Integer> repo = repository(r);
        Object value = json.readValue(withId(r,7),r.type());
        when(repo.findById(7)).thenReturn(Optional.of(value));
        when(repo.findById(8)).thenReturn(Optional.empty());
        mvc.perform(get("/buscar"+r.singular()).param(r.queryId(),"7"))
            .andExpect(status().isOk()).andExpect(content().json(withId(r,7),true));
        mvc.perform(get("/buscar"+r.singular()).param(r.queryId(),"8")).andExpect(status().isNotFound());
        mvc.perform(get("/buscar"+r.singular()).param(r.queryId(),"0")).andExpect(status().isBadRequest());
        mvc.perform(get("/buscar"+r.singular()).param(r.queryId(),"bad")).andExpect(status().isBadRequest());
    }

    @ParameterizedTest @MethodSource("resources")
    void updateAndDeleteRequireAnExistingPositiveId(Resource r) throws Exception {
        CrudRepository<Object,Integer> repo = repository(r);
        when(repo.existsById(7)).thenReturn(true);
        when(repo.existsById(8)).thenReturn(false);
        mvc.perform(post(route(r,"update")).contentType(MediaType.APPLICATION_JSON).content(withId(r,7)))
            .andExpect(status().isOk()).andExpect(content().string("1"));
        verify(repo).save(argThat(value -> json.valueToTree(value).get(r.id()).asInt()==7));
        mvc.perform(post(route(r,"update")).contentType(MediaType.APPLICATION_JSON).content(withId(r,8)))
            .andExpect(status().isNotFound());
        mvc.perform(post(route(r,"update")).contentType(MediaType.APPLICATION_JSON).content(r.payload()))
            .andExpect(status().isBadRequest());
        mvc.perform(delete(route(r,"delete")).contentType(MediaType.APPLICATION_JSON).content("{\""+r.id()+"\":7}"))
            .andExpect(status().isOk()).andExpect(content().string("1"));
        verify(repo).deleteById(7);
        mvc.perform(delete(route(r,"delete")).contentType(MediaType.APPLICATION_JSON).content("{\""+r.id()+"\":8}"))
            .andExpect(status().isNotFound());
        mvc.perform(delete(route(r,"delete")).contentType(MediaType.APPLICATION_JSON).content("{\""+r.id()+"\":-1}"))
            .andExpect(status().isBadRequest());
    }

    @ParameterizedTest @MethodSource("resources")
    void listSupportsPopulatedAndEmptyRepository(Resource r) throws Exception {
        CrudRepository<Object,Integer> repo=repository(r);
        Object value=json.readValue(withId(r,7),r.type());
        when(repo.findAll()).thenReturn(List.of(value),List.of());
        mvc.perform(post("/buscar"+r.plural())).andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(1)).andExpect(jsonPath("$[0]."+r.id()).value(7));
        mvc.perform(post("/buscar"+r.plural())).andExpect(status().isOk()).andExpect(content().json("[]"));
    }

    @Test void filteredQueriesUseOnlyPositiveParentIdentifiers() throws Exception {
        Tareas tarea = new Tareas(7,"Tarea","Detalle",java.time.LocalDate.of(2026,11,9),"Lista","En proceso",2);
        Comentarios comentario = new Comentarios(7,"3",4,"Texto",java.time.LocalDateTime.of(2026,11,8,9,0));
        when(tareas.findByIdProyecto(2)).thenReturn(List.of(tarea));
        when(comentarios.findByTarea(4)).thenReturn(List.of(comentario));
        mvc.perform(post("/getTarea").contentType(MediaType.APPLICATION_JSON).content("{\"id_proyecto\":2}"))
            .andExpect(status().isOk()).andExpect(jsonPath("$[0].id_tarea").value(7));
        mvc.perform(post("/getComentario").contentType(MediaType.APPLICATION_JSON).content("{\"id_tarea\":4}"))
            .andExpect(status().isOk()).andExpect(jsonPath("$[0].texto_comentario").value("Texto"));
        mvc.perform(post("/getTarea").contentType(MediaType.APPLICATION_JSON).content("{\"id_proyecto\":0}"))
            .andExpect(status().isBadRequest());
        mvc.perform(post("/getComentario").contentType(MediaType.APPLICATION_JSON).content("{}"))
            .andExpect(status().isBadRequest());
    }

    @Test void reversedProjectDatesAreRejectedWithoutPersistence() throws Exception {
        var r=resources().findFirst().orElseThrow();
        String reversed=r.payload().replace("2026-11-10","2026-11-01");
        mvc.perform(post("/addProyecto").contentType(MediaType.APPLICATION_JSON).content(reversed)).andExpect(status().isBadRequest());
        when(proyectos.existsById(7)).thenReturn(true);
        mvc.perform(post("/updateProyecto").contentType(MediaType.APPLICATION_JSON).content("{\"id_proyecto\":7,"+reversed.strip().substring(1)))
            .andExpect(status().isBadRequest());
        verify(proyectos,never()).save(any());
    }

    @Test void databaseErrorsDoNotExposeInternalDetails() throws Exception {
        when(proyectos.findAll()).thenThrow(new DataIntegrityViolationException("private SQL internal detail"));
        mvc.perform(post("/buscarProyectos")).andExpect(status().isInternalServerError())
            .andExpect(content().json("{\"error\":\"No fue posible completar la operación.\"}",true));
    }
}
