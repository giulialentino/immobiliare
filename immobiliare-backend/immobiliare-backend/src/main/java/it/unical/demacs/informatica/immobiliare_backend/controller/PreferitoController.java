package it.unical.demacs.informatica.immobiliare_backend.controller;

import it.unical.demacs.informatica.immobiliare_backend.model.Utente;
import it.unical.demacs.informatica.immobiliare_backend.service.PreferitoService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;

@RestController
@RequestMapping("/api/preferiti")
public class PreferitoController {

    @Autowired
    private PreferitoService preferitoService;

    @GetMapping
    public ResponseEntity<?> getPreferiti(HttpSession session) throws SQLException {
        Utente utente = (Utente) session.getAttribute("utenteLoggato");
        if (utente == null) return ResponseEntity.status(401).body("Non autenticato");
        return ResponseEntity.ok(preferitoService.getPreferiti(utente.getId()));
    }

    @GetMapping("/{idAnnuncio}/check")
    public ResponseEntity<?> isPreferito(@PathVariable Long idAnnuncio, HttpSession session) throws SQLException {
        Utente utente = (Utente) session.getAttribute("utenteLoggato");
        if (utente == null) return ResponseEntity.ok(false);
        return ResponseEntity.ok(preferitoService.isPreferito(utente.getId(), idAnnuncio));
    }

    @PostMapping("/{idAnnuncio}")
    public ResponseEntity<?> aggiungi(@PathVariable Long idAnnuncio, HttpSession session) throws SQLException {
        Utente utente = (Utente) session.getAttribute("utenteLoggato");
        if (utente == null) return ResponseEntity.status(401).body("Non autenticato");
        preferitoService.aggiungi(utente.getId(), idAnnuncio);
        return ResponseEntity.ok("Aggiunto ai preferiti");
    }

    @DeleteMapping("/{idAnnuncio}")
    public ResponseEntity<?> rimuovi(@PathVariable Long idAnnuncio, HttpSession session) throws SQLException {
        Utente utente = (Utente) session.getAttribute("utenteLoggato");
        if (utente == null) return ResponseEntity.status(401).body("Non autenticato");
        preferitoService.rimuovi(utente.getId(), idAnnuncio);
        return ResponseEntity.ok("Rimosso dai preferiti");
    }
}
