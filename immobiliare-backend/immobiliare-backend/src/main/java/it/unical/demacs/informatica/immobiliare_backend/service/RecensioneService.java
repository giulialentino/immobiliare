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
        recensione.setIdUtente(utente.getId());
        return recensioneDao.save(recensione);
    }
}
