package it.unical.demacs.informatica.immobiliare_backend.controller;

import it.unical.demacs.informatica.immobiliare_backend.dao.MessaggioDao;
import it.unical.demacs.informatica.immobiliare_backend.model.Messaggio;
import it.unical.demacs.informatica.immobiliare_backend.model.Utente;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.List;

@RestController
@RequestMapping("/api/messaggi")
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
public class MessaggioController {

    @Autowired
    private MessaggioDao messaggioDao;

    @GetMapping("/venditore/{idVenditore}")
    public ResponseEntity<?> getByVenditore(@PathVariable Long idVenditore, HttpSession session) {
        Utente utente = (Utente) session.getAttribute("utenteLoggato");
        if (utente == null) return ResponseEntity.status(401).body("Non autenticato");
        try {
            return ResponseEntity.ok(messaggioDao.findByVenditore(idVenditore));
        } catch (SQLException e) {
            return ResponseEntity.status(500).body("Errore server");
        }
    }

    @GetMapping("/inviati")
    public ResponseEntity<?> getInviati(HttpSession session) {
        Utente utente = (Utente) session.getAttribute("utenteLoggato");
        if (utente == null) return ResponseEntity.status(401).body("Non autenticato");
        try {
            return ResponseEntity.ok(messaggioDao.findByMittente(utente.getId()));
        } catch (SQLException e) {
            return ResponseEntity.status(500).body("Errore server");
        }
    }

    @GetMapping("/annuncio/{idAnnuncio}")
    public ResponseEntity<?> getByAnnuncio(@PathVariable Long idAnnuncio, HttpSession session) {
        Utente utente = (Utente) session.getAttribute("utenteLoggato");
        if (utente == null) return ResponseEntity.status(401).body("Non autenticato");
        try {
            return ResponseEntity.ok(messaggioDao.findByAnnuncio(idAnnuncio));
        } catch (SQLException e) {
            return ResponseEntity.status(500).body("Errore server");
        }
    }

    @GetMapping("/count/{idVenditore}")
    public ResponseEntity<?> count(@PathVariable Long idVenditore, HttpSession session) {
        Utente utente = (Utente) session.getAttribute("utenteLoggato");
        if (utente == null) return ResponseEntity.status(401).body("Non autenticato");
        try {
            return ResponseEntity.ok(messaggioDao.countMessaggi(idVenditore));
        } catch (SQLException e) {
            return ResponseEntity.status(500).body("Errore server");
        }
    }

    @PostMapping
    public ResponseEntity<?> invia(@RequestBody Messaggio messaggio, HttpSession session) {
        Utente utente = (Utente) session.getAttribute("utenteLoggato");
        if (utente == null) return ResponseEntity.status(401).body("Non autenticato");
        try {
            messaggio.setIdMittente(utente.getId());
            return ResponseEntity.ok(messaggioDao.save(messaggio));
        } catch (SQLException e) {
            return ResponseEntity.status(500).body("Errore server");
        }
    }

    @PatchMapping("/{id}/letto")
    public ResponseEntity<?> segnaLetto(@PathVariable Long id, HttpSession session) {
        Utente utente = (Utente) session.getAttribute("utenteLoggato");
        if (utente == null) return ResponseEntity.status(401).body("Non autenticato");
        try {
            messaggioDao.segnaComeLetto(id);
            return ResponseEntity.ok("Letto");
        } catch (SQLException e) {
            return ResponseEntity.status(500).body("Errore server");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> elimina(@PathVariable Long id, HttpSession session) {
        Utente utente = (Utente) session.getAttribute("utenteLoggato");
        if (utente == null) return ResponseEntity.status(401).body("Non autenticato");
        try {
            if (utente.getRuolo().equals("ACQUIRENTE")) {
                messaggioDao.eliminaPerAcquirente(id);
            } else {
                messaggioDao.eliminaPerVenditore(id);
            }
            return ResponseEntity.ok("Eliminato");
        } catch (SQLException e) {
            return ResponseEntity.status(500).body("Errore server");
        }
    }

    @DeleteMapping("/tutti/{idVenditore}")
    public ResponseEntity<?> eliminaTutti(@PathVariable Long idVenditore, HttpSession session) {
        Utente utente = (Utente) session.getAttribute("utenteLoggato");
        if (utente == null) return ResponseEntity.status(401).body("Non autenticato");
        try {
            messaggioDao.eliminaTuttiPerVenditore(idVenditore);
            return ResponseEntity.ok("Eliminati");
        } catch (SQLException e) {
            return ResponseEntity.status(500).body("Errore server");
        }
    }

    @DeleteMapping("/miei")
    public ResponseEntity<?> eliminaMiei(HttpSession session) {
        Utente utente = (Utente) session.getAttribute("utenteLoggato");
        if (utente == null) return ResponseEntity.status(401).body("Non autenticato");
        try {
            messaggioDao.eliminaTuttiPerAcquirente(utente.getId());
            return ResponseEntity.ok("Eliminati");
        } catch (SQLException e) {
            return ResponseEntity.status(500).body("Errore server");
        }
    }
    @GetMapping("/admin")
    public ResponseEntity<?> getMessaggiAdmin(HttpSession session) {
        Utente utente = (Utente) session.getAttribute("utenteLoggato");
        if (utente == null || !utente.getRuolo().equals("AMMINISTRATORE"))
            return ResponseEntity.status(403).body("Non autorizzato");
        try {
            return ResponseEntity.ok(messaggioDao.findPerAdmin());
        } catch (SQLException e) {
            return ResponseEntity.status(500).body("Errore server");
        }
    }

    @GetMapping("/count-admin")
    public ResponseEntity<?> countAdmin(HttpSession session) {
        Utente utente = (Utente) session.getAttribute("utenteLoggato");
        if (utente == null || !utente.getRuolo().equals("AMMINISTRATORE"))
            return ResponseEntity.status(403).body("Non autorizzato");
        try {
            return ResponseEntity.ok(messaggioDao.countNonLettiAdmin());
        } catch (SQLException e) {
            return ResponseEntity.status(500).body("Errore server");
        }
    }

}