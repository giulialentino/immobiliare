package it.unical.demacs.informatica.immobiliare_backend.controller;

import it.unical.demacs.informatica.immobiliare_backend.model.Segnalazione;
import it.unical.demacs.informatica.immobiliare_backend.model.Utente;
import it.unical.demacs.informatica.immobiliare_backend.service.SegnalazioneService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;

@RestController
@RequestMapping("/api/segnalazioni")
public class SegnalazioneController {

    @Autowired
    private SegnalazioneService segnalazioneService;

    @GetMapping("/check/{idAnnuncio}")
    public ResponseEntity<?> check(@PathVariable Long idAnnuncio, HttpSession session) {
        Utente utente = (Utente) session.getAttribute("utenteLoggato");
        if (utente == null) return ResponseEntity.ok("false");
        try {
            boolean esiste = segnalazioneService.checkSegnalazione(idAnnuncio, utente.getId());
            return ResponseEntity.ok(String.valueOf(esiste));
        } catch (SQLException e) {
            return ResponseEntity.ok("false");
        }
    }

    @PostMapping
    public ResponseEntity<?> segnala(@RequestBody Segnalazione segnalazione,
                                     HttpSession session) throws SQLException {
        Utente utente = (Utente) session.getAttribute("utenteLoggato");
        if (utente == null) return ResponseEntity.status(401).body("Non autenticato");
        segnalazioneService.segnala(segnalazione, utente);
        return ResponseEntity.ok("Segnalazione inviata");
    }

    @GetMapping
    public ResponseEntity<?> getAll(HttpSession session) throws SQLException {
        Utente utente = (Utente) session.getAttribute("utenteLoggato");
        if (utente == null || !utente.getRuolo().equals("AMMINISTRATORE"))
            return ResponseEntity.status(403).body("Non autorizzato");
        return ResponseEntity.ok(segnalazioneService.getAll());
    }

    @PatchMapping("/{id}/gestita")
    public ResponseEntity<?> segnaGestita(@PathVariable Long id, HttpSession session) throws SQLException {
        Utente utente = (Utente) session.getAttribute("utenteLoggato");
        if (utente == null || !utente.getRuolo().equals("AMMINISTRATORE"))
            return ResponseEntity.status(403).body("Non autorizzato");
        segnalazioneService.segnaGestita(id);
        return ResponseEntity.ok("Gestita");
    }
}
