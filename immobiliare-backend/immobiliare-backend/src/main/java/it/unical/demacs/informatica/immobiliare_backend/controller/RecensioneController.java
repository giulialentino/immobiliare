package it.unical.demacs.informatica.immobiliare_backend.controller;

import it.unical.demacs.informatica.immobiliare_backend.model.Recensione;
import it.unical.demacs.informatica.immobiliare_backend.model.Utente;
import it.unical.demacs.informatica.immobiliare_backend.service.RecensioneService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;

@RestController
@RequestMapping("/api/recensioni")
public class RecensioneController {

    @Autowired
    private RecensioneService recensioneService;

    @GetMapping("/annuncio/{idAnnuncio}")
    public ResponseEntity<?> getByAnnuncio(@PathVariable Long idAnnuncio) throws SQLException {
        return ResponseEntity.ok(recensioneService.getByAnnuncio(idAnnuncio));
    }

    @PostMapping
    public ResponseEntity<?> salva(@RequestBody Recensione recensione, HttpSession session) throws SQLException {
        Utente utente = (Utente) session.getAttribute("utenteLoggato");
        if (utente == null) return ResponseEntity.status(401).body("Non autenticato");
        return ResponseEntity.ok(recensioneService.salva(recensione, utente));
    }

    // Usato dal frontend per decidere se mostrare il form di recensione:
    // true solo se l'utente loggato ha già contattato il venditore per questo annuncio.
    @GetMapping("/puo-recensire/{idAnnuncio}")
    public ResponseEntity<?> puoRecensire(@PathVariable Long idAnnuncio, HttpSession session) throws SQLException {
        Utente utente = (Utente) session.getAttribute("utenteLoggato");
        if (utente == null || !utente.getRuolo().equals("ACQUIRENTE")) {
            return ResponseEntity.ok(false);
        }
        return ResponseEntity.ok(recensioneService.puoRecensire(idAnnuncio, utente));
    }
}