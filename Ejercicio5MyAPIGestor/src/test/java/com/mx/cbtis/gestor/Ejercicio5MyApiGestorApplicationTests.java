package com.mx.cbtis.gestor;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.context.ApplicationContext;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.servlet.view.xslt.XsltView;
import org.springframework.web.servlet.view.xslt.XsltViewResolver;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;
import com.mx.cbtis.gestor.modelo.*;
import com.mx.cbtis.gestor.repository.*;
import com.mx.cbtis.gestor.service.PasswordService;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class Ejercicio5MyApiGestorApplicationTests {
    @Autowired ProtectosRepository proyectos;
    @Autowired TareasRepository tareas;
    @Autowired ComentariosRepository comentarios;
    @Autowired AsignacionesRepository asignaciones;
    @Autowired UsuarioRepository usuarios;
    @Autowired PasswordService passwords;
    @Autowired ApplicationContext context;
    @Autowired MockMvc mvc;

    @Test void proyectoHttpPersisteConsultaYEliminaEnBaseAislada() throws Exception {
        mvc.perform(post("/addProyecto").contentType(MediaType.APPLICATION_JSON).content("""
            {"nombre":"Proyecto HTTP","descripcion":"Persistencia real aislada","fecha_inicio":"2026-11-08",
             "fecha_finalizacion":"2026-11-10","estado_proyecto":"En proceso","fondo":"/Images/blanco.png"}
            """))
            .andExpect(status().isOk()).andExpect(content().string("1"));
        Proyectos saved=proyectos.findAll().iterator().next();
        assertTrue(saved.getId_proyecto()>0);
        mvc.perform(get("/buscarProyecto").param("id_proyecto",saved.getId_proyecto().toString()))
            .andExpect(status().isOk()).andExpect(jsonPath("$.nombre").value("Proyecto HTTP"));
        mvc.perform(delete("/deleteProyecto").contentType(MediaType.APPLICATION_JSON)
            .content("{\"id_proyecto\":"+saved.getId_proyecto()+"}"))
            .andExpect(status().isOk()).andExpect(content().string("1"));
        assertFalse(proyectos.existsById(saved.getId_proyecto()));
        mvc.perform(get("/buscarProyecto").param("id_proyecto",saved.getId_proyecto().toString()))
            .andExpect(status().isNotFound());
    }

    @Test void registroYLoginHttpUtilizanHashSinExponerlo() throws Exception {
        mvc.perform(post("/addUsuario").contentType(MediaType.APPLICATION_JSON).content("""
            {"nombre":"Usuario HTTP","correo_electronico":"http@example.invalid",
             "contrasena":"ClaveSegura123","rol":"Administrador","imagen_usuario":"avatar.png"}
            """))
            .andExpect(status().isOk()).andExpect(content().string("1"));
        Usuario saved=usuarios.findByCorreo("http@example.invalid").orElseThrow();
        assertTrue(saved.getId_usuario()>0);
        assertNotEquals("ClaveSegura123",saved.getContrasena());
        assertTrue(passwords.matches("ClaveSegura123",saved.getContrasena()));
        assertEquals("Usuario",saved.getRol());
        mvc.perform(post("/getUsuarioLogin").contentType(MediaType.APPLICATION_JSON).content("""
            {"correo_electronico":"http@example.invalid","contrasena":"ClaveSegura123"}
            """))
            .andExpect(status().isOk()).andExpect(jsonPath("$.id_usuario").value(saved.getId_usuario()))
            .andExpect(jsonPath("$.contrasena").doesNotExist());
        mvc.perform(post("/getUsuarioLogin").contentType(MediaType.APPLICATION_JSON).content("""
            {"correo_electronico":"http@example.invalid","contrasena":"Equivocada123"}
            """))
            .andExpect(status().isUnauthorized());
    }

    @Test void contextoSeguroSinSseNiXslt() {
        assertTrue(context.getBeansOfType(XsltView.class).isEmpty());
        assertTrue(context.getBeansOfType(XsltViewResolver.class).isEmpty());
        var mappings = context.getBeansOfType(RequestMappingHandlerMapping.class);
        assertFalse(mappings.isEmpty(), "La guarda debe comprobar el contexto MVC real.");
        mappings.values().forEach(mapping -> mapping.getHandlerMethods().forEach((info, handler) -> {
            assertFalse(info.getPatternValues().contains("/**"));
            assertFalse(handler.getMethod().getGenericReturnType().getTypeName().contains("SseEmitter"));
            assertFalse(handler.getMethod().getGenericReturnType().getTypeName().contains("ServerSentEvent"));
            assertFalse(info.getProducesCondition().getProducibleMediaTypes().stream()
                .anyMatch(type -> type.toString().equals("text/event-stream")));
        }));
    }

    @Test void persistenceQueriesPreserveDatesAndFilterByParent() {
        Proyectos project=proyectos.save(new Proyectos(null,"Integración","Datos aislados",LocalDate.of(2026,11,8),LocalDate.of(2026,11,10),"En proceso","fondo.png"));
        Tareas task=tareas.save(new Tareas(null,"Tarea","Aislada",LocalDate.of(2026,11,9),"Lista","En proceso",project.getId_proyecto()));
        Comentarios comment=comentarios.save(new Comentarios(null,"2",task.getId_tarea(),"Texto persistido",LocalDateTime.of(2026,11,8,10,15)));
        Asignaciones assignment=asignaciones.save(new Asignaciones(null,2,task.getId_tarea(),LocalDate.of(2026,11,8)));
        assertTrue(project.getId_proyecto() > 0);
        assertTrue(task.getId_tarea() > 0);
        assertTrue(comment.getId_comentario() > 0);
        assertTrue(assignment.getId_asignacion() > 0);
        assertEquals(LocalDate.of(2026,11,8),proyectos.findById(project.getId_proyecto()).orElseThrow().getFecha_inicio());
        assertEquals(task.getId_tarea(),tareas.findByIdProyecto(project.getId_proyecto()).get(0).getId_tarea());
        assertTrue(tareas.findByIdProyecto(-1).isEmpty());
        assertEquals(comment.getId_comentario(),comentarios.findByTarea(task.getId_tarea()).get(0).getId_comentario());
        assertTrue(comentarios.findByTarea(-1).isEmpty());
        assertEquals(LocalDate.of(2026,11,8),asignaciones.findById(assignment.getId_asignacion()).orElseThrow().getFecha_asignacion());
    }
    @Test void emailLookupUsesBoundParametersAndCredentialsAreHashed() {
        String hash=passwords.encode("ClaveSegura123");
        Usuario user=usuarios.save(new Usuario(null,"Usuario","db@example.invalid",hash,"Usuario",null));
        assertTrue(user.getId_usuario() > 0);
        Usuario read=usuarios.findByCorreo("db@example.invalid").orElseThrow();
        assertEquals(user.getId_usuario(),read.getId_usuario());
        assertTrue(passwords.matches("ClaveSegura123",read.getContrasena()));
        assertFalse(passwords.matches("ClaveIncorrecta",read.getContrasena()));
        assertTrue(usuarios.findByCorreo("db@example.invalid' OR '1'='1").isEmpty());
    }
}
