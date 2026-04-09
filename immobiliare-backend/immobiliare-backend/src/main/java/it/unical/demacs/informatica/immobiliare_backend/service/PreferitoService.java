package it.unical.demacs.informatica.immobiliare_backend.service;

import it.unical.demacs.informatica.immobiliare_backend.dao.AnnuncioDao;
import it.unical.demacs.informatica.immobiliare_backend.dao.PreferitoDao;
import it.unical.demacs.informatica.immobiliare_backend.model.Annuncio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Service
public class PreferitoService {

    @Autowired
    private PreferitoDao preferitoDao;

    @Autowired
    private AnnuncioDao annuncioDao;

    public List<Annuncio> getPreferiti(Long idUtente) throws SQLException {
        List<Long> ids = preferitoDao.findIdAnnunciByUtente(idUtente);
        List<Annuncio> annunci = new ArrayList<>();
        for (Long id : ids) {
            Annuncio a = annuncioDao.findById(id);
            if (a != null) annunci.add(a);
        }
        return annunci;
    }

    public boolean isPreferito(Long idUtente, Long idAnnuncio) throws SQLException {
        return preferitoDao.isPreferito(idUtente, idAnnuncio);
    }

    public void aggiungi(Long idUtente, Long idAnnuncio) throws SQLException {
        preferitoDao.aggiungi(idUtente, idAnnuncio);
    }

    public void rimuovi(Long idUtente, Long idAnnuncio) throws SQLException {
        preferitoDao.rimuovi(idUtente, idAnnuncio);
    }
}
