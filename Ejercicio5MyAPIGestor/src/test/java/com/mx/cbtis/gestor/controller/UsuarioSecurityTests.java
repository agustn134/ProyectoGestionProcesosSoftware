package com.mx.cbtis.gestor.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import com.mx.cbtis.gestor.modelo.Usuario;
import com.mx.cbtis.gestor.repository.UsuarioRepository;
import com.mx.cbtis.gestor.service.PasswordService;

@WebMvcTest(UsuarioController.class)
class UsuarioSecurityTests {
    @Autowired MockMvc mvc;
    @MockBean UsuarioRepository repository;
    @MockBean PasswordService passwords;
    private static final String PAYLOAD="""
        {"nombre":"Usuario prueba","correo_electronico":"test@example.invalid","contrasena":"ClaveLarga123",
         "rol":"Administrador","imagen_usuario":"/Images/usuario.png"}
        """;
    private Usuario user() { return new Usuario(7,"Usuario prueba","test@example.invalid","stored-hash","Usuario","/Images/usuario.png"); }
    private String withId(int id) { return "{\"id_usuario\":"+id+","+PAYLOAD.strip().substring(1); }

    @Test void registrationHashesPasswordAndDoesNotAcceptPrivilegesFromJson() throws Exception {
        when(passwords.encode("ClaveLarga123")).thenReturn("encoded-hash");
        mvc.perform(post("/addUsuario").contentType(MediaType.APPLICATION_JSON).content(PAYLOAD))
            .andExpect(status().isOk()).andExpect(content().string("1"));
        verify(repository).save(argThat(u -> u.getId_usuario()==null && "Usuario".equals(u.getRol())
            && "encoded-hash".equals(u.getContrasena()) && "test@example.invalid".equals(u.getCorreo_electronico())));
        mvc.perform(post("/addUsuario").contentType(MediaType.APPLICATION_JSON).content(withId(99)))
            .andExpect(status().isBadRequest());
        mvc.perform(post("/addUsuario").contentType(MediaType.APPLICATION_JSON).content("{}"))
            .andExpect(status().isBadRequest());
    }

    @Test void invalidPasswordPolicyDoesNotPersist() throws Exception {
        when(passwords.encode("ClaveLarga123")).thenThrow(new IllegalArgumentException("Política de contraseña"));
        mvc.perform(post("/addUsuario").contentType(MediaType.APPLICATION_JSON).content(PAYLOAD))
            .andExpect(status().isBadRequest());
        verifyNoInteractions(repository);
    }

    @Test void loginRequiresHashMatchAndNeverExposesTheStoredPassword() throws Exception {
        when(repository.findByCorreo("test@example.invalid")).thenReturn(Optional.of(user()));
        when(passwords.matches("ClaveLarga123","stored-hash")).thenReturn(true,false);
        String login="{\"correo_electronico\":\"test@example.invalid\",\"contrasena\":\"ClaveLarga123\"}";
        mvc.perform(post("/getUsuarioLogin").contentType(MediaType.APPLICATION_JSON).content(login))
            .andExpect(status().isOk()).andExpect(jsonPath("$.id_usuario").value(7)).andExpect(jsonPath("$.contrasena").doesNotExist());
        mvc.perform(post("/getUsuarioLogin").contentType(MediaType.APPLICATION_JSON).content(login)).andExpect(status().isUnauthorized());
        when(repository.findByCorreo("test@example.invalid")).thenReturn(Optional.empty());
        mvc.perform(post("/getUsuarioLogin").contentType(MediaType.APPLICATION_JSON).content(login)).andExpect(status().isUnauthorized());
        mvc.perform(post("/getUsuarioLogin").contentType(MediaType.APPLICATION_JSON).content("{}"))
            .andExpect(status().isBadRequest());
    }

    @Test void queryAndListDoNotReturnPasswordHashes() throws Exception {
        when(repository.findById(7)).thenReturn(Optional.of(user()));
        when(repository.findById(8)).thenReturn(Optional.empty());
        when(repository.findAll()).thenReturn(List.of(user()),List.of());
        mvc.perform(get("/buscarUsuario").param("id","7")).andExpect(status().isOk())
            .andExpect(jsonPath("$.nombre").value("Usuario prueba")).andExpect(jsonPath("$.contrasena").doesNotExist());
        mvc.perform(get("/buscarUsuario").param("id","8")).andExpect(status().isNotFound());
        mvc.perform(get("/buscarUsuario").param("id","0")).andExpect(status().isBadRequest());
        mvc.perform(post("/buscarUsuarios")).andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(1)).andExpect(jsonPath("$[0].contrasena").doesNotExist());
        mvc.perform(post("/buscarUsuarios")).andExpect(status().isOk()).andExpect(content().json("[]"));
    }

    @Test void updatePreservesExistingRoleAndHashesTheReplacementPassword() throws Exception {
        Usuario previous=user(); previous.setRol("Administrador");
        when(repository.findById(7)).thenReturn(Optional.of(previous));
        when(repository.findById(8)).thenReturn(Optional.empty());
        when(passwords.encode("ClaveLarga123")).thenReturn("replacement-hash");
        mvc.perform(post("/updateUsuario").contentType(MediaType.APPLICATION_JSON).content(withId(7)))
            .andExpect(status().isOk()).andExpect(content().string("1"));
        verify(repository).save(argThat(u -> u.getId_usuario()==7 && "Administrador".equals(u.getRol())
            && "replacement-hash".equals(u.getContrasena())));
        mvc.perform(post("/updateUsuario").contentType(MediaType.APPLICATION_JSON).content(withId(8))).andExpect(status().isNotFound());
        mvc.perform(post("/updateUsuario").contentType(MediaType.APPLICATION_JSON).content(PAYLOAD)).andExpect(status().isBadRequest());
    }

    @Test void deleteRequiresPositiveExistingIdentifier() throws Exception {
        when(repository.existsById(7)).thenReturn(true);
        when(repository.existsById(8)).thenReturn(false);
        mvc.perform(delete("/deleteUsuario").contentType(MediaType.APPLICATION_JSON).content("{\"id_usuario\":7}"))
            .andExpect(status().isOk()).andExpect(content().string("1"));
        verify(repository).deleteById(7);
        mvc.perform(delete("/deleteUsuario").contentType(MediaType.APPLICATION_JSON).content("{\"id_usuario\":8}"))
            .andExpect(status().isNotFound());
        mvc.perform(delete("/deleteUsuario").contentType(MediaType.APPLICATION_JSON).content("{\"id_usuario\":-1}"))
            .andExpect(status().isBadRequest());
    }
}
