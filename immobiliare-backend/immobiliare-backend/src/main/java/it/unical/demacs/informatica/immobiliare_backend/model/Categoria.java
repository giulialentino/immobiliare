package it.unical.demacs.informatica.immobiliare_backend.model;

public class Categoria {
    private Long id;
    private String nome;

    public Categoria() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
}