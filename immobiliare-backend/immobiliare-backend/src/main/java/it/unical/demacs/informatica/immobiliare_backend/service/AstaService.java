package it.unical.demacs.informatica.immobiliare_backend.service;

import it.unical.demacs.informatica.immobiliare_backend.dao.AnnuncioDao;
import it.unical.demacs.informatica.immobiliare_backend.dao.AstaDao;
import it.unical.demacs.informatica.immobiliare_backend.dao.OffertaDao;
import it.unical.demacs.informatica.immobiliare_backend.model.Asta;
import it.unical.demacs.informatica.immobiliare_backend.model.Offerta;
import it.unical.demacs.informatica.immobiliare_backend.model.Utente;
import it.unical.demacs.informatica.immobiliare_backend.model.Annuncio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;

@Service
public class AstaService {

    @Autowired
    private AstaDao astaDao;

    @Autowired
    private OffertaDao offertaDao;

    @Autowired
    private AnnuncioDao annuncioDao;

    public Asta getByAnnuncio(Long idAnnuncio) throws SQLException {
        return astaDao.findByAnnuncio(idAnnuncio);
    }

    public Asta creaAsta(Asta asta) throws SQLException {
        Asta salvata = astaDao.save(asta);
        annuncioDao.aggiornaInAsta(asta.getIdAnnuncio(), true);
        return salvata;
    }

    public void faiOfferta(Long idAsta, Double importo, Utente utente) throws SQLException {
        if (importo == null) {
            throw new IllegalArgumentException("Importo mancante");
        }
        Offerta offerta = new Offerta();
        offerta.setIdAsta(idAsta);
        offerta.setIdUtente(utente.getId());
        offerta.setImporto(importo);
        offertaDao.save(offerta);
        astaDao.aggiornaOfferta(idAsta, importo, utente.getId());
    }

    public List<Offerta> getOfferte(Long idAsta) throws SQLException {
        return offertaDao.findByAsta(idAsta);
    }

    public void chiudiAsta(Long idAsta, Utente utente) throws SQLException {
        Asta asta = astaDao.findById(idAsta);
        if (asta == null) throw new IllegalArgumentException("Asta non trovata");

        Annuncio annuncio = annuncioDao.findById(asta.getIdAnnuncio());
        if (annuncio != null &&
                !utente.getRuolo().equals("AMMINISTRATORE") &&
                !annuncio.getIdVenditore().equals(utente.getId())) {
            throw new SecurityException("Non autorizzato");
        }

        astaDao.chiudi(idAsta);
        if (annuncio != null) {
            annuncioDao.aggiornaInAsta(asta.getIdAnnuncio(), false);
        }
    }
}
