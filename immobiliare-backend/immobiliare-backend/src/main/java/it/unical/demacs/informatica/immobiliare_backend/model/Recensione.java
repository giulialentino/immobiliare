package it.unical.demacs.informatica.immobiliare_backend.model;

public class Recensione {
    private Long id;
    private Long idAnnuncio;
    private Long idUtente;
    private Integer punteggio;
    private String commento;

    public Recensione() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getIdAnnuncio() { return idAnnuncio; }
    public void setIdAnnuncio(Long idAnnuncio) { this.idAnnuncio = idAnnuncio; }

    public Long getIdUtente() { return idUtente; }
    public void setIdUtente(Long idUtente) { this.idUtente = idUtente; }

    public Integer getPunteggio() { return punteggio; }
    public void setPunteggio(Integer punteggio) { this.punteggio = punteggio; }

    public String getCommento() { return commento; }
    public void setCommento(String commento) { this.commento = commento; }
}