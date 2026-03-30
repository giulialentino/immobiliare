package it.unical.demacs.informatica.immobiliare_backend.model;

public class Utente {
    private Long id;
    private String nome;
    private String cognome;
    private String email;
    private String password;
    private String ruolo;
    private boolean bannato;
    private String fotoProfilo;
    private boolean emailVerificata;
    private String tokenVerifica;
    private String tokenReset;
    private java.time.LocalDateTime tokenResetScadenza;

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

    public String getFotoProfilo() { return fotoProfilo; }
    public void setFotoProfilo(String fotoProfilo) { this.fotoProfilo = fotoProfilo; }

    public boolean isEmailVerificata() { return emailVerificata; }
    public void setEmailVerificata(boolean emailVerificata) { this.emailVerificata = emailVerificata; }

    public String getTokenVerifica() { return tokenVerifica; }
    public void setTokenVerifica(String tokenVerifica) { this.tokenVerifica = tokenVerifica; }

    public String getTokenReset() { return tokenReset; }
    public void setTokenReset(String tokenReset) { this.tokenReset = tokenReset; }

    public java.time.LocalDateTime getTokenResetScadenza() { return tokenResetScadenza; }
    public void setTokenResetScadenza(java.time.LocalDateTime tokenResetScadenza) { this.tokenResetScadenza = tokenResetScadenza; }
}