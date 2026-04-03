package it.unical.demacs.informatica.immobiliare_backend.controller;

import it.unical.demacs.informatica.immobiliare_backend.dao.AnnuncioDao;
import it.unical.demacs.informatica.immobiliare_backend.dao.MessaggioDao;
import it.unical.demacs.informatica.immobiliare_backend.dao.SegnalazioneDao;
import it.unical.demacs.informatica.immobiliare_backend.model.Annuncio;
import it.unical.demacs.informatica.immobiliare_backend.model.Messaggio;
import it.unical.demacs.informatica.immobiliare_backend.model.Segnalazione;
import it.unical.demacs.informatica.immobiliare_backend.model.Utente;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;

@RestController
@RequestMapping("/api/segnalazioni")
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
public class SegnalazioneController {

    @Autowired
    private SegnalazioneDao segnalazioneDao;

    @Autowired
    private MessaggioDao messaggioDao;

    @Autowired
    private AnnuncioDao annuncioDao;

    // Invia una segnalazione
    @PostMapping
    public ResponseEntity<?> segnala(@RequestBody Segnalazione segnalazione,
                                     HttpSession session) {
        Utente utente = (Utente) session.getAttribute("utenteLoggato");
        if (utente == null) return ResponseEntity.status(401).body("Non autenticato");

        try {
            // Controlla se ha già segnalato questo annuncio
            if (segnalazioneDao.esisteSegnalazione(segnalazione.getIdAnnuncio(), utente.getId())) {
                return ResponseEntity.badRequest().body("GIA_SEGNALATO");
            }

            segnalazione.setIdSegnalante(utente.getId());
            segnalazioneDao.save(segnalazione);

            // Invia notifica all'admin come messaggio (stesso meccanismo degli annunci)
            Annuncio annuncio = annuncioDao.findById(segnalazione.getIdAnnuncio());
            Messaggio msg = new Messaggio();
            msg.setIdAnnuncio(segnalazione.getIdAnnuncio());
            msg.setIdMittente(utente.getId());
            msg.setOggetto("⚠️ Segnalazione annuncio");
            msg.setTesto("L'utente " + utente.getNome() + " " + utente.getCognome() +
                    " ha segnalato l'annuncio: \"" + (annuncio != null ? annuncio.getTitolo() : "#" + segnalazione.getIdAnnuncio()) + "\"." +
                    "\n\nCategoria: " + segnalazione.getCategoria() +
                    "\nMotivo: " + segnalazione.getMotivo());
            messaggioDao.savePerAdmin(msg);

            // Manda conferma all'acquirente nei suoi messaggi inviati
            Messaggio conferma = new Messaggio();
            conferma.setIdAnnuncio(segnalazione.getIdAnnuncio());
            conferma.setIdMittente(utente.getId());
            conferma.setOggetto("✅ Segnalazione inviata");
            conferma.setTesto("Hai segnalato l'annuncio: \"" +
                    (annuncio != null ? annuncio.getTitolo() : "#" + segnalazione.getIdAnnuncio()) + "\"." +
                    "\nCategoria: " + segnalazione.getCategoria() +
                    "\nMotivo: " + segnalazione.getMotivo() +
                    "\n\nL'amministratore esaminerà la segnalazione al più presto.");
            messaggioDao.savePerVenditore(conferma, utente.getId());

            return ResponseEntity.ok("Segnalazione inviata");
        } catch (SQLException e) {
            return ResponseEntity.status(500).body("Errore server");
        }
    }

    // Lista segnalazioni (solo admin)
    @GetMapping
    public ResponseEntity<?> getAll(HttpSession session) {
        Utente utente = (Utente) session.getAttribute("utenteLoggato");
        if (utente == null || !utente.getRuolo().equals("AMMINISTRATORE"))
            return ResponseEntity.status(403).body("Non autorizzato");
        try {
            return ResponseEntity.ok(segnalazioneDao.findAll());
        } catch (SQLException e) {
            return ResponseEntity.status(500).body("Errore server");
        }
    }

    // Segna come gestita (solo admin)
    @PatchMapping("/{id}/gestita")
    public ResponseEntity<?> segnaGestita(@PathVariable Long id, HttpSession session) {
        Utente utente = (Utente) session.getAttribute("utenteLoggato");
        if (utente == null || !utente.getRuolo().equals("AMMINISTRATORE"))
            return ResponseEntity.status(403).body("Non autorizzato");
        try {
            segnalazioneDao.aggiornaStato(id, "GESTITA");
            return ResponseEntity.ok("Gestita");
        } catch (SQLException e) {
            return ResponseEntity.status(500).body("Errore server");
        }
    }
}