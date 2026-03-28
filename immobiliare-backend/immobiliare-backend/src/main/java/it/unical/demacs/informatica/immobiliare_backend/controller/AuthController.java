package it.unical.demacs.informatica.immobiliare_backend.controller;

import it.unical.demacs.informatica.immobiliare_backend.config.PasswordUtil;
import it.unical.demacs.informatica.immobiliare_backend.dao.UtenteDao;
import it.unical.demacs.informatica.immobiliare_backend.model.Utente;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
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
            if (utente.isBannato()) {
                return ResponseEntity.status(403).body("Utente bannato");
            }
            boolean passwordOk = passwordUtil.verifica(credenziali.getPassword(), utente.getPassword())
                    || utente.getPassword().equals(credenziali.getPassword());
            if (!passwordOk) {
                return ResponseEntity.status(401).body("Credenziali errate");
            }
            // Aggiorna password con costo 4 se era in chiaro
            if (utente.getPassword().equals(credenziali.getPassword())) {
                utenteDao.aggiornaPassword(utente.getId(), passwordUtil.cifra(credenziali.getPassword()));
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
    @PostMapping("/cifra-password")
    public ResponseEntity<?> cifraPassword() {
        try {
            List<Utente> utenti = utenteDao.findAll();
            for (Utente u : utenti) {
                if (!u.getPassword().startsWith("$2a$")) {
                    utenteDao.aggiornaPassword(u.getId(), passwordUtil.cifra(u.getPassword()));
                }
            }
            return ResponseEntity.ok("Password cifrate");
        } catch (SQLException e) {
            return ResponseEntity.status(500).body("Errore: " + e.getMessage());
        }
    }
    @GetMapping("/cifra-password")
    public ResponseEntity<?> cifraPasswordGet() {
        try {
            List<Utente> utenti = utenteDao.findAll();
            for (Utente u : utenti) {
                if (!u.getPassword().startsWith("$2a$")) {
                    utenteDao.aggiornaPassword(u.getId(), passwordUtil.cifra(u.getPassword()));
                }
            }
            return ResponseEntity.ok("Password cifrate: " + utenti.size());
        } catch (SQLException e) {
            return ResponseEntity.status(500).body("Errore: " + e.getMessage());
        }
    }

    @PostMapping("/registra")
    public ResponseEntity<?> registra(@RequestBody Utente nuovo) {
        try {
            long t1 = System.currentTimeMillis();
            Utente esistente = utenteDao.findByEmail(nuovo.getEmail());
            long t2 = System.currentTimeMillis();
            System.out.println("findByEmail: " + (t2-t1) + "ms");
            if (esistente != null) {
                return ResponseEntity.status(400).body("Email già registrata");
            }
            long t3 = System.currentTimeMillis();
            nuovo.setPassword(passwordUtil.cifra(nuovo.getPassword()));
            long t4 = System.currentTimeMillis();
            System.out.println("BCrypt: " + (t4-t3) + "ms");
            nuovo.setRuolo("ACQUIRENTE");
            nuovo.setBannato(false);
            Utente salvato = utenteDao.save(nuovo);
            salvato.setPassword(null);
            return ResponseEntity.ok(salvato);
        } catch (SQLException e) {
            return ResponseEntity.status(500).body("Errore server");
        }
    }

}