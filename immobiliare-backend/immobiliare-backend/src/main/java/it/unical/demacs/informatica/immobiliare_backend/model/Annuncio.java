package it.unical.demacs.informatica.immobiliare_backend.model;

import java.util.List;

public class Annuncio {
    private Long id;
    private String titolo;
    private String descrizione;
    private Double prezzo;
    private Double prezzoRibassato;
    private Integer metriQuadri;
    private String tipoOperazione; // "VENDITA" o "AFFITTO"
    private String indirizzo;
    private Double latitudine;
    private Double longitudine;
    private boolean inAsta;
    private Long idVenditore;
    private Long idCategoria;

    // Questi vengono caricati dal Proxy, non dal DAO direttamente
    private List<String> foto;
    private List<Recensione> recensioni;

    public Annuncio() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitolo() { return titolo; }
    public void setTitolo(String titolo) { this.titolo = titolo; }

    public String getDescrizione() { return descrizione; }
    public void setDescrizione(String descrizione) { this.descrizione = descrizione; }

    public Double getPrezzo() { return prezzo; }
    public void setPrezzo(Double prezzo) { this.prezzo = prezzo; }

    public Double getPrezzoRibassato() { return prezzoRibassato; }
    public void setPrezzoRibassato(Double prezzoRibassato) { this.prezzoRibassato = prezzoRibassato; }

    public Integer getMetriQuadri() { return metriQuadri; }
    public void setMetriQuadri(Integer metriQuadri) { this.metriQuadri = metriQuadri; }

    public String getTipoOperazione() { return tipoOperazione; }
    public void setTipoOperazione(String tipoOperazione) { this.tipoOperazione = tipoOperazione; }

    public String getIndirizzo() { return indirizzo; }
    public void setIndirizzo(String indirizzo) { this.indirizzo = indirizzo; }

    public Double getLatitudine() { return latitudine; }
    public void setLatitudine(Double latitudine) { this.latitudine = latitudine; }

    public Double getLongitudine() { return longitudine; }
    public void setLongitudine(Double longitudine) { this.longitudine = longitudine; }

    public boolean isInAsta() { return inAsta; }
    public void setInAsta(boolean inAsta) { this.inAsta = inAsta; }

    public Long getIdVenditore() { return idVenditore; }
    public void setIdVenditore(Long idVenditore) { this.idVenditore = idVenditore; }

    public Long getIdCategoria() { return idCategoria; }
    public void setIdCategoria(Long idCategoria) { this.idCategoria = idCategoria; }

    public List<String> getFoto() { return foto; }
    public void setFoto(List<String> foto) { this.foto = foto; }

    public List<Recensione> getRecensioni() { return recensioni; }
    public void setRecensioni(List<Recensione> recensioni) { this.recensioni = recensioni; }
}