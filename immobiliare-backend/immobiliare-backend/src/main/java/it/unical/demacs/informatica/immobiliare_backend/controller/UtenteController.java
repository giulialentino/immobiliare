package it.unical.demacs.informatica.immobiliare_backend.controller;

import it.unical.demacs.informatica.immobiliare_backend.dao.UtenteDao;
import it.unical.demacs.informatica.immobiliare_backend.model.Utente;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.List;

@RestController
@RequestMapping("/api/utenti")
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
public class UtenteController {

    @Autowired
    private UtenteDao utenteDao;

    // Solo AMMINISTRATORE può vedere tutti gli utenti
    @GetMapping
    public ResponseEntity<?> getAll(HttpSession session) {
        Utente utente = (Utente) session.getAttribute("utenteLoggato");
        if (utente == null) return ResponseEntity.status(401).body("Non autenticato");
        if (!utente.getRuolo().equals("AMMINISTRATORE")) {
            return ResponseEntity.status(403).body("Non autorizzato");
        }
        try {
            List<Utente> lista = utenteDao.findAll();
            lista.forEach(u -> u.setPassword(null));
            return ResponseEntity.ok(lista);
        } catch (SQLException e) {
            return ResponseEntity.status(500).body("Errore server");
        }
    }

    // Banna un utente — solo AMMINISTRATORE
    @PatchMapping("/{id}/banna")
    public ResponseEntity<?> banna(@PathVariable Long id, HttpSession session) {
        Utente utente = (Utente) session.getAttribute("utenteLoggato");
        if (utente == null) return ResponseEntity.status(401).body("Non autenticato");
        if (!utente.getRuolo().equals("AMMINISTRATORE")) {
            return ResponseEntity.status(403).body("Non autorizzato");
        }
        try {
            utenteDao.setBannato(id, true);
            return ResponseEntity.ok("Utente bannato");
        } catch (SQLException e) {
            return ResponseEntity.status(500).body("Errore server");
        }
    }

    // Promuovi ad amministratore — solo AMMINISTRATORE
    @PatchMapping("/{id}/promuovi")
    public ResponseEntity<?> promuovi(@PathVariable Long id, HttpSession session) {
        Utente utente = (Utente) session.getAttribute("utenteLoggato");
        if (utente == null) return ResponseEntity.status(401).body("Non autenticato");
        if (!utente.getRuolo().equals("AMMINISTRATORE")) {
            return ResponseEntity.status(403).body("Non autorizzato");
        }
        try {
            utenteDao.setRuolo(id, "AMMINISTRATORE");
            return ResponseEntity.ok("Utente promosso ad amministratore");
        } catch (SQLException e) {
            return ResponseEntity.status(500).body("Errore server");
        }
    }
}