package it.unical.demacs.informatica.immobiliare_backend.dao;

import it.unical.demacs.informatica.immobiliare_backend.config.DataSource;
import it.unical.demacs.informatica.immobiliare_backend.model.Asta;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class AstaDao {

    @Autowired
    private DataSource dataSource;

    private Asta mapRow(ResultSet rs) throws SQLException {
        Asta a = new Asta();
        a.setId(rs.getLong("id"));
        a.setIdAnnuncio(rs.getLong("id_annuncio"));
        Object prezzoBase = rs.getObject("prezzo_base");
        if (prezzoBase != null) a.setPrezzoBase(((java.math.BigDecimal) prezzoBase).doubleValue());
        Object offertaMax = rs.getObject("offerta_max");
        if (offertaMax != null) a.setOffertaMax(((java.math.BigDecimal) offertaMax).doubleValue());
        a.setIdOfferente(rs.getLong("id_offerente"));
        Timestamp ts = rs.getTimestamp("data_scadenza");
        if (ts != null) a.setDataScadenza(ts.toLocalDateTime());
        a.setAttiva(rs.getBoolean("attiva"));
        return a;
    }

    public Asta findByAnnuncio(Long idAnnuncio) throws SQLException {
        String sql = "SELECT * FROM asta WHERE id_annuncio = ? AND attiva = true";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, idAnnuncio);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        }
        return null;
    }

    public Asta save(Asta asta) throws SQLException {
        String sql = "INSERT INTO asta (id_annuncio, prezzo_base, data_scadenza, attiva) " +
                "VALUES (?, ?, ?, true) RETURNING id";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, asta.getIdAnnuncio());
            ps.setDouble(2, asta.getPrezzoBase());
            ps.setTimestamp(3, Timestamp.valueOf(asta.getDataScadenza()));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) asta.setId(rs.getLong("id"));
            }
        }
        return asta;
    }

    public void aggiornaOfferta(Long idAsta, Double importo, Long idOfferente) throws SQLException {
        String sql = "UPDATE asta SET offerta_max = ?, id_offerente = ? WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, importo);
            ps.setLong(2, idOfferente);
            ps.setLong(3, idAsta);
            ps.executeUpdate();
        }
    }

    public void chiudi(Long idAsta) throws SQLException {
        String sql = "UPDATE asta SET attiva = false WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, idAsta);
            ps.executeUpdate();
        }
    }
    public Asta findById(Long idAsta) throws SQLException {
        String sql = "SELECT * FROM asta WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, idAsta);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        }
        return null;
    }
}