package com.mx.cbtis.gestor.service;

import java.nio.charset.StandardCharsets;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class PasswordService {
    private final PasswordEncoder encoder;

    public PasswordService() {
        this(new BCryptPasswordEncoder(12));
    }

    PasswordService(PasswordEncoder encoder) {
        this.encoder = encoder;
    }

    public String encode(String password) {
        if (!validLength(password)) {
            throw new IllegalArgumentException("La contraseña debe tener entre 8 y 72 bytes UTF-8.");
        }
        return encoder.encode(password);
    }

    public boolean matches(String password, String storedHash) {
        if (!validLength(password) || storedHash == null || !storedHash.startsWith("$2")) {
            return false;
        }
        try {
            return encoder.matches(password, storedHash);
        } catch (IllegalArgumentException malformedHash) {
            return false;
        }
    }

    private boolean validLength(String password) {
        if (password == null) {
            return false;
        }
        int bytes = password.getBytes(StandardCharsets.UTF_8).length;
        return bytes >= 8 && bytes <= 72;
    }
}
