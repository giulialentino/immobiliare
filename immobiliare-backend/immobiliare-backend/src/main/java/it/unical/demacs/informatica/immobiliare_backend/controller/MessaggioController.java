package it.unical.demacs.informatica.immobiliare_backend.controller;

import it.unical.demacs.informatica.immobiliare_backend.model.Messaggio;
import it.unical.demacs.informatica.immobiliare_backend.model.Utente;
import it.unical.demacs.informatica.immobiliare_backend.service.MessaggioService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;

@RestController
@RequestMapping("/api/messaggi")
public class MessaggioController {

    @Autowired
    private MessaggioService messaggioService;

    @GetMapping("/venditore/{idVenditore}")
    public ResponseEntity<?> getByVenditore(@PathVariable Long idVenditore, HttpSession session) throws SQLException {
        Utente utente = (Utente) session.getAttribute("utenteLoggato");
        if (utente == null) return ResponseEntity.status(401).body("Non autenticato");
        return ResponseEntity.ok(messaggioService.getByVenditore(idVenditore));
    }

    @GetMapping("/inviati")
    public ResponseEntity<?> getInviati(HttpSession session) throws SQLException {
        Utente utente = (Utente) session.getAttribute("utenteLoggato");
        if (utente == null) return ResponseEntity.status(401).body("Non autenticato");
        return ResponseEntity.ok(messaggioService.getInviati(utente.getId()));
    }

    @GetMapping("/annuncio/{idAnnuncio}")
    public ResponseEntity<?> getByAnnuncio(@PathVariable Long idAnnuncio, HttpSession session) throws SQLException {
        Utente utente = (Utente) session.getAttribute("utenteLoggato");
        if (utente == null) return ResponseEntity.status(401).body("Non autenticato");
        return ResponseEntity.ok(messaggioService.getByAnnuncio(idAnnuncio));
    }

    @GetMapping("/count/{idVenditore}")
    public ResponseEntity<?> count(@PathVariable Long idVenditore, HttpSession session) throws SQLException {
        Utente utente = (Utente) session.getAttribute("utenteLoggato");
        if (utente == null) return ResponseEntity.status(401).body("Non autenticato");
        return ResponseEntity.ok(messaggioService.countMessaggi(idVenditore));
    }

    @PostMapping
    public ResponseEntity<?> invia(@RequestBody Messaggio messaggio, HttpSession session) throws SQLException {
        Utente utente = (Utente) session.getAttribute("utenteLoggato");
        if (utente == null) return ResponseEntity.status(401).body("Non autenticato");
        return ResponseEntity.ok(messaggioService.invia(messaggio, utente));
    }

    @PatchMapping("/{id}/letto")
    public ResponseEntity<?> segnaLetto(@PathVariable Long id, HttpSession session) throws SQLException {
        Utente utente = (Utente) session.getAttribute("utenteLoggato");
        if (utente == null) return ResponseEntity.status(401).body("Non autenticato");
        messaggioService.segnaComeLetto(id);
        return ResponseEntity.ok("Letto");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> elimina(@PathVariable Long id, HttpSession session) throws SQLException {
        Utente utente = (Utente) session.getAttribute("utenteLoggato");
        if (utente == null) return ResponseEntity.status(401).body("Non autenticato");
        messaggioService.elimina(id, utente);
        return ResponseEntity.ok("Eliminato");
    }

    @DeleteMapping("/tutti/{idVenditore}")
    public ResponseEntity<?> eliminaTutti(@PathVariable Long idVenditore, HttpSession session) throws SQLException {
        Utente utente = (Utente) session.getAttribute("utenteLoggato");
        if (utente == null) return ResponseEntity.status(401).body("Non autenticato");
        messaggioService.eliminaTuttiPerVenditore(idVenditore);
        return ResponseEntity.ok("Eliminati");
    }

    @DeleteMapping("/miei")
    public ResponseEntity<?> eliminaMiei(HttpSession session) throws SQLException {
        Utente utente = (Utente) session.getAttribute("utenteLoggato");
        if (utente == null) return ResponseEntity.status(401).body("Non autenticato");
        messaggioService.eliminaTuttiPerAcquirente(utente.getId());
        return ResponseEntity.ok("Eliminati");
    }

    @GetMapping("/admin")
    public ResponseEntity<?> getMessaggiAdmin(HttpSession session) throws SQLException {
        Utente utente = (Utente) session.getAttribute("utenteLoggato");
        if (utente == null || !utente.getRuolo().equals("AMMINISTRATORE"))
            return ResponseEntity.status(403).body("Non autorizzato");
        return ResponseEntity.ok(messaggioService.getMessaggiAdmin());
    }

    @GetMapping("/count-admin")
    public ResponseEntity<?> countAdmin(HttpSession session) throws SQLException {
        Utente utente = (Utente) session.getAttribute("utenteLoggato");
        if (utente == null || !utente.getRuolo().equals("AMMINISTRATORE"))
            return ResponseEntity.status(403).body("Non autorizzato");
        return ResponseEntity.ok(messaggioService.countNonLettiAdmin());
    }
}
