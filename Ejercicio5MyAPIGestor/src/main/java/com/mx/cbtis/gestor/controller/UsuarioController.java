package com.mx.cbtis.gestor.controller;

import java.util.ArrayList;
import java.util.List;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import com.mx.cbtis.gestor.dto.*;
import com.mx.cbtis.gestor.modelo.Usuario;
import com.mx.cbtis.gestor.repository.UsuarioRepository;
import com.mx.cbtis.gestor.service.PasswordService;

@RestController
public class UsuarioController {
    private final UsuarioRepository usuariotable;
    private final PasswordService passwords;

    public UsuarioController(UsuarioRepository repository, PasswordService passwords) {
        this.usuariotable = repository;
        this.passwords = passwords;
    }

    @PostMapping("/getUsuarioLogin")
    public Usuario getUsuarioLogin(@Valid @RequestBody LoginRequest request) {
        Usuario user = usuariotable.findByCorreo(request.correo_electronico())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciales incorrectas."));
        if (!passwords.matches(request.contrasena(), user.getContrasena())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciales incorrectas.");
        }
        return user;
    }

    @GetMapping("/buscarUsuario")
    public Usuario getUsuario(@RequestParam int id) {
        requireId(id);
        return usuariotable.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado."));
    }

    @PostMapping("/addUsuario")
    public int addUsuario(@Valid @RequestBody UsuarioRequest request) {
        if (request.id_usuario() != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El alta no acepta un ID existente.");
        }
        Usuario user = request.toEntity();
        user.setContrasena(encode(request.contrasena()));
        usuariotable.save(user);
        return 1;
    }

    @PostMapping("/updateUsuario")
    public int updateUsuario(@Valid @RequestBody UsuarioRequest request) {
        requireId(request.id_usuario());
        Usuario previous = usuariotable.findById(request.id_usuario())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado."));
        Usuario user = request.toEntity();
        user.setRol(previous.getRol());
        user.setContrasena(encode(request.contrasena()));
        usuariotable.save(user);
        return 1;
    }

    @DeleteMapping("/deleteUsuario")
    public int deleteUsuario(@Valid @RequestBody UsuarioIdRequest request) {
        requireId(request.id_usuario());
        if (!usuariotable.existsById(request.id_usuario())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado.");
        }
        usuariotable.deleteById(request.id_usuario());
        return 1;
    }

    @PostMapping("/buscarUsuarios")
    public List<Usuario> getUsuarios() {
        List<Usuario> values = new ArrayList<>();
        usuariotable.findAll().forEach(values::add);
        return values;
    }

    private String encode(String password) {
        try {
            return passwords.encode(password);
        } catch (IllegalArgumentException invalidPassword) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, invalidPassword.getMessage());
        }
    }

    private void requireId(Integer id) {
        if (id == null || id <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El ID debe ser positivo.");
        }
    }
}
