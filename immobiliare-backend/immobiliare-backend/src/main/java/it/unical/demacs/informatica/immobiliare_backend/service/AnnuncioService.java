package it.unical.demacs.informatica.immobiliare_backend.service;

import it.unical.demacs.informatica.immobiliare_backend.dao.*;
import it.unical.demacs.informatica.immobiliare_backend.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;

@Service
public class AnnuncioService {

    @Autowired
    private AnnuncioDao annuncioDao;

    @Autowired
    private FotoDao fotoDao;

    @Autowired
    private RecensioneDao recensioneDao;

    @Autowired
    private MessaggioDao messaggioDao;

    public List<Annuncio> getAnnunciFiltrati(String tipoOperazione, Long idCategoria) throws SQLException {
        List<Annuncio> lista = annuncioDao.findByFiltri(tipoOperazione, idCategoria);
        for (Annuncio a : lista) {
            a.setFoto(fotoDao.findUrlsByAnnuncio(a.getId()));
        }
        return lista;
    }

    public AnnuncioProxy getDettaglio(Long id) throws SQLException {
        Annuncio annuncio = annuncioDao.findById(id);
        if (annuncio == null) return null;

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

        return proxy;
    }

    public Annuncio creaAnnuncio(Annuncio annuncio, Utente utente) throws SQLException {
        List<Annuncio> annunciVenditore = annuncioDao.findByVenditore(utente.getId());
        if (annunciVenditore.size() >= 50) {
            throw new IllegalStateException("Hai raggiunto il limite massimo di 50 annunci.");
        }

        annuncio.setIdVenditore(utente.getId());
        annuncio.setStato("IN_ATTESA");
        Annuncio salvato = annuncioDao.save(annuncio);

        Messaggio msg = new Messaggio();
        msg.setIdAnnuncio(salvato.getId());
        msg.setIdMittente(utente.getId());
        msg.setOggetto("Nuova richiesta di approvazione");
        msg.setTesto("Il venditore " + utente.getNome() + " " + utente.getCognome() +
                " ha richiesto l'approvazione dell'annuncio: \"" + salvato.getTitolo() + "\".");
        messaggioDao.savePerAdmin(msg);

        return salvato;
    }

    public Annuncio modificaAnnuncio(Long id, Annuncio annuncio, Utente utente) throws SQLException {
        Annuncio esistente = annuncioDao.findById(id);
        if (esistente == null) return null;

        if (!utente.getRuolo().equals("AMMINISTRATORE") &&
                !esistente.getIdVenditore().equals(utente.getId())) {
            throw new SecurityException("Non autorizzato");
        }

        if (!utente.getRuolo().equals("AMMINISTRATORE") &&
                "APPROVATO".equals(esistente.getStato()) &&
                esistente.getNumeroModifiche() != null &&
                esistente.getNumeroModifiche() >= 10) {
            throw new IllegalStateException("LIMITE_MODIFICHE");
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
        return annuncio;
    }

    public boolean eliminaAnnuncio(Long id, Utente utente) throws SQLException {
        Annuncio esistente = annuncioDao.findById(id);
        if (esistente == null) return false;

        if (!utente.getRuolo().equals("AMMINISTRATORE") &&
                !esistente.getIdVenditore().equals(utente.getId())) {
            throw new SecurityException("Non autorizzato");
        }

        annuncioDao.delete(id);
        return true;
    }

    public void ribassaPrezzo(Long id, Double nuovoPrezzo, Utente utente) throws SQLException {
        Annuncio esistente = annuncioDao.findById(id);
        if (esistente == null) throw new IllegalArgumentException("Annuncio non trovato");
        if (!utente.getRuolo().equals("AMMINISTRATORE") &&
                !esistente.getIdVenditore().equals(utente.getId())) {
            throw new SecurityException("Non autorizzato");
        }
        annuncioDao.ribassaPrezzo(id, nuovoPrezzo);
    }

    public void annullaRibasso(Long id, Utente utente) throws SQLException {
        Annuncio esistente = annuncioDao.findById(id);
        if (esistente == null) throw new IllegalArgumentException("Annuncio non trovato");
        if (!utente.getRuolo().equals("AMMINISTRATORE") &&
                !esistente.getIdVenditore().equals(utente.getId())) {
            throw new SecurityException("Non autorizzato");
        }
        annuncioDao.annullaRibasso(id);
    }

    public List<Annuncio> getInAttesa() throws SQLException {
        return annuncioDao.findInAttesa();
    }

    public int countInAttesa() throws SQLException {
        return annuncioDao.countInAttesa();
    }

    public void aggiornaStato(Long id, String stato, Utente admin) throws SQLException {
        annuncioDao.aggiornaStato(id, stato);

        Annuncio annuncio = annuncioDao.findById(id);
        if (annuncio != null) {
            Messaggio msg = new Messaggio();
            msg.setIdAnnuncio(id);
            msg.setIdMittente(admin.getId());
            msg.setOggetto(stato.equals("APPROVATO") ?
                    "✅ Annuncio approvato!" : "❌ Annuncio rifiutato");
            msg.setTesto(stato.equals("APPROVATO") ?
                    "Il tuo annuncio \"" + annuncio.getTitolo() + "\" è stato approvato ed è ora visibile a tutti gli utenti." :
                    "Il tuo annuncio \"" + annuncio.getTitolo() + "\" è stato rifiutato. Puoi modificarlo e ripubblicarlo.");
            messaggioDao.saveNotifica(msg);
        }
    }

    public List<Annuncio> getByVenditore(Long idVenditore) throws SQLException {
        return annuncioDao.findByVenditore(idVenditore);
    }
}
