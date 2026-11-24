package com.mx.cbtis.gestor.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

class PasswordServiceTests {
    @Test void hashesAreSaltedAndCorrectCredentialsMatch() {
        PasswordService service=new PasswordService(new BCryptPasswordEncoder(4));
        String first=service.encode("ClaveSegura123");
        String second=service.encode("ClaveSegura123");
        assertNotEquals("ClaveSegura123",first);
        assertNotEquals(first,second);
        assertTrue(service.matches("ClaveSegura123",first));
        assertFalse(service.matches("OtraClave123",first));
    }
    @Test void unsafeLengthsAndPlaintextLegacyValuesAreRejected() {
        PasswordService service=new PasswordService(new BCryptPasswordEncoder(4));
        assertThrows(IllegalArgumentException.class,()->service.encode(null));
        assertThrows(IllegalArgumentException.class,()->service.encode("short"));
        assertThrows(IllegalArgumentException.class,()->service.encode("a".repeat(73)));
        assertThrows(IllegalArgumentException.class,()->service.encode("é".repeat(37)));
        assertFalse(service.matches("ClaveSegura123","ClaveSegura123"));
        assertFalse(service.matches("ClaveSegura123",null));
        assertFalse(service.matches(null,"$2bad"));
        assertFalse(service.matches("a".repeat(73),"$2bad"));
        assertTrue(service.matches("a".repeat(72),service.encode("a".repeat(72))));
    }
    @Test void corruptHashDoesNotBecomeAnUncaughtServerError() {
        PasswordEncoder encoder=mock(PasswordEncoder.class);
        when(encoder.matches("ClaveSegura123","$2corrupt")).thenThrow(new IllegalArgumentException("bad hash"));
        assertFalse(new PasswordService(encoder).matches("ClaveSegura123","$2corrupt"));
    }
}
