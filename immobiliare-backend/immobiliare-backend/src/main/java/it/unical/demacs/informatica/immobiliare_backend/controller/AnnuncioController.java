package it.unical.demacs.informatica.immobiliare_backend.controller;

import it.unical.demacs.informatica.immobiliare_backend.model.Annuncio;
import it.unical.demacs.informatica.immobiliare_backend.model.AnnuncioProxy;
import it.unical.demacs.informatica.immobiliare_backend.model.Utente;
import it.unical.demacs.informatica.immobiliare_backend.service.AnnuncioService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.Map;

@RestController
@RequestMapping("/api/annunci")
public class AnnuncioController {

    @Autowired
    private AnnuncioService annuncioService;

    @GetMapping
    public ResponseEntity<?> getAll(
            @RequestParam(required = false) String tipoOperazione,
            @RequestParam(required = false) Long idCategoria) throws SQLException {
        return ResponseEntity.ok(annuncioService.getAnnunciFiltrati(tipoOperazione, idCategoria));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) throws SQLException {
        AnnuncioProxy proxy = annuncioService.getDettaglio(id);
        if (proxy == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(proxy);
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Annuncio annuncio, HttpSession session) throws SQLException {
        Utente utente = (Utente) session.getAttribute("utenteLoggato");
        if (utente == null) return ResponseEntity.status(401).body("Non autenticato");
        return ResponseEntity.ok(annuncioService.creaAnnuncio(annuncio, utente));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> modifica(@PathVariable Long id,
                                      @RequestBody Annuncio annuncio,
                                      HttpSession session) throws SQLException {
        Utente utente = (Utente) session.getAttribute("utenteLoggato");
        if (utente == null) return ResponseEntity.status(401).body("Non autenticato");
        Annuncio risultato = annuncioService.modificaAnnuncio(id, annuncio, utente);
        if (risultato == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(risultato);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> elimina(@PathVariable Long id, HttpSession session) throws SQLException {
        Utente utente = (Utente) session.getAttribute("utenteLoggato");
        if (utente == null) return ResponseEntity.status(401).body("Non autenticato");
        boolean eliminato = annuncioService.eliminaAnnuncio(id, utente);
        if (!eliminato) return ResponseEntity.notFound().build();
        return ResponseEntity.ok("Eliminato");
    }

    @PatchMapping("/{id}/ribassa")
    public ResponseEntity<?> ribassa(@PathVariable Long id,
                                     @RequestBody Map<String, Double> body,
                                     HttpSession session) throws SQLException {
        Utente utente = (Utente) session.getAttribute("utenteLoggato");
        if (utente == null) return ResponseEntity.status(401).body("Non autenticato");
        annuncioService.ribassaPrezzo(id, body.get("prezzo"), utente);
        return ResponseEntity.ok("Prezzo ribassato");
    }

    @PatchMapping("/{id}/annulla-ribasso")
    public ResponseEntity<?> annullaRibasso(@PathVariable Long id, HttpSession session) throws SQLException {
        Utente utente = (Utente) session.getAttribute("utenteLoggato");
        if (utente == null) return ResponseEntity.status(401).body("Non autenticato");
        annuncioService.annullaRibasso(id, utente);
        return ResponseEntity.ok("Ribasso annullato");
    }

    @GetMapping("/in-attesa")
    public ResponseEntity<?> getInAttesa(HttpSession session) throws SQLException {
        Utente utente = (Utente) session.getAttribute("utenteLoggato");
        if (utente == null || !utente.getRuolo().equals("AMMINISTRATORE"))
            return ResponseEntity.status(403).body("Non autorizzato");
        return ResponseEntity.ok(annuncioService.getInAttesa());
    }

    @PatchMapping("/{id}/stato")
    public ResponseEntity<?> aggiornaStato(@PathVariable Long id,
                                           @RequestBody Map<String, String> body,
                                           HttpSession session) throws SQLException {
        Utente utente = (Utente) session.getAttribute("utenteLoggato");
        if (utente == null || !utente.getRuolo().equals("AMMINISTRATORE"))
            return ResponseEntity.status(403).body("Non autorizzato");
        annuncioService.aggiornaStato(id, body.get("stato"), utente);
        return ResponseEntity.ok("Stato aggiornato");
    }

    @GetMapping("/count-in-attesa")
    public ResponseEntity<?> countInAttesa(HttpSession session) throws SQLException {
        Utente utente = (Utente) session.getAttribute("utenteLoggato");
        if (utente == null || !utente.getRuolo().equals("AMMINISTRATORE"))
            return ResponseEntity.status(403).body("Non autorizzato");
        return ResponseEntity.ok(annuncioService.countInAttesa());
    }

    @GetMapping("/venditore/{idVenditore}")
    public ResponseEntity<?> getByVenditore(@PathVariable Long idVenditore, HttpSession session) throws SQLException {
        Utente utente = (Utente) session.getAttribute("utenteLoggato");
        if (utente == null) return ResponseEntity.status(401).body("Non autenticato");
        return ResponseEntity.ok(annuncioService.getByVenditore(idVenditore));
    }
}
