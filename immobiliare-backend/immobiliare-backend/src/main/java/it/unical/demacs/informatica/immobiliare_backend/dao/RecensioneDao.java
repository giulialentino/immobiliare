package it.unical.demacs.informatica.immobiliare_backend.dao;

import it.unical.demacs.informatica.immobiliare_backend.config.DataSource;
import it.unical.demacs.informatica.immobiliare_backend.model.Recensione;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class RecensioneDao {

    @Autowired
    private DataSource dataSource;

    private Recensione mapRow(ResultSet rs) throws SQLException {
        Recensione r = new Recensione();
        r.setId(rs.getLong("id"));
        r.setIdAnnuncio(rs.getLong("id_annuncio"));
        r.setIdUtente(rs.getLong("id_utente"));
        r.setPunteggio(rs.getInt("punteggio"));
        r.setCommento(rs.getString("commento"));
        return r;
    }

    public List<Recensione> findByAnnuncio(Long idAnnuncio) throws SQLException {
        List<Recensione> lista = new ArrayList<>();
        String sql = "SELECT * FROM recensione WHERE id_annuncio = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, idAnnuncio);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapRow(rs));
            }
        }
        return lista;
    }

    public Recensione save(Recensione r) throws SQLException {
        String sql = "INSERT INTO recensione (id_annuncio, id_utente, punteggio, commento) " +
                "VALUES (?, ?, ?, ?) RETURNING id";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, r.getIdAnnuncio());
            ps.setLong(2, r.getIdUtente());
            ps.setInt(3, r.getPunteggio());
            ps.setString(4, r.getCommento());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) r.setId(rs.getLong("id"));
            }
        }
        return r;
    }

    // Verifica se l'utente ha già inviato almeno un messaggio per questo annuncio.
    // Usato come condizione di "visita" prima di permettere una recensione: solo chi
    // ha manifestato interesse contattando il venditore può recensire l'immobile.
    public boolean haContattatoVenditore(Long idAnnuncio, Long idUtente) throws SQLException {
        String sql = "SELECT COUNT(*) FROM messaggio WHERE id_annuncio = ? AND id_mittente = ? AND per_admin = false";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, idAnnuncio);
            ps.setLong(2, idUtente);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1) > 0;
            }
        }
        return false;
    }
}