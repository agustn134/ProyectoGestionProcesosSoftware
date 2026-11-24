package com.mx.cbtis.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.mx.cbtis.modelo.*;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.MockedConstruction;
import org.springframework.http.*;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;

/** Unit contracts for every public client operation; the HTTP server is an isolated dependency. */
class GestorControllerContractTests {
    private final GestorController controller = new GestorController();

    private static Proyectos validProject() {
        return new Proyectos(7,"Proyecto","Descripción","2026-11-01","2026-11-30","En proceso","/Images/blanco.png");
    }

    @Test void paginasPublicasResuelvenVistasEsperadas() {
        assertAll(() -> assertEquals("home",controller.getHome()),
            () -> assertEquals("tutorial",controller.getTutorial()),
            () -> assertEquals("manual",controller.getManual()),
            () -> assertEquals("login",controller.getLogin()));
    }

    @Test void registroPreparaFormularioVacio() {
        ExtendedModelMap model = new ExtendedModelMap();
        assertEquals("register",controller.getRegister(model));
        assertInstanceOf(Usuario.class,model.get("usuario"));
        assertNull(((Usuario)model.get("usuario")).getId_usuario());
    }

    @Test void registroEntregaLosDatosAlBackendYRedirigeALogin() {
        Usuario user = new Usuario(9,"Prueba","prueba@example.invalid","solo-prueba","usuario",null);
        try (MockedConstruction<RestTemplate> http = mockConstruction(RestTemplate.class,(mock,context) ->
                when(mock.postForEntity(anyString(),any(),eq(Integer.class))).thenReturn(ResponseEntity.ok(1)))) {
            assertEquals("redirect:/login",controller.setSaveUsuario(user).getViewName());
            verify(http.constructed().get(0)).postForEntity("http://localhost:8081/addUsuario",user,Integer.class);
        }
    }

    @Test void registroInvalidoMuestraError400SinReflejarContrasena() {
        Usuario user=new Usuario(null,"Prueba","prueba@example.invalid","corta",null,null);
        try (MockedConstruction<RestTemplate> ignored=mockConstruction(RestTemplate.class,(mock,context) ->
                when(mock.postForEntity(anyString(),any(),eq(Integer.class)))
                    .thenThrow(HttpClientErrorException.create(HttpStatus.BAD_REQUEST,"Invalid registration",HttpHeaders.EMPTY,new byte[0],null)))) {
            ModelAndView result=controller.setSaveUsuario(user);
            assertEquals(HttpStatus.BAD_REQUEST,result.getStatus());
            assertEquals("register",result.getViewName());
            assertEquals("Prueba",((Usuario)result.getModel().get("usuario")).getNombre());
            assertNull(((Usuario)result.getModel().get("usuario")).getContrasena());
            assertTrue(result.getModel().get("errorRegistro").toString().contains("Revisa"));
        }
    }

    @Test void registroSinConfirmacionDevuelveFormularioCon502() {
        try (MockedConstruction<RestTemplate> ignored=mockConstruction(RestTemplate.class,(mock,context) ->
                when(mock.postForEntity(anyString(),any(),eq(Integer.class))).thenReturn(ResponseEntity.ok(0)))) {
            ModelAndView result=controller.setSaveUsuario(new Usuario());
            assertEquals(HttpStatus.BAD_GATEWAY,result.getStatus());
            assertEquals("register",result.getViewName());
            assertTrue(result.getModel().get("errorRegistro").toString().contains("no confirmó"));
        }
    }

    @Test void registroConServicioCaidoDevuelveErrorVisibleSinDatosSecretos() {
        Usuario user=new Usuario(null,"Prueba","prueba@example.invalid","solo-prueba",null,null);
        try (MockedConstruction<RestTemplate> ignored=mockConstruction(RestTemplate.class,(mock,context) ->
                when(mock.postForEntity(anyString(),any(),eq(Integer.class))).thenThrow(new RestClientException("sin conexión")))) {
            ModelAndView result=controller.setSaveUsuario(user);
            assertEquals(HttpStatus.BAD_GATEWAY,result.getStatus());
            assertNull(user.getContrasena());
            assertTrue(result.getModel().get("errorRegistro").toString().contains("No se pudo conectar"));
        }
    }

    @Test void inicioSinDatosProduceUnaListaVacia() {
        ExtendedModelMap model = new ExtendedModelMap();
        try (MockedConstruction<RestTemplate> http = mockConstruction(RestTemplate.class)) {
            assertEquals("inicio",controller.getInicio(model));
            assertEquals(List.of(),model.get("listadoProyectos"));
            assertInstanceOf(Proyectos.class,model.get("proyectos"));
            verify(http.constructed().get(0)).postForObject("http://localhost:8081/buscarProyectos",null,Proyectos[].class);
        }
    }

    @Test void inicioConBackendCaidoConservaElFormularioYExplicaElError() {
        ExtendedModelMap model = new ExtendedModelMap();
        try (MockedConstruction<RestTemplate> ignored = mockConstruction(RestTemplate.class,(mock,context) ->
                when(mock.postForObject(anyString(),any(),eq(Proyectos[].class))).thenThrow(new RestClientException("sin conexión")))) {
            assertEquals("inicio",controller.getInicio(model));
            assertEquals(List.of(),model.get("listadoProyectos"));
            assertTrue(model.get("errorProyecto").toString().contains("No se pudieron cargar"));
        }
    }

    static Stream<Arguments> proyectosInvalidos() {
        return Stream.of(
            Arguments.of("nombre nulo", (Consumer<Proyectos>)(p -> p.setNombre(null)), "El nombre es obligatorio."),
            Arguments.of("nombre vacío", (Consumer<Proyectos>)(p -> p.setNombre("  ")), "El nombre es obligatorio."),
            Arguments.of("nombre demasiado largo", (Consumer<Proyectos>)(p -> p.setNombre("x".repeat(101))), "El nombre no debe superar 100 caracteres."),
            Arguments.of("descripción nula", (Consumer<Proyectos>)(p -> p.setDescripcion(null)), "La descripción es obligatoria."),
            Arguments.of("descripción vacía", (Consumer<Proyectos>)(p -> p.setDescripcion(" ")), "La descripción es obligatoria."),
            Arguments.of("descripción demasiado larga", (Consumer<Proyectos>)(p -> p.setDescripcion("x".repeat(101))), "La descripción no debe superar 100 caracteres."),
            Arguments.of("fecha inexistente", (Consumer<Proyectos>)(p -> p.setFecha_inicio("2026-02-30")), "Las fechas son obligatorias y deben ser válidas."),
            Arguments.of("fecha nula", (Consumer<Proyectos>)(p -> p.setFecha_inicio(null)), "Las fechas son obligatorias y deben ser válidas."),
            Arguments.of("fin anterior", (Consumer<Proyectos>)(p -> p.setFecha_finalizacion("2026-10-01")), "La fecha final no puede ser anterior a la inicial."),
            Arguments.of("estado nulo", (Consumer<Proyectos>)(p -> p.setEstado_proyecto(null)), "Selecciona un estado válido."),
            Arguments.of("estado desconocido", (Consumer<Proyectos>)(p -> p.setEstado_proyecto("Otro")), "Selecciona un estado válido.")
        );
    }

    @ParameterizedTest(name="{0}") @MethodSource("proyectosInvalidos")
    void formularioInvalidoNoEnviaDatos(String caso, Consumer<Proyectos> mutate, String expected) {
        Proyectos project=validProject();
        mutate.accept(project);
        try (MockedConstruction<RestTemplate> http=mockConstruction(RestTemplate.class)) {
            ModelAndView result=controller.setSaveProyecto(project);
            assertEquals(HttpStatus.BAD_REQUEST,result.getStatus());
            assertEquals("inicio",result.getViewName());
            assertSame(project,result.getModel().get("proyectos"));
            assertEquals(expected,result.getModel().get("errorProyecto"));
            assertTrue(http.constructed().isEmpty(),"Invalid form must not call the API");
        }
    }

    @Test void proyectoValidoCompletaFondoPredeterminadoAntesDeEnviar() {
        Proyectos project=validProject(); project.setFondo(null); project.setEstado_proyecto("Finalizado");
        try (MockedConstruction<RestTemplate> http=mockConstruction(RestTemplate.class,(mock,context) ->
                when(mock.postForEntity(anyString(),any(),eq(Integer.class))).thenReturn(ResponseEntity.ok(1)))) {
            assertEquals("redirect:/inicio",controller.setSaveProyecto(project).getViewName());
            assertEquals("/Images/blanco.png",project.getFondo());
            verify(http.constructed().get(0)).postForEntity("http://localhost:8081/addProyecto",project,Integer.class);
        }
    }

    @Test void respuestaSinConfirmacionNoSePresentaComoGuardadoExitoso() {
        try (MockedConstruction<RestTemplate> ignored=mockConstruction(RestTemplate.class,(mock,context) ->
                when(mock.postForEntity(anyString(),any(),eq(Integer.class))).thenReturn(ResponseEntity.ok(0)))) {
            ModelAndView result=controller.setSaveProyecto(validProject());
            assertEquals(HttpStatus.BAD_GATEWAY,result.getStatus());
            assertTrue(result.getModel().get("errorProyecto").toString().contains("no confirmó"));
        }
    }

    @Test void consultarProyectosNulosDevuelveListaVacia() {
        ExtendedModelMap model=new ExtendedModelMap();
        try (MockedConstruction<RestTemplate> ignored=mockConstruction(RestTemplate.class)) {
            ResponseEntity<Object> result=controller.consultarProyectos(model);
            assertEquals(HttpStatus.OK,result.getStatusCode());
            assertEquals(List.of(),result.getBody());
            assertEquals(List.of(),model.get("listaProyectos"));
        }
    }

    @Test void borrarProyectoUtilizaDeleteConElProyectoCorrecto() {
        Proyectos project=validProject();
        try (MockedConstruction<RestTemplate> http=mockConstruction(RestTemplate.class,(mock,context) ->
                when(mock.exchange(anyString(),eq(HttpMethod.DELETE),any(HttpEntity.class),eq(Integer.class))).thenReturn(ResponseEntity.ok(1)))) {
            assertEquals("redirect:/inicio",controller.eliminarProyecto(project).getViewName());
            verify(http.constructed().get(0)).exchange(eq("http://localhost:8081/deleteProyecto"),eq(HttpMethod.DELETE),
                argThat((HttpEntity<?> entity) -> entity.getBody()==project),eq(Integer.class));
        }
    }

    @Test void idProyectoAusenteOInvalidoNoConsultaApi() {
        try (MockedConstruction<RestTemplate> http=mockConstruction(RestTemplate.class)) {
            Proyectos project=validProject(); project.setId_proyecto(null);
            assertEquals(HttpStatus.BAD_REQUEST,controller.updateProyecto(project).getStatusCode());
            project.setId_proyecto(0);
            assertEquals(HttpStatus.BAD_REQUEST,controller.updateProyecto(project).getStatusCode());
            assertTrue(http.constructed().isEmpty());
        }
    }

    @Test void consultaProyectoNuloSeTraduceEnNoEncontrado() {
        try (MockedConstruction<RestTemplate> ignored=mockConstruction(RestTemplate.class)) {
            assertEquals(HttpStatus.NOT_FOUND,controller.updateProyecto(validProject()).getStatusCode());
        }
    }

    @Test void consultaProyectoSinIdSeTraduceEnNoEncontrado() {
        try (MockedConstruction<RestTemplate> ignored=mockConstruction(RestTemplate.class,(mock,context) ->
                when(mock.getForObject(anyString(),eq(Proyectos.class),anyMap())).thenReturn(new Proyectos()))) {
            assertEquals(HttpStatus.NOT_FOUND,controller.updateProyecto(validProject()).getStatusCode());
        }
    }

    @Test void consultaProyectoConFalloDeRedSeTraduceEnBadGateway() {
        try (MockedConstruction<RestTemplate> ignored=mockConstruction(RestTemplate.class,(mock,context) ->
                when(mock.getForObject(anyString(),eq(Proyectos.class),anyMap())).thenThrow(new RestClientException("sin conexión")))) {
            assertEquals(HttpStatus.BAD_GATEWAY,controller.updateProyecto(validProject()).getStatusCode());
        }
    }

    @Test void loginTransfiereCredencialesYConservaLaRespuestaDelBackend() {
        Usuario request=new Usuario(null,null,"prueba@example.invalid","solo-prueba",null,null);
        Usuario authenticated=new Usuario(9,"Prueba","prueba@example.invalid",null,"usuario",null);
        try (MockedConstruction<RestTemplate> http=mockConstruction(RestTemplate.class,(mock,context) ->
                when(mock.postForEntity(anyString(),any(),eq(Usuario.class))).thenReturn(ResponseEntity.ok(authenticated)))) {
            assertSame(authenticated,controller.verificarUsuario(request).getBody());
            verify(http.constructed().get(0)).postForEntity("http://localhost:8081/getUsuarioLogin",request,Usuario.class);
        }
    }

    @Test void loginNoExponeContrasenaAunqueLaApiLaIncluya() {
        Usuario authenticated=new Usuario(9,"Prueba","prueba@example.invalid","no-debe-salir","usuario",null);
        try (MockedConstruction<RestTemplate> ignored=mockConstruction(RestTemplate.class,(mock,context) ->
                when(mock.postForEntity(anyString(),any(),eq(Usuario.class))).thenReturn(ResponseEntity.ok(authenticated)))) {
            assertNull(controller.verificarUsuario(new Usuario()).getBody().getContrasena());
        }
    }

    @Test void credencialesIncorrectasDevuelven401EnLugarDe500() {
        try (MockedConstruction<RestTemplate> ignored=mockConstruction(RestTemplate.class,(mock,context) ->
                when(mock.postForEntity(anyString(),any(),eq(Usuario.class)))
                    .thenThrow(HttpClientErrorException.create(HttpStatus.UNAUTHORIZED,"Unauthorized",HttpHeaders.EMPTY,new byte[0],null)))) {
            ResponseEntity<Usuario> response=controller.verificarUsuario(new Usuario());
            assertEquals(HttpStatus.UNAUTHORIZED,response.getStatusCode());
            assertNull(response.getBody());
        }
    }

    @Test void loginConRespuestaVaciaONoAutenticadaNoDaAcceso() {
        try (MockedConstruction<RestTemplate> ignored=mockConstruction(RestTemplate.class,(mock,context) ->
                when(mock.postForEntity(anyString(),any(),eq(Usuario.class))).thenReturn(ResponseEntity.ok(null)))) {
            assertEquals(HttpStatus.UNAUTHORIZED,controller.verificarUsuario(new Usuario()).getStatusCode());
        }
        try (MockedConstruction<RestTemplate> ignored=mockConstruction(RestTemplate.class,(mock,context) ->
                when(mock.postForEntity(anyString(),any(),eq(Usuario.class))).thenReturn(ResponseEntity.ok(new Usuario())))) {
            assertEquals(HttpStatus.UNAUTHORIZED,controller.verificarUsuario(new Usuario()).getStatusCode());
        }
    }

    @Test void loginConServicioCaidoDevuelve502Diferenciado() {
        try (MockedConstruction<RestTemplate> ignored=mockConstruction(RestTemplate.class,(mock,context) ->
                when(mock.postForEntity(anyString(),any(),eq(Usuario.class))).thenThrow(new RestClientException("sin conexión")))) {
            assertEquals(HttpStatus.BAD_GATEWAY,controller.verificarUsuario(new Usuario()).getStatusCode());
        }
    }

    @Test void consultarUsuariosPublicaListaYModelo() {
        Usuario user=new Usuario(9,"Prueba","prueba@example.invalid",null,"usuario",null);
        ExtendedModelMap model=new ExtendedModelMap();
        try (MockedConstruction<RestTemplate> http=mockConstruction(RestTemplate.class,(mock,context) ->
                when(mock.postForObject(anyString(),any(),eq(Usuario[].class))).thenReturn(new Usuario[]{user}))) {
            assertEquals(List.of(user),controller.consultarUsuarios(model).getBody());
            assertEquals(List.of(user),model.get("listaUsuarios"));
            verify(http.constructed().get(0)).postForObject(eq("http://localhost:8081/buscarUsuarios"),any(),eq(Usuario[].class));
        }
    }

    @Test void obtenerTareasFiltraPorProyectoYPublicaLista() {
        Tareas filter=new Tareas(); filter.setId_proyecto(7);
        Tareas task=new Tareas(11,"Tarea","Revisar","2026-11-10","Pendiente","Abierta",7);
        ExtendedModelMap model=new ExtendedModelMap();
        try (MockedConstruction<RestTemplate> http=mockConstruction(RestTemplate.class,(mock,context) ->
                when(mock.postForObject(anyString(),any(),eq(Tareas[].class))).thenReturn(new Tareas[]{task}))) {
            assertEquals(List.of(task),controller.obtenerTareas(filter,model).getBody());
            assertEquals(List.of(task),model.get("tareas"));
            verify(http.constructed().get(0)).postForObject("http://localhost:8081/getTarea",filter,Tareas[].class);
        }
    }

    @Test void obtenerComentariosConservaFiltroYAutor() {
        Comentarios filter=new Comentarios(); filter.setId_tarea(11);
        Comentarios comment=new Comentarios(5,"usuario-9",11,"Revisar","2026-11-08T10:30:00");
        ExtendedModelMap model=new ExtendedModelMap();
        try (MockedConstruction<RestTemplate> http=mockConstruction(RestTemplate.class,(mock,context) ->
                when(mock.postForObject(anyString(),any(),eq(Comentarios[].class))).thenReturn(new Comentarios[]{comment}))) {
            assertEquals(List.of(comment),controller.obtenerComentarios(filter,model).getBody());
            assertEquals(List.of(comment),model.get("comentarios"));
            verify(http.constructed().get(0)).postForObject("http://localhost:8081/getComentario",filter,Comentarios[].class);
        }
    }

    @Test void formularioProyectoIncluyeTareaComentarioYUsuarios() {
        Usuario user=new Usuario(9,"Prueba","prueba@example.invalid",null,"usuario",null);
        ExtendedModelMap model=new ExtendedModelMap();
        try (MockedConstruction<RestTemplate> ignored=mockConstruction(RestTemplate.class,(mock,context) ->
                when(mock.postForObject(anyString(),any(),eq(Usuario[].class))).thenReturn(new Usuario[]{user}))) {
            assertEquals("proyecto",controller.getProyecto(model));
            assertInstanceOf(Tareas.class,model.get("tarea"));
            assertInstanceOf(Comentarios.class,model.get("comentarios"));
            assertEquals(List.of(user),model.get("listaUsuarios"));
        }
    }

    @Test void formularioTareaPreparaUnaTareaSinHacerSolicitudes() {
        ExtendedModelMap model=new ExtendedModelMap();
        try (MockedConstruction<RestTemplate> http=mockConstruction(RestTemplate.class)) {
            assertEquals("proyecto",controller.getTarea(model));
            assertInstanceOf(Tareas.class,model.get("tareas"));
            http.constructed().forEach(mock -> verifyNoInteractions(mock));
        }
    }

    @Test void guardarTareaConservaProyectoEnLaRedireccion() {
        Tareas task=new Tareas(); task.setId_proyecto(7);
        try (MockedConstruction<RestTemplate> http=mockConstruction(RestTemplate.class,(mock,context) ->
                when(mock.postForEntity(anyString(),any(),eq(Integer.class))).thenReturn(ResponseEntity.ok(1)))) {
            ModelAndView result=controller.setSaveTareas(task);
            assertEquals("redirect:/proyecto",result.getViewName());
            assertEquals(7,result.getModel().get("id_proyecto"));
            verify(http.constructed().get(0)).postForEntity("http://localhost:8081/addTareas",task,Integer.class);
        }
    }

    @Test void guardarComentarioEntregaElContenidoAlBackend() {
        Comentarios comment=new Comentarios(); comment.setTexto_comentario("Revisar");
        try (MockedConstruction<RestTemplate> http=mockConstruction(RestTemplate.class,(mock,context) ->
                when(mock.postForEntity(anyString(),any(),eq(Integer.class))).thenReturn(ResponseEntity.ok(1)))) {
            assertEquals("proyecto",controller.setSaveComentario(comment));
            verify(http.constructed().get(0)).postForEntity("http://localhost:8081/addComentario",comment,Integer.class);
        }
    }

    @Test void actualizarTareaConsultaUsandoElIdentificador() {
        Tareas task=new Tareas(); task.setId_tarea(11);
        try (MockedConstruction<RestTemplate> http=mockConstruction(RestTemplate.class,(mock,context) ->
                when(mock.getForObject(anyString(),eq(Tareas.class),anyMap())).thenReturn(task))) {
            assertSame(task,controller.updateTarea(task).getBody());
            verify(http.constructed().get(0)).getForObject("http://localhost:8081/buscarTarea?id_tarea={id_tarea}",Tareas.class,Map.of("id_tarea",11));
        }
    }

    @Test void eliminarTareaUsaDeleteYRedirige() {
        Tareas task=new Tareas(); task.setId_tarea(11);
        try (MockedConstruction<RestTemplate> http=mockConstruction(RestTemplate.class,(mock,context) ->
                when(mock.exchange(anyString(),eq(HttpMethod.DELETE),any(HttpEntity.class),eq(Integer.class))).thenReturn(ResponseEntity.ok(1)))) {
            assertEquals("redirect:/proyecto",controller.eliminarTarea(task).getViewName());
            verify(http.constructed().get(0)).exchange(eq("http://localhost:8081/deleteTareas"),eq(HttpMethod.DELETE),
                argThat((HttpEntity<?> entity) -> entity.getBody()==task),eq(Integer.class));
        }
    }

    @Test void verificarYCambiarTareaPreservanLaConfirmacionDelBackend() {
        Tareas task=new Tareas(); task.setId_tarea(11);
        try (MockedConstruction<RestTemplate> http=mockConstruction(RestTemplate.class,(mock,context) ->
                when(mock.postForEntity(anyString(),any(),eq(Integer.class))).thenReturn(ResponseEntity.ok(1)))) {
            assertEquals(1,controller.verificarTarea(task).getBody());
            assertEquals(1,controller.cambiosTareas(task).getBody());
            assertEquals(2,http.constructed().size());
            http.constructed().forEach(mock -> verify(mock).postForEntity("http://localhost:8081/addTareas",task,Integer.class));
        }
    }

    @Test void consultarListasDeTareasYComentariosConservaLosRegistros() {
        Tareas task=new Tareas(); task.setId_tarea(11);
        Comentarios comment=new Comentarios(); comment.setId_comentario(5);
        try (MockedConstruction<RestTemplate> http=mockConstruction(RestTemplate.class,(mock,context) -> {
            when(mock.postForObject(anyString(),any(),eq(Tareas[].class))).thenReturn(new Tareas[]{task});
            when(mock.postForObject(anyString(),any(),eq(Comentarios[].class))).thenReturn(new Comentarios[]{comment});
        })) {
            assertEquals(List.of(task),controller.consultarTareas().getBody());
            assertEquals(List.of(comment),controller.consultarComentarios().getBody());
            verify(http.constructed().get(0)).postForObject(eq("http://localhost:8081/buscarTareas"),any(),eq(Tareas[].class));
            verify(http.constructed().get(1)).postForObject(eq("http://localhost:8081/buscarComentarios"),any(),eq(Comentarios[].class));
        }
    }

    @Test void falloBackendEnConsultaDeUsuariosNoDevuelveUnExitoFalso() {
        try (MockedConstruction<RestTemplate> ignored=mockConstruction(RestTemplate.class,(mock,context) ->
                when(mock.postForObject(anyString(),any(),eq(Usuario[].class))).thenThrow(new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR)))) {
            assertThrows(RestClientException.class,() -> controller.consultarUsuarios(new ExtendedModelMap()));
        }
    }
}
