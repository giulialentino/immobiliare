package it.unical.demacs.informatica.immobiliare_backend.dao;

import it.unical.demacs.informatica.immobiliare_backend.config.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.*;

@Repository
public class RichiestaPromozioneDao {

    @Autowired
    private DataSource dataSource;

    public void save(Long idUtente) throws SQLException {
        String sql = "INSERT INTO richiesta_promozione (id_utente) VALUES (?) " +
                "ON CONFLICT (id_utente) DO UPDATE SET stato = 'IN_ATTESA', data_richiesta = NOW()";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, idUtente);
            ps.executeUpdate();
        }
    }

    public String getStato(Long idUtente) throws SQLException {
        String sql = "SELECT stato FROM richiesta_promozione WHERE id_utente = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, idUtente);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getString("stato");
            }
        }
        return null;
    }

    public boolean exists(Long idUtente) throws SQLException {
        String sql = "SELECT COUNT(*) FROM richiesta_promozione WHERE id_utente = ? AND stato = 'IN_ATTESA'";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, idUtente);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1) > 0;
            }
        }
        return false;
    }

    public void aggiornaStato(Long idUtente, String stato) throws SQLException {
        String sql = "UPDATE richiesta_promozione SET stato = ? WHERE id_utente = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, stato);
            ps.setLong(2, idUtente);
            ps.executeUpdate();
        }
    }
}