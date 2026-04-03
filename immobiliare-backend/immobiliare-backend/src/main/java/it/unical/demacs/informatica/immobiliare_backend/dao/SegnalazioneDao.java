package it.unical.demacs.informatica.immobiliare_backend.dao;

import it.unical.demacs.informatica.immobiliare_backend.config.DataSource;
import it.unical.demacs.informatica.immobiliare_backend.model.Segnalazione;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class SegnalazioneDao {

    @Autowired
    private DataSource dataSource;

    private Segnalazione mapRow(ResultSet rs) throws SQLException {
        Segnalazione s = new Segnalazione();
        s.setId(rs.getLong("id"));
        s.setIdAnnuncio(rs.getLong("id_annuncio"));
        s.setIdSegnalante(rs.getLong("id_segnalante"));
        s.setMotivo(rs.getString("motivo"));
        s.setCategoria(rs.getString("categoria"));
        s.setStato(rs.getString("stato"));
        Timestamp ts = rs.getTimestamp("data_inserimento");
        if (ts != null) s.setDataInserimento(ts.toLocalDateTime());
        try { s.setNomeSegnalante(rs.getString("nome_segnalante")); } catch (Exception ignored) {}
        try { s.setCognomeSegnalante(rs.getString("cognome_segnalante")); } catch (Exception ignored) {}
        try { s.setTitoloAnnuncio(rs.getString("titolo_annuncio")); } catch (Exception ignored) {}
        return s;
    }

    public void save(Segnalazione s) throws SQLException {
        String sql = "INSERT INTO segnalazione (id_annuncio, id_segnalante, motivo, categoria, stato) " +
                "VALUES (?, ?, ?, ?, 'IN_ATTESA')";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, s.getIdAnnuncio());
            ps.setLong(2, s.getIdSegnalante());
            ps.setString(3, s.getMotivo());
            ps.setString(4, s.getCategoria());
            ps.executeUpdate();
        }
    }

    public List<Segnalazione> findAll() throws SQLException {
        List<Segnalazione> lista = new ArrayList<>();
        String sql = """
            SELECT s.*, 
                   u.nome AS nome_segnalante, u.cognome AS cognome_segnalante,
                   a.titolo AS titolo_annuncio
            FROM segnalazione s
            LEFT JOIN utente u ON s.id_segnalante = u.id
            LEFT JOIN annuncio a ON s.id_annuncio = a.id
            ORDER BY s.data_inserimento DESC
            """;
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) lista.add(mapRow(rs));
        }
        return lista;
    }

    public void aggiornaStato(Long id, String stato) throws SQLException {
        String sql = "UPDATE segnalazione SET stato = ? WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, stato);
            ps.setLong(2, id);
            ps.executeUpdate();
        }
    }

    public boolean esisteSegnalazione(Long idAnnuncio, Long idSegnalante) throws SQLException {
        String sql = "SELECT COUNT(*) FROM segnalazione WHERE id_annuncio = ? AND id_segnalante = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, idAnnuncio);
            ps.setLong(2, idSegnalante);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1) > 0;
            }
        }
        return false;
    }
}