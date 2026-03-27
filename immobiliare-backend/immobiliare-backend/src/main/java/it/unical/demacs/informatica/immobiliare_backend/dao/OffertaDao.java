package it.unical.demacs.informatica.immobiliare_backend.dao;

import it.unical.demacs.informatica.immobiliare_backend.config.DataSource;
import it.unical.demacs.informatica.immobiliare_backend.model.Offerta;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class OffertaDao {

    @Autowired
    private DataSource dataSource;

    private Offerta mapRow(ResultSet rs) throws SQLException {
        Offerta o = new Offerta();
        o.setId(rs.getLong("id"));
        o.setIdAsta(rs.getLong("id_asta"));
        o.setIdUtente(rs.getLong("id_utente"));
        Object importo = rs.getObject("importo");
        if (importo != null) o.setImporto(((java.math.BigDecimal) importo).doubleValue());
        Timestamp ts = rs.getTimestamp("data_offerta");
        if (ts != null) o.setDataOfferta(ts.toLocalDateTime());
        return o;
    }

    public List<Offerta> findByAsta(Long idAsta) throws SQLException {
        List<Offerta> lista = new ArrayList<>();
        String sql = "SELECT * FROM offerta WHERE id_asta = ? ORDER BY importo DESC";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, idAsta);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapRow(rs));
            }
        }
        return lista;
    }

    public Offerta save(Offerta o) throws SQLException {
        String sql = "INSERT INTO offerta (id_asta, id_utente, importo) " +
                "VALUES (?, ?, ?) RETURNING id";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, o.getIdAsta());
            ps.setLong(2, o.getIdUtente());
            ps.setDouble(3, o.getImporto());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) o.setId(rs.getLong("id"));
            }
        }
        return o;
    }
}