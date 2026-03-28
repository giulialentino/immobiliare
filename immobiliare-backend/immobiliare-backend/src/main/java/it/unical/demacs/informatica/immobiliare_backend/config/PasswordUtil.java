package it.unical.demacs.informatica.immobiliare_backend.config;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class PasswordUtil {

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(4);

    public String cifra(String password) {
        return encoder.encode(password);
    }

    public boolean verifica(String password, String hash) {
        return encoder.matches(password, hash);
    }
}