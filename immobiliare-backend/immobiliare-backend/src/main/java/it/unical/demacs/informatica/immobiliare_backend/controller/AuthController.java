package it.unical.demacs.informatica.immobiliare_backend.controller;

import it.unical.demacs.informatica.immobiliare_backend.config.PasswordUtil;
import it.unical.demacs.informatica.immobiliare_backend.dao.UtenteDao;
import it.unical.demacs.informatica.immobiliare_backend.model.Utente;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
public class AuthController {

    @Autowired
    private UtenteDao utenteDao;

    @Autowired
    private PasswordUtil passwordUtil;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Utente credenziali, HttpSession session) {
        try {
            Utente utente = utenteDao.findByEmail(credenziali.getEmail());
            if (utente == null) {
                return ResponseEntity.status(401).body("Credenziali errate");
            }
            // Controlla sia password in chiaro (vecchi utenti) che cifrata (nuovi)
            boolean passwordOk = passwordUtil.verifica(credenziali.getPassword(), utente.getPassword())
                    || utente.getPassword().equals(credenziali.getPassword());
            if (!passwordOk) {
                return ResponseEntity.status(401).body("Credenziali errate");
            }
            if (utente.isBannato()) {
                return ResponseEntity.status(403).body("Utente bannato");
            }
            session.setAttribute("utenteLoggato", utente);
            utente.setPassword(null);
            return ResponseEntity.ok(utente);
        } catch (SQLException e) {
            return ResponseEntity.status(500).body("Errore server");
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok("Logout effettuato");
    }

    @GetMapping("/me")
    public ResponseEntity<?> getUtenteLoggato(HttpSession session) {
        Utente utente = (Utente) session.getAttribute("utenteLoggato");
        if (utente == null) {
            return ResponseEntity.status(401).body("Non autenticato");
        }
        return ResponseEntity.ok(utente);
    }

    @PostMapping("/registra")
    public ResponseEntity<?> registra(@RequestBody Utente nuovo) {
        try {
            Utente esistente = utenteDao.findByEmail(nuovo.getEmail());
            if (esistente != null) {
                return ResponseEntity.status(400).body("Email già registrata");
            }
            nuovo.setRuolo("ACQUIRENTE");
            nuovo.setBannato(false);
            // Cifra la password prima di salvare
            nuovo.setPassword(passwordUtil.cifra(nuovo.getPassword()));
            Utente salvato = utenteDao.save(nuovo);
            salvato.setPassword(null);
            return ResponseEntity.ok(salvato);
        } catch (SQLException e) {
            return ResponseEntity.status(500).body("Errore server");
        }
    }
}