package it.unical.demacs.informatica.immobiliare_backend.dao;

import it.unical.demacs.informatica.immobiliare_backend.config.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class PreferitoDao {

    @Autowired
    private DataSource dataSource;

    public List<Long> findIdAnnunciByUtente(Long idUtente) throws SQLException {
        List<Long> ids = new ArrayList<>();
        String sql = "SELECT id_annuncio FROM preferito WHERE id_utente = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, idUtente);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) ids.add(rs.getLong("id_annuncio"));
            }
        }
        return ids;
    }

    public boolean isPreferito(Long idUtente, Long idAnnuncio) throws SQLException {
        String sql = "SELECT COUNT(*) FROM preferito WHERE id_utente = ? AND id_annuncio = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, idUtente);
            ps.setLong(2, idAnnuncio);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1) > 0;
            }
        }
        return false;
    }

    public void aggiungi(Long idUtente, Long idAnnuncio) throws SQLException {
        String sql = "INSERT INTO preferito (id_utente, id_annuncio) VALUES (?, ?) ON CONFLICT DO NOTHING";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, idUtente);
            ps.setLong(2, idAnnuncio);
            ps.executeUpdate();
        }
    }

    public void rimuovi(Long idUtente, Long idAnnuncio) throws SQLException {
        String sql = "DELETE FROM preferito WHERE id_utente = ? AND id_annuncio = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, idUtente);
            ps.setLong(2, idAnnuncio);
            ps.executeUpdate();
        }
    }
}