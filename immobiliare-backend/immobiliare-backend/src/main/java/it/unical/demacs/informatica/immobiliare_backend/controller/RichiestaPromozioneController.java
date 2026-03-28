package it.unical.demacs.informatica.immobiliare_backend.controller;

import it.unical.demacs.informatica.immobiliare_backend.dao.MessaggioDao;
import it.unical.demacs.informatica.immobiliare_backend.dao.RichiestaPromozioneDao;
import it.unical.demacs.informatica.immobiliare_backend.dao.UtenteDao;
import it.unical.demacs.informatica.immobiliare_backend.model.Messaggio;
import it.unical.demacs.informatica.immobiliare_backend.model.Utente;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;

@RestController
@RequestMapping("/api/promozione")
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
public class RichiestaPromozioneController {

    @Autowired
    private RichiestaPromozioneDao richiestaDao;

    @Autowired
    private MessaggioDao messaggioDao;

    @Autowired
    private UtenteDao utenteDao;

    @PostMapping("/richiedi")
    public ResponseEntity<?> richiedi(HttpSession session) {
        Utente utente = (Utente) session.getAttribute("utenteLoggato");
        if (utente == null) return ResponseEntity.status(401).body("Non autenticato");
        if (!utente.getRuolo().equals("ACQUIRENTE"))
            return ResponseEntity.status(403).body("Solo gli acquirenti possono richiedere la promozione");
        try {
            if (richiestaDao.exists(utente.getId())) {
                return ResponseEntity.badRequest().body("Hai già una richiesta in attesa");
            }
            richiestaDao.save(utente.getId());

            Messaggio msg = new Messaggio();
            msg.setIdAnnuncio(null);
            msg.setIdMittente(utente.getId());
            msg.setOggetto("Richiesta promozione a venditore");
            msg.setTesto("L'utente " + utente.getNome() + " " + utente.getCognome() +
                    " (" + utente.getEmail() + ") ha richiesto di diventare venditore.");
            messaggioDao.savePerAdmin(msg);

            return ResponseEntity.ok("Richiesta inviata");
        } catch (SQLException e) {
            return ResponseEntity.status(500).body("Errore server: " + e.getMessage());
        }
    }

    @GetMapping("/stato")
    public ResponseEntity<?> getStato(HttpSession session) {
        Utente utente = (Utente) session.getAttribute("utenteLoggato");
        if (utente == null) return ResponseEntity.status(401).body("Non autenticato");
        try {
            String stato = richiestaDao.getStato(utente.getId());
            return ResponseEntity.ok(stato != null ? stato : "NESSUNA");
        } catch (SQLException e) {
            return ResponseEntity.status(500).body("Errore server");
        }
    }

    @PostMapping("/approva/{idUtente}")
    public ResponseEntity<?> approva(@PathVariable Long idUtente, HttpSession session) {
        Utente admin = (Utente) session.getAttribute("utenteLoggato");
        if (admin == null || !admin.getRuolo().equals("AMMINISTRATORE"))
            return ResponseEntity.status(403).body("Non autorizzato");
        try {
            utenteDao.aggiornaRuolo(idUtente, "VENDITORE");
            richiestaDao.aggiornaStato(idUtente, "APPROVATO");

            Utente utente = utenteDao.findById(idUtente);
            if (utente != null) {
                Messaggio msg = new Messaggio();
                msg.setIdAnnuncio(null);
                msg.setIdMittente(admin.getId());
                msg.setOggetto("Sei diventato venditore!");
                msg.setTesto("Congratulazioni " + utente.getNome() + "! La tua richiesta è stata approvata. " +
                        "Ora puoi pubblicare annunci sulla piattaforma.");
                messaggioDao.saveNotificaUtente(msg, idUtente);
            }

            return ResponseEntity.ok("APPROVATO");
        } catch (SQLException e) {
            return ResponseEntity.status(500).body("Errore server");
        }
    }

    @PostMapping("/rifiuta/{idUtente}")
    public ResponseEntity<?> rifiuta(@PathVariable Long idUtente, HttpSession session) {
        Utente admin = (Utente) session.getAttribute("utenteLoggato");
        if (admin == null || !admin.getRuolo().equals("AMMINISTRATORE"))
            return ResponseEntity.status(403).body("Non autorizzato");
        try {
            richiestaDao.aggiornaStato(idUtente, "RIFIUTATO");

            Utente utente = utenteDao.findById(idUtente);
            if (utente != null) {
                Messaggio msg = new Messaggio();
                msg.setIdAnnuncio(null);
                msg.setIdMittente(admin.getId());
                msg.setOggetto("Richiesta rifiutata");
                msg.setTesto("La tua richiesta di diventare venditore è stata rifiutata. " +
                        "Contatta l'amministratore per maggiori informazioni.");
                messaggioDao.saveNotificaUtente(msg, idUtente);
            }

            return ResponseEntity.ok("RIFIUTATO");
        } catch (SQLException e) {
            return ResponseEntity.status(500).body("Errore server");
        }
    }
}