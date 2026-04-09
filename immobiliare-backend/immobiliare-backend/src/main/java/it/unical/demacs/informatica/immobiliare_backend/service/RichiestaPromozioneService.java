package it.unical.demacs.informatica.immobiliare_backend.service;

import it.unical.demacs.informatica.immobiliare_backend.dao.MessaggioDao;
import it.unical.demacs.informatica.immobiliare_backend.dao.RichiestaPromozioneDao;
import it.unical.demacs.informatica.immobiliare_backend.dao.UtenteDao;
import it.unical.demacs.informatica.immobiliare_backend.model.Messaggio;
import it.unical.demacs.informatica.immobiliare_backend.model.Utente;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;

@Service
public class RichiestaPromozioneService {

    @Autowired
    private RichiestaPromozioneDao richiestaDao;

    @Autowired
    private MessaggioDao messaggioDao;

    @Autowired
    private UtenteDao utenteDao;

    public void richiedi(Utente utente) throws SQLException {
        if (!utente.getRuolo().equals("ACQUIRENTE")) {
            throw new SecurityException("Solo gli acquirenti possono richiedere la promozione");
        }
        if (richiestaDao.exists(utente.getId())) {
            throw new IllegalStateException("Hai già una richiesta in attesa");
        }

        richiestaDao.save(utente.getId());

        Messaggio msg = new Messaggio();
        msg.setIdAnnuncio(null);
        msg.setIdMittente(utente.getId());
        msg.setOggetto("Richiesta promozione a venditore");
        msg.setTesto("L'utente " + utente.getNome() + " " + utente.getCognome() +
                " (" + utente.getEmail() + ") ha richiesto di diventare venditore.");
        messaggioDao.savePerAdmin(msg);
    }

    public String getStato(Long idUtente) throws SQLException {
        String stato = richiestaDao.getStato(idUtente);
        return stato != null ? stato : "NESSUNA";
    }

    public void approva(Long idUtente, Utente admin) throws SQLException {
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
            messaggioDao.saveNotifica(msg);
        }
    }

    public void rifiuta(Long idUtente, Utente admin) throws SQLException {
        richiestaDao.aggiornaStato(idUtente, "RIFIUTATO");

        Utente utente = utenteDao.findById(idUtente);
        if (utente != null) {
            Messaggio msg = new Messaggio();
            msg.setIdAnnuncio(null);
            msg.setIdMittente(admin.getId());
            msg.setOggetto("Richiesta rifiutata");
            msg.setTesto("La tua richiesta di diventare venditore è stata rifiutata. " +
                    "Contatta l'amministratore per maggiori informazioni.");
            messaggioDao.saveNotifica(msg);
        }
    }
}
