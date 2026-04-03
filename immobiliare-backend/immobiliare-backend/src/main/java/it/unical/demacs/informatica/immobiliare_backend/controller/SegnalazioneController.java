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

    @GetMapping("/check/{idAnnuncio}")
    public ResponseEntity<?> check(@PathVariable Long idAnnuncio, HttpSession session) {
        Utente utente = (Utente) session.getAttribute("utenteLoggato");
        if (utente == null) return ResponseEntity.ok("false");
        try {
            boolean esiste = segnalazioneDao.esisteSegnalazione(idAnnuncio, utente.getId());
            return ResponseEntity.ok(String.valueOf(esiste));
        } catch (SQLException e) {
            return ResponseEntity.ok("false");
        }
    }

    @PostMapping
    public ResponseEntity<?> segnala(@RequestBody Segnalazione segnalazione,
                                     HttpSession session) {
        Utente utente = (Utente) session.getAttribute("utenteLoggato");
        if (utente == null) return ResponseEntity.status(401).body("Non autenticato");

        try {
            if (segnalazioneDao.esisteSegnalazione(segnalazione.getIdAnnuncio(), utente.getId())) {
                return ResponseEntity.badRequest().body("GIA_SEGNALATO");
            }

            segnalazione.setIdSegnalante(utente.getId());
            segnalazioneDao.save(segnalazione);

            Annuncio annuncio = annuncioDao.findById(segnalazione.getIdAnnuncio());

            // Solo messaggio all'admin, nessuna conferma all'acquirente
            Messaggio msgAdmin = new Messaggio();
            msgAdmin.setIdAnnuncio(segnalazione.getIdAnnuncio());
            msgAdmin.setIdMittente(utente.getId());
            msgAdmin.setOggetto("⚠️ Segnalazione annuncio");
            msgAdmin.setTesto("L'utente " + utente.getNome() + " " + utente.getCognome() +
                    " ha segnalato l'annuncio: \"" + (annuncio != null ? annuncio.getTitolo() : "#" + segnalazione.getIdAnnuncio()) + "\"." +
                    "\n\nCategoria: " + segnalazione.getCategoria() +
                    "\nMotivo: " + segnalazione.getMotivo());
            messaggioDao.savePerAdmin(msgAdmin);

            return ResponseEntity.ok("Segnalazione inviata");
        } catch (SQLException e) {
            return ResponseEntity.status(500).body("Errore server");
        }
    }

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