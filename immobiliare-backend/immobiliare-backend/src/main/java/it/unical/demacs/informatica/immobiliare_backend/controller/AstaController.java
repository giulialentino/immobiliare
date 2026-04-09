package it.unical.demacs.informatica.immobiliare_backend.controller;

import it.unical.demacs.informatica.immobiliare_backend.model.Asta;
import it.unical.demacs.informatica.immobiliare_backend.model.Utente;
import it.unical.demacs.informatica.immobiliare_backend.service.AstaService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.Map;

@RestController
@RequestMapping("/api/aste")
public class AstaController {

    @Autowired
    private AstaService astaService;

    @GetMapping("/annuncio/{idAnnuncio}")
    public ResponseEntity<?> getByAnnuncio(@PathVariable Long idAnnuncio) throws SQLException {
        Asta asta = astaService.getByAnnuncio(idAnnuncio);
        if (asta == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(asta);
    }

    @PostMapping
    public ResponseEntity<?> crea(@RequestBody Asta asta, HttpSession session) throws SQLException {
        Utente utente = (Utente) session.getAttribute("utenteLoggato");
        if (utente == null) return ResponseEntity.status(401).body("Non autenticato");
        if (!utente.getRuolo().equals("VENDITORE") && !utente.getRuolo().equals("AMMINISTRATORE")) {
            return ResponseEntity.status(403).body("Non autorizzato");
        }
        return ResponseEntity.ok(astaService.creaAsta(asta));
    }

    @PostMapping("/{idAsta}/offerta")
    public ResponseEntity<?> offerta(@PathVariable Long idAsta,
                                     @RequestBody Map<String, Double> body,
                                     HttpSession session) throws SQLException {
        Utente utente = (Utente) session.getAttribute("utenteLoggato");
        if (utente == null) return ResponseEntity.status(401).body("Non autenticato");
        astaService.faiOfferta(idAsta, body.get("importo"), utente);
        return ResponseEntity.ok("Offerta effettuata");
    }

    @GetMapping("/{idAsta}/offerte")
    public ResponseEntity<?> getOfferte(@PathVariable Long idAsta) throws SQLException {
        return ResponseEntity.ok(astaService.getOfferte(idAsta));
    }

    @PatchMapping("/{idAsta}/chiudi")
    public ResponseEntity<?> chiudi(@PathVariable Long idAsta, HttpSession session) throws SQLException {
        Utente utente = (Utente) session.getAttribute("utenteLoggato");
        if (utente == null) return ResponseEntity.status(401).body("Non autenticato");
        astaService.chiudiAsta(idAsta, utente);
        return ResponseEntity.ok("Asta chiusa");
    }
}
