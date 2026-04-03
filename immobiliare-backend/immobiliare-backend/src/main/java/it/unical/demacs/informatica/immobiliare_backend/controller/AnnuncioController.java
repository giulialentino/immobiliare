package it.unical.demacs.informatica.immobiliare_backend.controller;

import it.unical.demacs.informatica.immobiliare_backend.dao.AnnuncioDao;
import it.unical.demacs.informatica.immobiliare_backend.dao.FotoDao;
import it.unical.demacs.informatica.immobiliare_backend.dao.RecensioneDao;
import it.unical.demacs.informatica.immobiliare_backend.model.Annuncio;
import it.unical.demacs.informatica.immobiliare_backend.model.AnnuncioProxy;
import it.unical.demacs.informatica.immobiliare_backend.model.Utente;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import it.unical.demacs.informatica.immobiliare_backend.dao.MessaggioDao;
import it.unical.demacs.informatica.immobiliare_backend.model.Messaggio;

import java.sql.SQLException;
import java.util.List;

@RestController
@RequestMapping("/api/annunci")
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
public class AnnuncioController {

    @Autowired
    private MessaggioDao messaggioDao;

    @Autowired
    private AnnuncioDao annuncioDao;

    @Autowired
    private FotoDao fotoDao;

    @Autowired
    private RecensioneDao recensioneDao;

    // Tutti possono vedere gli annunci
    @GetMapping
    public ResponseEntity<?> getAll(
            @RequestParam(required = false) String tipoOperazione,
            @RequestParam(required = false) Long idCategoria) {
        try {
            List<Annuncio> lista = annuncioDao.findByFiltri(tipoOperazione, idCategoria);
            // Carica la prima foto per ogni annuncio
            for (Annuncio a : lista) {
                List<String> foto = fotoDao.findUrlsByAnnuncio(a.getId());
                a.setFoto(foto);
            }
            return ResponseEntity.ok(lista);
        } catch (SQLException e) {
            return ResponseEntity.status(500).body("Errore server");
        }
    }

    // Dettaglio annuncio — usa il Proxy per caricare foto e recensioni
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        try {
            Annuncio annuncio = annuncioDao.findById(id);
            if (annuncio == null) return ResponseEntity.notFound().build();

            AnnuncioProxy proxy = new AnnuncioProxy(fotoDao, recensioneDao);
            proxy.setId(annuncio.getId());
            proxy.setTitolo(annuncio.getTitolo());
            proxy.setDescrizione(annuncio.getDescrizione());
            proxy.setPrezzo(annuncio.getPrezzo());
            proxy.setPrezzoRibassato(annuncio.getPrezzoRibassato());
            proxy.setMetriQuadri(annuncio.getMetriQuadri());
            proxy.setTipoOperazione(annuncio.getTipoOperazione());
            proxy.setIndirizzo(annuncio.getIndirizzo());
            proxy.setLatitudine(annuncio.getLatitudine());
            proxy.setLongitudine(annuncio.getLongitudine());
            proxy.setInAsta(annuncio.isInAsta());
            proxy.setIdVenditore(annuncio.getIdVenditore());
            proxy.setIdCategoria(annuncio.getIdCategoria());
            proxy.setStato(annuncio.getStato());
            proxy.setDataInserimento(annuncio.getDataInserimento());

            proxy.getFoto();
            proxy.getRecensioni();

            return ResponseEntity.ok(proxy);
        } catch (SQLException e) {
            return ResponseEntity.status(500).body("Errore server");
        }
    }

    // Solo VENDITORE o AMMINISTRATORE può creare annunci
    @PostMapping
    public ResponseEntity<?> create(@RequestBody Annuncio annuncio, HttpSession session) {
        Utente utente = (Utente) session.getAttribute("utenteLoggato");
        if (utente == null) return ResponseEntity.status(401).body("Non autenticato");
        try {
            List<Annuncio> annunciVenditore = annuncioDao.findByVenditore(utente.getId());
            if (annunciVenditore.size() >= 50) {
                return ResponseEntity.badRequest().body("Hai raggiunto il limite massimo di 50 annunci.");
            }

            annuncio.setIdVenditore(utente.getId());
            annuncio.setStato("IN_ATTESA");
            Annuncio salvato = annuncioDao.save(annuncio);

            // Invia messaggio automatico all'admin
            Messaggio msg = new Messaggio();
            msg.setIdAnnuncio(salvato.getId());
            msg.setIdMittente(utente.getId());
            msg.setOggetto("Nuova richiesta di approvazione");
            msg.setTesto("Il venditore " + utente.getNome() + " " + utente.getCognome() +
                    " ha richiesto l'approvazione dell'annuncio: \"" + salvato.getTitolo() + "\".");
            messaggioDao.savePerAdmin(msg);

            return ResponseEntity.ok(salvato);
        } catch (SQLException e) {
            return ResponseEntity.status(500).body("Errore server");
        }
    }

    // Modifica annuncio
    @PutMapping("/{id}")
    public ResponseEntity<?> modifica(@PathVariable Long id,
                                      @RequestBody Annuncio annuncio,
                                      HttpSession session) {
        Utente utente = (Utente) session.getAttribute("utenteLoggato");
        if (utente == null) return ResponseEntity.status(401).body("Non autenticato");
        try {
            Annuncio esistente = annuncioDao.findById(id);
            if (esistente == null) return ResponseEntity.notFound().build();
            if (!utente.getRuolo().equals("AMMINISTRATORE") &&
                    !esistente.getIdVenditore().equals(utente.getId())) {
                return ResponseEntity.status(403).body("Non autorizzato");
            }

            // Controllo limite modifiche (solo per venditore, non admin)
            // Controllo limite modifiche (solo venditore)
            if (!utente.getRuolo().equals("AMMINISTRATORE") &&
                    esistente.getNumeroModifiche() != null &&
                    esistente.getNumeroModifiche() >= 10) {
                return ResponseEntity.badRequest().body("LIMITE_MODIFICHE");
            }

            annuncio.setId(id);
            annuncio.setNumeroModifiche(
                    esistente.getNumeroModifiche() != null ? esistente.getNumeroModifiche() + 1 : 1
            );
            annuncio.setIndirizzo(esistente.getIndirizzo());
            annuncio.setLatitudine(esistente.getLatitudine());
            annuncio.setLongitudine(esistente.getLongitudine());
            annuncio.setStato("IN_ATTESA");
            annuncioDao.update(annuncio);
            return ResponseEntity.ok(annuncio);
        } catch (SQLException e) {
            return ResponseEntity.status(500).body("Errore server");
        }
    }


    // Elimina annuncio
    @DeleteMapping("/{id}")
    public ResponseEntity<?> elimina(@PathVariable Long id, HttpSession session) {
        Utente utente = (Utente) session.getAttribute("utenteLoggato");
        if (utente == null) return ResponseEntity.status(401).body("Non autenticato");
        try {
            Annuncio esistente = annuncioDao.findById(id);
            if (esistente == null) return ResponseEntity.notFound().build();
            if (!utente.getRuolo().equals("AMMINISTRATORE") &&
                    !esistente.getIdVenditore().equals(utente.getId())) {
                return ResponseEntity.status(403).body("Non autorizzato");
            }
            annuncioDao.delete(id);
            return ResponseEntity.ok("Eliminato");
        } catch (SQLException e) {
            return ResponseEntity.status(500).body("Errore server");
        }
    }

    // Ribassa prezzo
    @PatchMapping("/{id}/ribassa")
    public ResponseEntity<?> ribassa(@PathVariable Long id,
                                     @RequestBody java.util.Map<String, Double> body,
                                     HttpSession session) {
        Utente utente = (Utente) session.getAttribute("utenteLoggato");
        if (utente == null) return ResponseEntity.status(401).body("Non autenticato");
        try {
            Double nuovoPrezzo = body.get("prezzo");
            annuncioDao.ribassaPrezzo(id, nuovoPrezzo);
            return ResponseEntity.ok("Prezzo ribassato");
        } catch (SQLException e) {
            return ResponseEntity.status(500).body("Errore server");
        }
    }
    @PatchMapping("/{id}/annulla-ribasso")
    public ResponseEntity<?> annullaRibasso(@PathVariable Long id, HttpSession session) {
        Utente utente = (Utente) session.getAttribute("utenteLoggato");
        if (utente == null) return ResponseEntity.status(401).body("Non autenticato");
        try {
            annuncioDao.annullaRibasso(id);
            return ResponseEntity.ok("Ribasso annullato");
        } catch (SQLException e) {
            return ResponseEntity.status(500).body("Errore server");
        }
    }
    @GetMapping("/in-attesa")
    public ResponseEntity<?> getInAttesa(HttpSession session) {
        Utente utente = (Utente) session.getAttribute("utenteLoggato");
        if (utente == null || !utente.getRuolo().equals("AMMINISTRATORE"))
            return ResponseEntity.status(403).body("Non autorizzato");
        try {
            return ResponseEntity.ok(annuncioDao.findInAttesa());
        } catch (SQLException e) {
            return ResponseEntity.status(500).body("Errore server");
        }
    }

    @PatchMapping("/{id}/stato")
    public ResponseEntity<?> aggiornaStato(@PathVariable Long id,
                                           @RequestBody Map<String, String> body,
                                           HttpSession session) {
        Utente utente = (Utente) session.getAttribute("utenteLoggato");
        if (utente == null || !utente.getRuolo().equals("AMMINISTRATORE"))
            return ResponseEntity.status(403).body("Non autorizzato");
        try {
            String stato = body.get("stato");
            annuncioDao.aggiornaStato(id, stato);

            // Manda messaggio al venditore
            Annuncio annuncio = annuncioDao.findById(id);
            if (annuncio != null) {
                Messaggio msg = new Messaggio();
                msg.setIdAnnuncio(id);
                msg.setIdMittente(utente.getId());
                msg.setOggetto(stato.equals("APPROVATO") ?
                        "✅ Annuncio approvato!" : "❌ Annuncio rifiutato");
                msg.setTesto(stato.equals("APPROVATO") ?
                        "Il tuo annuncio \"" + annuncio.getTitolo() + "\" è stato approvato ed è ora visibile a tutti gli utenti." :
                        "Il tuo annuncio \"" + annuncio.getTitolo() + "\" è stato rifiutato. Puoi modificarlo e ripubblicarlo.");
                messaggioDao.savePerVenditore(msg, annuncio.getIdVenditore());
            }

            return ResponseEntity.ok("Stato aggiornato");
        } catch (SQLException e) {
            return ResponseEntity.status(500).body("Errore server");
        }
    }
    @GetMapping("/count-in-attesa")
    public ResponseEntity<?> countInAttesa(HttpSession session) {
        Utente utente = (Utente) session.getAttribute("utenteLoggato");
        if (utente == null || !utente.getRuolo().equals("AMMINISTRATORE"))
            return ResponseEntity.status(403).body("Non autorizzato");
        try {
            return ResponseEntity.ok(annuncioDao.countInAttesa());
        } catch (SQLException e) {
            return ResponseEntity.status(500).body("Errore server");
        }
    }
    @GetMapping("/venditore/{idVenditore}")
    public ResponseEntity<?> getByVenditore(@PathVariable Long idVenditore, HttpSession session) {
        Utente utente = (Utente) session.getAttribute("utenteLoggato");
        if (utente == null) return ResponseEntity.status(401).body("Non autenticato");
        try {
            return ResponseEntity.ok(annuncioDao.findByVenditore(idVenditore));
        } catch (SQLException e) {
            return ResponseEntity.status(500).body("Errore server");
        }
    }


}