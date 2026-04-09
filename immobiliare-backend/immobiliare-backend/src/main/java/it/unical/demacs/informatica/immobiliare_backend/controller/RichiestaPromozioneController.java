package it.unical.demacs.informatica.immobiliare_backend.controller;

import it.unical.demacs.informatica.immobiliare_backend.model.Utente;
import it.unical.demacs.informatica.immobiliare_backend.service.RichiestaPromozioneService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;

@RestController
@RequestMapping("/api/promozione")
public class RichiestaPromozioneController {

    @Autowired
    private RichiestaPromozioneService richiestaService;

    @PostMapping("/richiedi")
    public ResponseEntity<?> richiedi(HttpSession session) throws SQLException {
        Utente utente = (Utente) session.getAttribute("utenteLoggato");
        if (utente == null) return ResponseEntity.status(401).body("Non autenticato");
        richiestaService.richiedi(utente);
        return ResponseEntity.ok("Richiesta inviata");
    }

    @GetMapping("/stato")
    public ResponseEntity<?> getStato(HttpSession session) throws SQLException {
        Utente utente = (Utente) session.getAttribute("utenteLoggato");
        if (utente == null) return ResponseEntity.status(401).body("Non autenticato");
        return ResponseEntity.ok(richiestaService.getStato(utente.getId()));
    }

    @PostMapping("/approva/{idUtente}")
    public ResponseEntity<?> approva(@PathVariable Long idUtente, HttpSession session) throws SQLException {
        Utente admin = (Utente) session.getAttribute("utenteLoggato");
        if (admin == null || !admin.getRuolo().equals("AMMINISTRATORE"))
            return ResponseEntity.status(403).body("Non autorizzato");
        richiestaService.approva(idUtente, admin);
        return ResponseEntity.ok("APPROVATO");
    }

    @PostMapping("/rifiuta/{idUtente}")
    public ResponseEntity<?> rifiuta(@PathVariable Long idUtente, HttpSession session) throws SQLException {
        Utente admin = (Utente) session.getAttribute("utenteLoggato");
        if (admin == null || !admin.getRuolo().equals("AMMINISTRATORE"))
            return ResponseEntity.status(403).body("Non autorizzato");
        richiestaService.rifiuta(idUtente, admin);
        return ResponseEntity.ok("RIFIUTATO");
    }
}
