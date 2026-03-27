package it.unical.demacs.informatica.immobiliare_backend.controller;

import it.unical.demacs.informatica.immobiliare_backend.dao.RecensioneDao;
import it.unical.demacs.informatica.immobiliare_backend.model.Recensione;
import it.unical.demacs.informatica.immobiliare_backend.model.Utente;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.List;

@RestController
@RequestMapping("/api/recensioni")
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
public class RecensioneController {

    @Autowired
    private RecensioneDao recensioneDao;

    @GetMapping("/annuncio/{idAnnuncio}")
    public ResponseEntity<?> getByAnnuncio(@PathVariable Long idAnnuncio) {
        try {
            List<Recensione> lista = recensioneDao.findByAnnuncio(idAnnuncio);
            return ResponseEntity.ok(lista);
        } catch (SQLException e) {
            return ResponseEntity.status(500).body("Errore server");
        }
    }

    // Solo ACQUIRENTE può scrivere recensioni
    @PostMapping
    public ResponseEntity<?> salva(@RequestBody Recensione recensione, HttpSession session) {
        Utente utente = (Utente) session.getAttribute("utenteLoggato");
        if (utente == null) return ResponseEntity.status(401).body("Non autenticato");
        if (!utente.getRuolo().equals("ACQUIRENTE")) {
            return ResponseEntity.status(403).body("Solo gli acquirenti possono recensire");
        }
        try {
            recensione.setIdUtente(utente.getId());
            Recensione salvata = recensioneDao.save(recensione);
            return ResponseEntity.ok(salvata);
        } catch (SQLException e) {
            return ResponseEntity.status(500).body("Errore server");
        }
    }
}