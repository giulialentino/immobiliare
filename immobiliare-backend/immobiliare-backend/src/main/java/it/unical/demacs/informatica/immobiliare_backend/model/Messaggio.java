package it.unical.demacs.informatica.immobiliare_backend.model;

public class Messaggio {
    private Long id;
    private Long idAnnuncio;
    private Long idMittente;
    private String oggetto;
    private String testo;
    private boolean letto;
    private boolean eliminatoVenditore;
    private boolean eliminatoAcquirente;
    private String nomeMittente;
    private String cognomeMittente;
    private String emailMittente;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getIdAnnuncio() { return idAnnuncio; }
    public void setIdAnnuncio(Long idAnnuncio) { this.idAnnuncio = idAnnuncio; }
    public Long getIdMittente() { return idMittente; }
    public void setIdMittente(Long idMittente) { this.idMittente = idMittente; }
    public String getOggetto() { return oggetto; }
    public void setOggetto(String oggetto) { this.oggetto = oggetto; }
    public String getTesto() { return testo; }
    public void setTesto(String testo) { this.testo = testo; }
    public boolean isLetto() { return letto; }
    public void setLetto(boolean letto) { this.letto = letto; }
    public boolean isEliminatoVenditore() { return eliminatoVenditore; }
    public void setEliminatoVenditore(boolean b) { this.eliminatoVenditore = b; }
    public boolean isEliminatoAcquirente() { return eliminatoAcquirente; }
    public void setEliminatoAcquirente(boolean b) { this.eliminatoAcquirente = b; }
    public String getNomeMittente() { return nomeMittente; }
    public void setNomeMittente(String n) { this.nomeMittente = n; }
    public String getCognomeMittente() { return cognomeMittente; }
    public void setCognomeMittente(String c) { this.cognomeMittente = c; }
    public String getEmailMittente() { return emailMittente; }
    public void setEmailMittente(String e) { this.emailMittente = e; }
}