package it.unical.demacs.informatica.immobiliare_backend.model;

import java.time.LocalDateTime;

public class Offerta {
    private Long id;
    private Long idAsta;
    private Long idUtente;
    private Double importo;
    private LocalDateTime dataOfferta;
    // Valorizzato dal service solo se chi fa la richiesta ha diritto di vederlo
    // (venditore dell'annuncio o amministratore). Per chiunque altro resta null,
    // così il nome di chi offre non esce mai nella risposta JSON per chi non deve vederlo.
    private String nomeOfferente;

    public Offerta() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getIdAsta() { return idAsta; }
    public void setIdAsta(Long idAsta) { this.idAsta = idAsta; }

    public Long getIdUtente() { return idUtente; }
    public void setIdUtente(Long idUtente) { this.idUtente = idUtente; }

    public Double getImporto() { return importo; }
    public void setImporto(Double importo) { this.importo = importo; }

    public LocalDateTime getDataOfferta() { return dataOfferta; }
    public void setDataOfferta(LocalDateTime dataOfferta) { this.dataOfferta = dataOfferta; }

    public String getNomeOfferente() { return nomeOfferente; }
    public void setNomeOfferente(String nomeOfferente) { this.nomeOfferente = nomeOfferente; }
}