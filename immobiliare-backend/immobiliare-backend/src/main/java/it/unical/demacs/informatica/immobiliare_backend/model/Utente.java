package it.unical.demacs.informatica.immobiliare_backend.model;

public class Utente {
    private Long id;
    private String nome;
    private String cognome;
    private String email;
    private String password;
    private String ruolo; // "AMMINISTRATORE", "VENDITORE", "ACQUIRENTE"
    private boolean bannato;

    public Utente() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getCognome() { return cognome; }
    public void setCognome(String cognome) { this.cognome = cognome; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRuolo() { return ruolo; }
    public void setRuolo(String ruolo) { this.ruolo = ruolo; }

    public boolean isBannato() { return bannato; }
    public void setBannato(boolean bannato) { this.bannato = bannato; }
}