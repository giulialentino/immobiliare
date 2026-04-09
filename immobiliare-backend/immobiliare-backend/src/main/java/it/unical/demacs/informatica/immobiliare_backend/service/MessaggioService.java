package it.unical.demacs.informatica.immobiliare_backend.service;

import it.unical.demacs.informatica.immobiliare_backend.dao.MessaggioDao;
import it.unical.demacs.informatica.immobiliare_backend.model.Messaggio;
import it.unical.demacs.informatica.immobiliare_backend.model.Utente;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;

@Service
public class MessaggioService {

    @Autowired
    private MessaggioDao messaggioDao;

    public List<Messaggio> getByVenditore(Long idVenditore) throws SQLException {
        return messaggioDao.findByVenditore(idVenditore);
    }

    public List<Messaggio> getInviati(Long idMittente) throws SQLException {
        return messaggioDao.findByMittente(idMittente);
    }

    public List<Messaggio> getByAnnuncio(Long idAnnuncio) throws SQLException {
        return messaggioDao.findByAnnuncio(idAnnuncio);
    }

    public int countMessaggi(Long idVenditore) throws SQLException {
        return messaggioDao.countMessaggi(idVenditore);
    }

    public Messaggio invia(Messaggio messaggio, Utente utente) throws SQLException {
        messaggio.setIdMittente(utente.getId());
        return messaggioDao.save(messaggio);
    }

    public void segnaComeLetto(Long id) throws SQLException {
        messaggioDao.segnaComeLetto(id);
    }

    public void elimina(Long id, Utente utente) throws SQLException {
        if (utente.getRuolo().equals("ACQUIRENTE")) {
            messaggioDao.eliminaPerAcquirente(id);
        } else {
            messaggioDao.eliminaPerVenditore(id);
        }
    }

    public void eliminaTuttiPerVenditore(Long idVenditore) throws SQLException {
        messaggioDao.eliminaTuttiPerVenditore(idVenditore);
    }

    public void eliminaTuttiPerAcquirente(Long idAcquirente) throws SQLException {
        messaggioDao.eliminaTuttiPerAcquirente(idAcquirente);
    }

    public List<Messaggio> getMessaggiAdmin() throws SQLException {
        return messaggioDao.findPerAdmin();
    }

    public int countNonLettiAdmin() throws SQLException {
        return messaggioDao.countNonLettiAdmin();
    }
}
