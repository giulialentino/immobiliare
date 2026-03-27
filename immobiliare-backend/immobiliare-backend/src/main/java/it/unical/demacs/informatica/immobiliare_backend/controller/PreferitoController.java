package it.unical.demacs.informatica.immobiliare_backend.controller;

import it.unical.demacs.informatica.immobiliare_backend.dao.AnnuncioDao;
import it.unical.demacs.informatica.immobiliare_backend.dao.PreferitoDao;
import it.unical.demacs.informatica.immobiliare_backend.model.Annuncio;
import it.unical.demacs.informatica.immobiliare_backend.model.Utente;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/preferiti")
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
public class PreferitoController {

    @Autowired
    private PreferitoDao preferitoDao;

    @Autowired
    private AnnuncioDao annuncioDao;

    @GetMapping
    public ResponseEntity<?> getPreferiti(HttpSession session) {
        Utente utente = (Utente) session.getAttribute("utenteLoggato");
        if (utente == null) return ResponseEntity.status(401).body("Non autenticato");
        try {
            List<Long> ids = preferitoDao.findIdAnnunciByUtente(utente.getId());
            List<Annuncio> annunci = new ArrayList<>();
            for (Long id : ids) {
                Annuncio a = annuncioDao.findById(id);
                if (a != null) annunci.add(a);
            }
            return ResponseEntity.ok(annunci);
        } catch (SQLException e) {
            return ResponseEntity.status(500).body("Errore server");
        }
    }

    @GetMapping("/{idAnnuncio}/check")
    public ResponseEntity<?> isPreferito(@PathVariable Long idAnnuncio, HttpSession session) {
        Utente utente = (Utente) session.getAttribute("utenteLoggato");
        if (utente == null) return ResponseEntity.ok(false);
        try {
            return ResponseEntity.ok(preferitoDao.isPreferito(utente.getId(), idAnnuncio));
        } catch (SQLException e) {
            return ResponseEntity.status(500).body("Errore server");
        }
    }

    @PostMapping("/{idAnnuncio}")
    public ResponseEntity<?> aggiungi(@PathVariable Long idAnnuncio, HttpSession session) {
        Utente utente = (Utente) session.getAttribute("utenteLoggato");
        if (utente == null) return ResponseEntity.status(401).body("Non autenticato");
        try {
            preferitoDao.aggiungi(utente.getId(), idAnnuncio);
            return ResponseEntity.ok("Aggiunto ai preferiti");
        } catch (SQLException e) {
            return ResponseEntity.status(500).body("Errore server");
        }
    }

    @DeleteMapping("/{idAnnuncio}")
    public ResponseEntity<?> rimuovi(@PathVariable Long idAnnuncio, HttpSession session) {
        Utente utente = (Utente) session.getAttribute("utenteLoggato");
        if (utente == null) return ResponseEntity.status(401).body("Non autenticato");
        try {
            preferitoDao.rimuovi(utente.getId(), idAnnuncio);
            return ResponseEntity.ok("Rimosso dai preferiti");
        } catch (SQLException e) {
            return ResponseEntity.status(500).body("Errore server");
        }
    }
}