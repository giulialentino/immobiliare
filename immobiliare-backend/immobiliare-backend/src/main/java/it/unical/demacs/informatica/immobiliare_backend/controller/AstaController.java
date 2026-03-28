package it.unical.demacs.informatica.immobiliare_backend.controller;

import it.unical.demacs.informatica.immobiliare_backend.dao.AnnuncioDao;
import it.unical.demacs.informatica.immobiliare_backend.dao.AstaDao;
import it.unical.demacs.informatica.immobiliare_backend.dao.OffertaDao;
import it.unical.demacs.informatica.immobiliare_backend.model.Asta;
import it.unical.demacs.informatica.immobiliare_backend.model.Offerta;
import it.unical.demacs.informatica.immobiliare_backend.model.Utente;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/aste")
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
public class AstaController {

    @Autowired
    private AstaDao astaDao;

    @Autowired
    private OffertaDao offertaDao;

    @Autowired
    private AnnuncioDao annuncioDao;

    @GetMapping("/annuncio/{idAnnuncio}")
    public ResponseEntity<?> getByAnnuncio(@PathVariable Long idAnnuncio) {
        try {
            Asta asta = astaDao.findByAnnuncio(idAnnuncio);
            if (asta == null) return ResponseEntity.notFound().build();
            return ResponseEntity.ok(asta);
        } catch (SQLException e) {
            return ResponseEntity.status(500).body("Errore server");
        }
    }

    @PostMapping
    public ResponseEntity<?> crea(@RequestBody Asta asta, HttpSession session) {
        Utente utente = (Utente) session.getAttribute("utenteLoggato");
        if (utente == null) return ResponseEntity.status(401).body("Non autenticato");
        if (!utente.getRuolo().equals("VENDITORE") && !utente.getRuolo().equals("AMMINISTRATORE")) {
            return ResponseEntity.status(403).body("Non autorizzato");
        }
        try {
            Asta salvata = astaDao.save(asta);
            // Aggiorna in_asta = true sull'annuncio
            annuncioDao.aggiornaInAsta(asta.getIdAnnuncio(), true);
            return ResponseEntity.ok(salvata);
        } catch (SQLException e) {
            return ResponseEntity.status(500).body("Errore server");
        }
    }

    @PostMapping("/{idAsta}/offerta")
    public ResponseEntity<?> offerta(@PathVariable Long idAsta,
                                     @RequestBody Map<String, Double> body,
                                     HttpSession session) {
        Utente utente = (Utente) session.getAttribute("utenteLoggato");
        if (utente == null) return ResponseEntity.status(401).body("Non autenticato");
        try {
            Double importo = body.get("importo");
            if (importo == null) return ResponseEntity.badRequest().body("Importo mancante");
            Offerta offerta = new Offerta();
            offerta.setIdAsta(idAsta);
            offerta.setIdUtente(utente.getId());
            offerta.setImporto(importo);
            offertaDao.save(offerta);
            astaDao.aggiornaOfferta(idAsta, importo, utente.getId());
            return ResponseEntity.ok("Offerta effettuata");
        } catch (SQLException e) {
            return ResponseEntity.status(500).body("Errore server");
        }
    }

    @GetMapping("/{idAsta}/offerte")
    public ResponseEntity<?> getOfferte(@PathVariable Long idAsta) {
        try {
            List<Offerta> offerte = offertaDao.findByAsta(idAsta);
            return ResponseEntity.ok(offerte);
        } catch (SQLException e) {
            return ResponseEntity.status(500).body("Errore server");
        }
    }

    @PatchMapping("/{idAsta}/chiudi")
    public ResponseEntity<?> chiudi(@PathVariable Long idAsta, HttpSession session) {
        Utente utente = (Utente) session.getAttribute("utenteLoggato");
        if (utente == null) return ResponseEntity.status(401).body("Non autenticato");
        try {
            // Trova l'idAnnuncio prima di chiudere
            Asta asta = astaDao.findById(idAsta);
            astaDao.chiudi(idAsta);
            // Aggiorna in_asta = false sull'annuncio
            if (asta != null) {
                annuncioDao.aggiornaInAsta(asta.getIdAnnuncio(), false);
            }
            return ResponseEntity.ok("Asta chiusa");
        } catch (SQLException e) {
            return ResponseEntity.status(500).body("Errore server");
        }
    }
}