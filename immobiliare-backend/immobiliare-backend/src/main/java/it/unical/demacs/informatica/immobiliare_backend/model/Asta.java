package it.unical.demacs.informatica.immobiliare_backend.model;

import java.time.LocalDateTime;

public class Asta {
    private Long id;
    private Long idAnnuncio;
    private Double prezzoBase;
    private Double offertaMax;
    private Long idOfferente;
    private LocalDateTime dataScadenza;
    private boolean attiva;

    public Asta() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getIdAnnuncio() { return idAnnuncio; }
    public void setIdAnnuncio(Long idAnnuncio) { this.idAnnuncio = idAnnuncio; }

    public Double getPrezzoBase() { return prezzoBase; }
    public void setPrezzoBase(Double prezzoBase) { this.prezzoBase = prezzoBase; }

    public Double getOffertaMax() { return offertaMax; }
    public void setOffertaMax(Double offertaMax) { this.offertaMax = offertaMax; }

    public Long getIdOfferente() { return idOfferente; }
    public void setIdOfferente(Long idOfferente) { this.idOfferente = idOfferente; }

    public LocalDateTime getDataScadenza() { return dataScadenza; }
    public void setDataScadenza(LocalDateTime dataScadenza) { this.dataScadenza = dataScadenza; }

    public boolean isAttiva() { return attiva; }
    public void setAttiva(boolean attiva) { this.attiva = attiva; }
}