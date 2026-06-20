package it.unical.demacs.informatica.immobiliare_backend.service;

import it.unical.demacs.informatica.immobiliare_backend.dao.RecensioneDao;
import it.unical.demacs.informatica.immobiliare_backend.model.Recensione;
import it.unical.demacs.informatica.immobiliare_backend.model.Utente;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;

@Service
public class RecensioneService {

    @Autowired
    private RecensioneDao recensioneDao;

    public List<Recensione> getByAnnuncio(Long idAnnuncio) throws SQLException {
        return recensioneDao.findByAnnuncio(idAnnuncio);
    }

    public Recensione salva(Recensione recensione, Utente utente) throws SQLException {
        if (!utente.getRuolo().equals("ACQUIRENTE")) {
            throw new SecurityException("Solo gli acquirenti possono recensire");
        }
        boolean haContattato = recensioneDao.haContattatoVenditore(recensione.getIdAnnuncio(), utente.getId());
        if (!haContattato) {
            throw new IllegalStateException("Puoi recensire solo gli annunci per cui hai contattato il venditore");
        }
        boolean giaRecensito = recensioneDao.findByAnnuncio(recensione.getIdAnnuncio()).stream()
                .anyMatch(r -> r.getIdUtente().equals(utente.getId()));
        if (giaRecensito) {
            throw new IllegalStateException("Hai già recensito questo annuncio");
        }
        recensione.setIdUtente(utente.getId());
        return recensioneDao.save(recensione);
    }

    // Dice al frontend se mostrare il form di recensione: vero solo se l'utente ha
    // contattato il venditore per questo annuncio e non lo ha già recensito.
    public boolean puoRecensire(Long idAnnuncio, Utente utente) throws SQLException {
        boolean haContattato = recensioneDao.haContattatoVenditore(idAnnuncio, utente.getId());
        if (!haContattato) return false;
        boolean giaRecensito = recensioneDao.findByAnnuncio(idAnnuncio).stream()
                .anyMatch(r -> r.getIdUtente().equals(utente.getId()));
        return !giaRecensito;
    }
}