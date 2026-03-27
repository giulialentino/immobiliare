package it.unical.demacs.informatica.immobiliare_backend.model;

import it.unical.demacs.informatica.immobiliare_backend.dao.FotoDao;
import it.unical.demacs.informatica.immobiliare_backend.dao.RecensioneDao;

import java.sql.SQLException;
import java.util.List;

public class AnnuncioProxy extends Annuncio {

    private final FotoDao fotoDao;
    private final RecensioneDao recensioneDao;

    private boolean fotoCaricate = false;
    private boolean recensioniCaricate = false;

    public AnnuncioProxy(FotoDao fotoDao, RecensioneDao recensioneDao) {
        this.fotoDao = fotoDao;
        this.recensioneDao = recensioneDao;
    }

    // Carica le foto SOLO la prima volta che vengono richieste
    @Override
    public List<String> getFoto() {
        if (!fotoCaricate) {
            try {
                super.setFoto(fotoDao.findUrlsByAnnuncio(this.getId()));
                fotoCaricate = true;
            } catch (SQLException e) {
                throw new RuntimeException("Errore caricamento foto", e);
            }
        }
        return super.getFoto();
    }

    // Carica le recensioni SOLO la prima volta che vengono richieste
    @Override
    public List<Recensione> getRecensioni() {
        if (!recensioniCaricate) {
            try {
                super.setRecensioni(recensioneDao.findByAnnuncio(this.getId()));
                recensioniCaricate = true;
            } catch (SQLException e) {
                throw new RuntimeException("Errore caricamento recensioni", e);
            }
        }
        return super.getRecensioni();
    }
}