package it.unical.demacs.informatica.immobiliare_backend.dao;

import it.unical.demacs.informatica.immobiliare_backend.config.DataSource;
import it.unical.demacs.informatica.immobiliare_backend.model.Utente;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class UtenteDao {

    @Autowired
    private DataSource dataSource;

    // Converte una riga del ResultSet in un oggetto Utente
    private Utente mapRow(ResultSet rs) throws SQLException {
        Utente u = new Utente();
        u.setId(rs.getLong("id"));
        u.setNome(rs.getString("nome"));
        u.setCognome(rs.getString("cognome"));
        u.setEmail(rs.getString("email"));
        u.setPassword(rs.getString("password"));
        u.setRuolo(rs.getString("ruolo"));
        u.setBannato(rs.getBoolean("bannato"));
        try { u.setFotoProfilo(rs.getString("foto_profilo")); } catch (Exception ignored) {}
        return u;
    }




    public Utente findByEmail(String email) throws SQLException {
        String sql = "SELECT * FROM utente WHERE email = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        }
        return null;
    }

    public Utente save(Utente u) throws SQLException {
        String sql = "INSERT INTO utente (nome, cognome, email, password, ruolo, bannato) " +
                "VALUES (?, ?, ?, ?, ?, ?) RETURNING id";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, u.getNome());
            ps.setString(2, u.getCognome());
            ps.setString(3, u.getEmail());
            ps.setString(4, u.getPassword());
            ps.setString(5, u.getRuolo());
            ps.setBoolean(6, u.isBannato());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) u.setId(rs.getLong("id"));
            }
        }
        return u;
    }

    public void setBannato(Long id, boolean bannato) throws SQLException {
        String sql = "UPDATE utente SET bannato = ? WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBoolean(1, bannato);
            ps.setLong(2, id);
            ps.executeUpdate();
        }
    }

    public void setRuolo(Long id, String ruolo) throws SQLException {
        String sql = "UPDATE utente SET ruolo = ? WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, ruolo);
            ps.setLong(2, id);
            ps.executeUpdate();
        }
    }
    public void aggiornaRuolo(Long id, String ruolo) throws SQLException {
        String sql = "UPDATE utente SET ruolo = ? WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, ruolo);
            ps.setLong(2, id);
            ps.executeUpdate();
        }
    }

    public Utente findById(Long id) throws SQLException {
        String sql = "SELECT * FROM utente WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        }
        return null;
    }
    public void aggiornaPassword(Long id, String nuovaPassword) throws SQLException {
        String sql = "UPDATE utente SET password = ? WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nuovaPassword);
            ps.setLong(2, id);
            ps.executeUpdate();
        }
    }
    public List<Utente> findAll() throws SQLException {
        List<Utente> lista = new ArrayList<>();
        String sql = "SELECT * FROM utente";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapRow(rs));
            }
        }
        return lista;
    }
    public void aggiornaFotoProfilo(Long id, String url) throws SQLException {
        String sql = "UPDATE utente SET foto_profilo = ? WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            if (url == null) {
                ps.setNull(1, java.sql.Types.VARCHAR);
            } else {
                ps.setString(1, url);
            }
            ps.setLong(2, id);
            ps.executeUpdate();
        }
    }
}