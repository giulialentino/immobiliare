package it.unical.demacs.informatica.immobiliare_backend.dao;

import it.unical.demacs.informatica.immobiliare_backend.config.DataSource;
import it.unical.demacs.informatica.immobiliare_backend.model.Annuncio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class AnnuncioDao {

    @Autowired
    private DataSource dataSource;

    private Annuncio mapRow(ResultSet rs) throws SQLException {
        Annuncio a = new Annuncio();
        try { a.setStato(rs.getString("stato")); } catch (Exception ignored) {}
        a.setId(rs.getLong("id"));
        a.setTitolo(rs.getString("titolo"));
        a.setDescrizione(rs.getString("descrizione"));
        a.setPrezzo(rs.getDouble("prezzo"));
        Object prezzoRib = rs.getObject("prezzo_ribassato");
        if (prezzoRib != null) {
            a.setPrezzoRibassato(((java.math.BigDecimal) prezzoRib).doubleValue());
        }
        a.setMetriQuadri(rs.getInt("metri_quadri"));
        a.setTipoOperazione(rs.getString("tipo_operazione"));
        a.setIndirizzo(rs.getString("indirizzo"));
        a.setLatitudine(rs.getDouble("latitudine"));
        a.setLongitudine(rs.getDouble("longitudine"));
        a.setInAsta(rs.getBoolean("in_asta"));
        a.setIdVenditore(rs.getLong("id_venditore"));
        a.setIdCategoria(rs.getLong("id_categoria"));
        return a;
    }

    public List<Annuncio> findAll() throws SQLException {
        List<Annuncio> lista = new ArrayList<>();
        String sql = "SELECT * FROM annuncio";
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) lista.add(mapRow(rs));
        }
        return lista;
    }

    public List<Annuncio> findByFiltri(String tipoOperazione, Long idCategoria) throws SQLException {
        List<Annuncio> lista = new ArrayList<>();
        String sql = "SELECT * FROM annuncio WHERE 1=1 AND stato='APPROVATO'";
        if (tipoOperazione != null) sql += " AND tipo_operazione = ?";
        if (idCategoria != null) sql += " AND id_categoria = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            int i = 1;
            if (tipoOperazione != null) ps.setString(i++, tipoOperazione);
            if (idCategoria != null) ps.setLong(i++, idCategoria);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapRow(rs));
            }
        }
        return lista;
    }

    public Annuncio findById(Long id) throws SQLException {
        String sql = "SELECT * FROM annuncio WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        }
        return null;
    }

    public List<Annuncio> findByVenditore(Long idVenditore) throws SQLException {
        List<Annuncio> lista = new ArrayList<>();
        String sql = "SELECT * FROM annuncio WHERE id_venditore = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, idVenditore);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapRow(rs));
            }
        }
        return lista;
    }

    public Annuncio save(Annuncio a) throws SQLException {
        String sql = "INSERT INTO annuncio (titolo, descrizione, prezzo, prezzo_ribassato, " +
                "metri_quadri, tipo_operazione, indirizzo, latitudine, longitudine, " +
                "in_asta, id_venditore, id_categoria, stato) " +
                "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,'IN_ATTESA') RETURNING id";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, a.getTitolo());
            ps.setString(2, a.getDescrizione());
            ps.setDouble(3, a.getPrezzo());
            ps.setObject(4, a.getPrezzoRibassato());
            ps.setInt(5, a.getMetriQuadri());
            ps.setString(6, a.getTipoOperazione());
            ps.setString(7, a.getIndirizzo());
            ps.setDouble(8, a.getLatitudine());
            ps.setDouble(9, a.getLongitudine());
            ps.setBoolean(10, a.isInAsta());
            ps.setLong(11, a.getIdVenditore());
            ps.setLong(12, a.getIdCategoria());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) a.setId(rs.getLong("id"));
            }
        }
        return a;
    }


    public void update(Annuncio a) throws SQLException {
        String sql = "UPDATE annuncio SET titolo=?, descrizione=?, prezzo=?, " +
                "prezzo_ribassato=?, metri_quadri=?, tipo_operazione=?, " +
                "indirizzo=?, latitudine=?, longitudine=?, in_asta=?, id_categoria=? " +
                "WHERE id=?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, a.getTitolo());
            ps.setString(2, a.getDescrizione());
            ps.setDouble(3, a.getPrezzo());
            ps.setObject(4, a.getPrezzoRibassato());
            ps.setInt(5, a.getMetriQuadri());
            ps.setString(6, a.getTipoOperazione());
            ps.setString(7, a.getIndirizzo());
            ps.setDouble(8, a.getLatitudine());
            ps.setDouble(9, a.getLongitudine());
            ps.setBoolean(10, a.isInAsta());
            ps.setLong(11, a.getIdCategoria());
            ps.setLong(12, a.getId());
            ps.executeUpdate();
        }
    }

    public void delete(Long id) throws SQLException {
        String sql = "DELETE FROM annuncio WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        }
    }

    public void ribassaPrezzo(Long id, Double nuovoPrezzo) throws SQLException {
        String sql = "UPDATE annuncio SET prezzo_ribassato = ? WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, nuovoPrezzo);
            ps.setLong(2, id);
            ps.executeUpdate();
        }
    }
    public void annullaRibasso(Long id) throws SQLException {
        String sql = "UPDATE annuncio SET prezzo_ribassato = NULL WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        }
    }
    public void aggiornaStato(Long id, String stato) throws SQLException {
        String sql = "UPDATE annuncio SET stato = ? WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, stato);
            ps.setLong(2, id);
            ps.executeUpdate();
        }
    }

    public List<Annuncio> findInAttesa() throws SQLException {
        List<Annuncio> lista = new ArrayList<>();
        String sql = "SELECT * FROM annuncio WHERE stato = 'IN_ATTESA' ORDER BY data_inserimento DESC";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapRow(rs));
            }
        }
        return lista;
    }
    public int countInAttesa() throws SQLException {
        String sql = "SELECT COUNT(*) FROM annuncio WHERE stato = 'IN_ATTESA'";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        return 0;
    }
}