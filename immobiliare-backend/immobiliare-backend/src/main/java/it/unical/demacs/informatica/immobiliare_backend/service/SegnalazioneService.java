package it.unical.demacs.informatica.immobiliare_backend.service;

import it.unical.demacs.informatica.immobiliare_backend.dao.AnnuncioDao;
import it.unical.demacs.informatica.immobiliare_backend.dao.MessaggioDao;
import it.unical.demacs.informatica.immobiliare_backend.dao.SegnalazioneDao;
import it.unical.demacs.informatica.immobiliare_backend.model.Annuncio;
import it.unical.demacs.informatica.immobiliare_backend.model.Messaggio;
import it.unical.demacs.informatica.immobiliare_backend.model.Segnalazione;
import it.unical.demacs.informatica.immobiliare_backend.model.Utente;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;

@Service
public class SegnalazioneService {

    @Autowired
    private SegnalazioneDao segnalazioneDao;

    @Autowired
    private MessaggioDao messaggioDao;

    @Autowired
    private AnnuncioDao annuncioDao;

    public boolean checkSegnalazione(Long idAnnuncio, Long idUtente) throws SQLException {
        return segnalazioneDao.esisteSegnalazione(idAnnuncio, idUtente);
    }

    public void segnala(Segnalazione segnalazione, Utente utente) throws SQLException {
        if (segnalazioneDao.esisteSegnalazione(segnalazione.getIdAnnuncio(), utente.getId())) {
            throw new IllegalStateException("GIA_SEGNALATO");
        }

        segnalazione.setIdSegnalante(utente.getId());
        segnalazioneDao.save(segnalazione);

        Annuncio annuncio = annuncioDao.findById(segnalazione.getIdAnnuncio());

        Messaggio msgAdmin = new Messaggio();
        msgAdmin.setIdAnnuncio(segnalazione.getIdAnnuncio());
        msgAdmin.setIdMittente(utente.getId());
        msgAdmin.setOggetto("⚠️ Segnalazione annuncio");
        msgAdmin.setTesto("L'utente " + utente.getNome() + " " + utente.getCognome() +
                " ha segnalato l'annuncio: \"" + (annuncio != null ? annuncio.getTitolo() : "#" + segnalazione.getIdAnnuncio()) + "\"." +
                "\n\nCategoria: " + segnalazione.getCategoria() +
                "\nMotivo: " + segnalazione.getMotivo());
        messaggioDao.savePerAdmin(msgAdmin);
    }

    public List<Segnalazione> getAll() throws SQLException {
        return segnalazioneDao.findAll();
    }

    public void segnaGestita(Long id) throws SQLException {
        segnalazioneDao.aggiornaStato(id, "GESTITA");
    }
}
