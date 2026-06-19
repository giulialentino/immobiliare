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
        Asta asta = astaDao.findById(idAsta);
        if (asta == null) {
            throw new IllegalArgumentException("Asta non trovata");
        }
        if (!asta.isAttiva()) {
            throw new IllegalStateException("L'asta è già chiusa");
        }
        if (asta.getDataScadenza() != null && asta.getDataScadenza().isBefore(java.time.LocalDateTime.now())) {
            // L'asta è scaduta ma il job automatico non l'ha ancora marcata come chiusa
            // (può succedere nei pochi secondi tra la scadenza e il prossimo ciclo dello scheduler):
            // la chiudiamo subito qui, invece di accettare un'offerta su un'asta di fatto già finita.
            astaDao.chiudi(idAsta);
            annuncioDao.aggiornaInAsta(asta.getIdAnnuncio(), false);
            throw new IllegalStateException("L'asta è scaduta");
        }
        Offerta offerta = new Offerta();
        offerta.setIdAsta(idAsta);
        offerta.setIdUtente(utente.getId());
        offerta.setImporto(importo);
        offertaDao.save(offerta);
        astaDao.aggiornaOfferta(idAsta, importo, utente.getId());
    }

    public List<Offerta> getOfferte(Long idAsta, Utente richiedente) throws SQLException {
        List<Offerta> offerte = offertaDao.findByAsta(idAsta);

        boolean puoVedereNomi = false;
        if (richiedente != null) {
            if (richiedente.getRuolo().equals("AMMINISTRATORE")) {
                puoVedereNomi = true;
            } else if (richiedente.getRuolo().equals("VENDITORE")) {
                Asta asta = astaDao.findById(idAsta);
                if (asta != null) {
                    Annuncio annuncio = annuncioDao.findById(asta.getIdAnnuncio());
                    puoVedereNomi = annuncio != null && annuncio.getIdVenditore().equals(richiedente.getId());
                }
            }
        }

        // Chi non ha diritto di vedere i nomi (acquirenti, visitatori non loggati) riceve
        // comunque gli importi, ma con nomeOfferente svuotato: il filtro avviene qui,
        // lato server, prima che la risposta esca — non è un nascondimento solo grafico.
        if (!puoVedereNomi) {
            for (Offerta o : offerte) {
                o.setNomeOfferente(null);
            }
        }
        return offerte;
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

    // Job schedulato: chiude automaticamente tutte le aste la cui scadenza è passata.
    // Eseguito dallo scheduler di Spring (vedi @Scheduled), non da un utente:
    // qui non serve il controllo sui permessi presente in chiudiAsta().
    @org.springframework.scheduling.annotation.Scheduled(fixedRate = 60000)
    public void chiudiAsteScadute() {
        try {
            List<Asta> scadute = astaDao.findScadute();
            for (Asta asta : scadute) {
                astaDao.chiudi(asta.getId());
                annuncioDao.aggiornaInAsta(asta.getIdAnnuncio(), false);
            }
        } catch (SQLException e) {
            System.err.println("Errore durante la chiusura automatica delle aste scadute: " + e.getMessage());
        }
    }
}