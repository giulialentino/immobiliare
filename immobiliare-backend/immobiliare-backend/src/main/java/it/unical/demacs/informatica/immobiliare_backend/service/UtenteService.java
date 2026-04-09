package it.unical.demacs.informatica.immobiliare_backend.service;

import it.unical.demacs.informatica.immobiliare_backend.dao.UtenteDao;
import it.unical.demacs.informatica.immobiliare_backend.model.Utente;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;

@Service
public class UtenteService {

    @Autowired
    private UtenteDao utenteDao;

    public List<Utente> getAll() throws SQLException {
        List<Utente> lista = utenteDao.findAll();
        lista.forEach(u -> u.setPassword(null));
        return lista;
    }

    public void banna(Long id) throws SQLException {
        utenteDao.setBannato(id, true);
    }

    public void promuoviAdAdmin(Long id) throws SQLException {
        utenteDao.aggiornaRuolo(id, "AMMINISTRATORE");
    }
}
