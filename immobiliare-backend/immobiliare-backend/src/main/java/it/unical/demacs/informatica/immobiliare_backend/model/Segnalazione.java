package it.unical.demacs.informatica.immobiliare_backend.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;

public class Segnalazione {
    private Long id;
    private Long idAnnuncio;
    private Long idSegnalante;
    private String motivo;
    private String categoria; // "CONTENUTO_FALSO", "TRUFFA", "FOTO_NON_REALI", "ALTRO"
    private String stato; // "IN_ATTESA", "GESTITA"
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime dataInserimento;
    private String nomeSegnalante;
    private String cognomeSegnalante;
    private String titoloAnnuncio;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getIdAnnuncio() { return idAnnuncio; }
    public void setIdAnnuncio(Long idAnnuncio) { this.idAnnuncio = idAnnuncio; }
    public Long getIdSegnalante() { return idSegnalante; }
    public void setIdSegnalante(Long idSegnalante) { this.idSegnalante = idSegnalante; }
    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }
    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }
    public String getStato() { return stato; }
    public void setStato(String stato) { this.stato = stato; }
    public LocalDateTime getDataInserimento() { return dataInserimento; }
    public void setDataInserimento(LocalDateTime d) { this.dataInserimento = d; }
    public String getNomeSegnalante() { return nomeSegnalante; }
    public void setNomeSegnalante(String n) { this.nomeSegnalante = n; }
    public String getCognomeSegnalante() { return cognomeSegnalante; }
    public void setCognomeSegnalante(String c) { this.cognomeSegnalante = c; }
    public String getTitoloAnnuncio() { return titoloAnnuncio; }
    public void setTitoloAnnuncio(String t) { this.titoloAnnuncio = t; }
}