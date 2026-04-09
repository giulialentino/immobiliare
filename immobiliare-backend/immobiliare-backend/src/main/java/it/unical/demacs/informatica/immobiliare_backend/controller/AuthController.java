package it.unical.demacs.informatica.immobiliare_backend.controller;

import it.unical.demacs.informatica.immobiliare_backend.model.Utente;
import it.unical.demacs.informatica.immobiliare_backend.service.AuthService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Utente credenziali, HttpSession session) throws SQLException {
        Utente utente = authService.login(credenziali.getEmail(), credenziali.getPassword());
        authService.aggiornaSessione(session, utente);
        return ResponseEntity.ok(utente);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok("Logout effettuato");
    }

    @GetMapping("/me")
    public ResponseEntity<?> getUtenteLoggato(HttpSession session) {
        Utente utente = (Utente) session.getAttribute("utenteLoggato");
        if (utente == null) return ResponseEntity.status(401).body("Non autenticato");
        return ResponseEntity.ok(utente);
    }

    @GetMapping("/utente/{id}")
    public ResponseEntity<?> getUtenteById(@PathVariable Long id) throws SQLException {
        Utente utente = authService.getUtenteById(id);
        if (utente == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(utente);
    }

    @PostMapping("/registra")
    public ResponseEntity<?> registra(@RequestBody Utente nuovo) throws SQLException {
        authService.registra(nuovo);
        return ResponseEntity.ok("Registrazione avvenuta! Controlla la tua email per verificare l'account.");
    }

    @GetMapping("/verifica-email")
    public ResponseEntity<?> verificaEmail(@RequestParam String token) throws SQLException {
        authService.verificaEmail(token);
        return ResponseEntity.ok("Email verificata con successo!");
    }

    @PostMapping("/recupera-password")
    public ResponseEntity<?> recuperaPassword(@RequestBody Map<String, String> body) throws SQLException {
        authService.recuperaPassword(body.get("email"));
        return ResponseEntity.ok("Se l'email è registrata, riceverai le istruzioni.");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> body) throws SQLException {
        authService.resetPassword(body.get("token"), body.get("nuovaPassword"));
        return ResponseEntity.ok("Password aggiornata con successo!");
    }

    @PostMapping("/cambia-password")
    public ResponseEntity<?> cambiaPassword(@RequestBody Map<String, String> body,
                                            HttpSession session) throws SQLException {
        Utente utente = (Utente) session.getAttribute("utenteLoggato");
        if (utente == null) return ResponseEntity.status(401).body("Non autenticato");
        authService.cambiaPassword(utente, body.get("vecchiaPassword"), body.get("nuovaPassword"));
        return ResponseEntity.ok("Password aggiornata");
    }

    @PostMapping("/foto-profilo")
    public ResponseEntity<?> uploadFotoProfilo(@RequestParam("file") MultipartFile file,
                                               HttpSession session) throws SQLException, IOException {
        Utente utente = (Utente) session.getAttribute("utenteLoggato");
        if (utente == null) return ResponseEntity.status(401).body("Non autenticato");
        String url = authService.uploadFotoProfilo(utente, file);
        authService.aggiornaSessione(session, utente);
        return ResponseEntity.ok(url);
    }

    @DeleteMapping("/foto-profilo")
    public ResponseEntity<?> rimuoviFotoProfilo(HttpSession session) throws SQLException {
        Utente utente = (Utente) session.getAttribute("utenteLoggato");
        if (utente == null) return ResponseEntity.status(401).body("Non autenticato");
        authService.rimuoviFotoProfilo(utente);
        authService.aggiornaSessione(session, utente);
        return ResponseEntity.ok("Foto rimossa");
    }
}
