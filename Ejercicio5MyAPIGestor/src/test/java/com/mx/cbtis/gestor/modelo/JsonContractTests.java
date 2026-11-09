package com.mx.cbtis.gestor.modelo;

import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.mx.cbtis.gestor.dto.*;

class JsonContractTests {
    private final ObjectMapper json=new ObjectMapper().registerModule(new JavaTimeModule())
        .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    record Contract(Object value,String payload) {}
    static Stream<Contract> models() {
        return Stream.of(
            new Contract(new Asignaciones(1,2,3,LocalDate.of(2026,11,8)),"""
                {"id_asignacion":1,"id_usuario":2,"id_tarea":3,"fecha_asignacion":"2026-11-08"}
                """),
            new Contract(new Comentarios(1,"2",3,"Detalle",LocalDateTime.of(2026,11,8,10,15)),"""
                {"id_comentario":1,"id_usuario":"2","id_tarea":3,"texto_comentario":"Detalle","fecha_hora":"2026-11-08T10:15:00"}
                """),
            new Contract(new Proyectos(1,"Proyecto","Detalle",LocalDate.of(2026,11,8),LocalDate.of(2026,11,9),"En proceso","/Images/blanco.png"),"""
                {"id_proyecto":1,"nombre":"Proyecto","descripcion":"Detalle","fecha_inicio":"2026-11-08","fecha_finalizacion":"2026-11-09","estado_proyecto":"En proceso","fondo":"/Images/blanco.png"}
                """),
            new Contract(new Tareas(1,"Tarea","Detalle",LocalDate.of(2026,11,9),"Lista","En proceso",2),"""
                {"id_tarea":1,"nombre":"Tarea","descripcion":"Detalle","fecha_vencimiento":"2026-11-09","lista":"Lista","estado":"En proceso","id_proyecto":2}
                """));
    }
    @ParameterizedTest @MethodSource("models")
    void entityJsonRoundTripPreservesEveryPersistedValue(Contract c) throws Exception {
        var expected=json.readTree(c.payload());
        assertEquals(expected,json.valueToTree(c.value()));
        Object restored=json.readValue(c.payload(),c.value().getClass());
        assertEquals(expected,json.valueToTree(restored));
    }

    @Test void userPasswordIsWritableButNeverSerialized() throws Exception {
        Usuario user=new Usuario(1,"Nombre","test@example.invalid","secret-test","Usuario","avatar.png");
        assertEquals("secret-test",user.getContrasena());
        var publicJson=json.readTree("""
            {"id_usuario":1,"nombre":"Nombre","correo_electronico":"test@example.invalid","rol":"Usuario","imagen_usuario":"avatar.png"}
            """);
        assertEquals(publicJson,json.valueToTree(user));
        Usuario restored=json.readValue("""
            {"id_usuario":1,"nombre":"Nombre","correo_electronico":"test@example.invalid","contrasena":"input-test","rol":"Usuario","imagen_usuario":"avatar.png"}
            """,Usuario.class);
        assertEquals("input-test",restored.getContrasena());
        assertEquals(publicJson,json.valueToTree(restored));
    }

    static Stream<Contract> requestContracts() {
        return Stream.of(
            new Contract(new ProyectoRequest(1,"Proyecto","Detalle",LocalDate.of(2026,11,8),LocalDate.of(2026,11,9),"En proceso","fondo.png"),"""
                {"id_proyecto":1,"nombre":"Proyecto","descripcion":"Detalle","fecha_inicio":"2026-11-08","fecha_finalizacion":"2026-11-09","estado_proyecto":"En proceso","fondo":"fondo.png"}
                """),
            new Contract(new TareaRequest(1,"Tarea","Detalle",LocalDate.of(2026,11,9),"Lista","En proceso",2),"""
                {"id_tarea":1,"nombre":"Tarea","descripcion":"Detalle","fecha_vencimiento":"2026-11-09","lista":"Lista","estado":"En proceso","id_proyecto":2}
                """),
            new Contract(new ComentarioRequest(1,"2",3,"Detalle",LocalDateTime.of(2026,11,8,10,15)),"""
                {"id_comentario":1,"id_usuario":"2","id_tarea":3,"texto_comentario":"Detalle","fecha_hora":"2026-11-08T10:15:00"}
                """),
            new Contract(new AsignacionRequest(1,2,3,LocalDate.of(2026,11,8)),"""
                {"id_asignacion":1,"id_usuario":2,"id_tarea":3,"fecha_asignacion":"2026-11-08"}
                """),
            new Contract(new UsuarioRequest(1,"Nombre","test@example.invalid","secret-test","avatar.png"),"""
                {"id_usuario":1,"nombre":"Nombre","correo_electronico":"test@example.invalid","contrasena":"secret-test","imagen_usuario":"avatar.png"}
                """),
            new Contract(new ProyectoIdRequest(1),"{\"id_proyecto\":1}"),
            new Contract(new TareaIdRequest(1),"{\"id_tarea\":1}"),
            new Contract(new ComentarioIdRequest(1),"{\"id_comentario\":1}"),
            new Contract(new AsignacionIdRequest(1),"{\"id_asignacion\":1}"),
            new Contract(new UsuarioIdRequest(1),"{\"id_usuario\":1}"),
            new Contract(new ProyectoFiltroRequest(2),"{\"id_proyecto\":2}"),
            new Contract(new TareaFiltroRequest(3),"{\"id_tarea\":3}"),
            new Contract(new LoginRequest("test@example.invalid","secret-test"),"{\"correo_electronico\":\"test@example.invalid\",\"contrasena\":\"secret-test\"}")
        );
    }
    @ParameterizedTest @MethodSource("requestContracts")
    void requestRecordsRetainTheirExplicitJsonContract(Contract c) throws Exception {
        assertEquals(json.readTree(c.payload()),json.valueToTree(c.value()));
        Object restored=json.readValue(c.payload(),c.value().getClass());
        assertEquals(c.value(),restored);
    }
}
