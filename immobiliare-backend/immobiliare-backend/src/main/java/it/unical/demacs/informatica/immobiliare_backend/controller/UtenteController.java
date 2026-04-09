package it.unical.demacs.informatica.immobiliare_backend.controller;

import it.unical.demacs.informatica.immobiliare_backend.model.Utente;
import it.unical.demacs.informatica.immobiliare_backend.service.UtenteService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;

@RestController
@RequestMapping("/api/utenti")
public class UtenteController {

    @Autowired
    private UtenteService utenteService;

    @GetMapping
    public ResponseEntity<?> getAll(HttpSession session) throws SQLException {
        Utente utente = (Utente) session.getAttribute("utenteLoggato");
        if (utente == null) return ResponseEntity.status(401).body("Non autenticato");
        if (!utente.getRuolo().equals("AMMINISTRATORE"))
            return ResponseEntity.status(403).body("Non autorizzato");
        return ResponseEntity.ok(utenteService.getAll());
    }

    @PatchMapping("/{id}/banna")
    public ResponseEntity<?> banna(@PathVariable Long id, HttpSession session) throws SQLException {
        Utente utente = (Utente) session.getAttribute("utenteLoggato");
        if (utente == null) return ResponseEntity.status(401).body("Non autenticato");
        if (!utente.getRuolo().equals("AMMINISTRATORE"))
            return ResponseEntity.status(403).body("Non autorizzato");
        utenteService.banna(id);
        return ResponseEntity.ok("Utente bannato");
    }

    @PatchMapping("/{id}/promuovi")
    public ResponseEntity<?> promuovi(@PathVariable Long id, HttpSession session) throws SQLException {
        Utente utente = (Utente) session.getAttribute("utenteLoggato");
        if (utente == null) return ResponseEntity.status(401).body("Non autenticato");
        if (!utente.getRuolo().equals("AMMINISTRATORE"))
            return ResponseEntity.status(403).body("Non autorizzato");
        utenteService.promuoviAdAdmin(id);
        return ResponseEntity.ok("Utente promosso ad amministratore");
    }
}
