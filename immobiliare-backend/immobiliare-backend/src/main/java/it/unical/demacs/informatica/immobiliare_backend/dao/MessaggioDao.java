package it.unical.demacs.informatica.immobiliare_backend.dao;

import it.unical.demacs.informatica.immobiliare_backend.config.DataSource;
import it.unical.demacs.informatica.immobiliare_backend.model.Messaggio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class MessaggioDao {

    @Autowired
    private DataSource dataSource;
    private boolean perAdmin;
    public boolean isPerAdmin() { return perAdmin; }
    public void setPerAdmin(boolean b) { this.perAdmin = b; }
    // già presente — verifica che ci sia
    private Long idMittente;
    public Long getIdMittente() { return idMittente; }
    public void setIdMittente(Long idMittente) { this.idMittente = idMittente; }

    private Messaggio mapRow(ResultSet rs) throws SQLException {
        Messaggio m = new Messaggio();
        m.setId(rs.getLong("id"));
        m.setIdAnnuncio(rs.getLong("id_annuncio"));
        m.setIdMittente(rs.getLong("id_mittente"));
        m.setOggetto(rs.getString("oggetto"));
        m.setTesto(rs.getString("testo"));
        m.setLetto(rs.getBoolean("letto"));
        m.setEliminatoVenditore(rs.getBoolean("eliminato_venditore"));
        m.setEliminatoAcquirente(rs.getBoolean("eliminato_acquirente"));
        try { m.setPerAdmin(rs.getBoolean("per_admin")); } catch (Exception ignored) {}
        try {
            m.setNomeMittente(rs.getString("nome_mittente"));
            m.setCognomeMittente(rs.getString("cognome_mittente"));
            m.setEmailMittente(rs.getString("email_mittente"));
        } catch (Exception ignored) {}
        return m;
    }

    public List<Messaggio> findByVenditore(Long idVenditore) throws SQLException {
        List<Messaggio> lista = new ArrayList<>();
        String sql = "SELECT m.*, u.nome as nome_mittente, u.cognome as cognome_mittente, u.email as email_mittente " +
                "FROM messaggio m " +
                "JOIN annuncio a ON m.id_annuncio = a.id " +
                "JOIN utente u ON m.id_mittente = u.id " +
                "WHERE a.id_venditore = ? AND m.eliminato_venditore = FALSE AND m.per_admin = FALSE " +
                "ORDER BY m.data_invio DESC";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, idVenditore);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapRow(rs));
            }
        }
        return lista;
    }

    public List<Messaggio> findByMittente(Long idMittente) throws SQLException {
        List<Messaggio> lista = new ArrayList<>();
        String sql = "SELECT m.*, u.nome as nome_mittente, u.cognome as cognome_mittente, u.email as email_mittente " +
                "FROM messaggio m " +
                "JOIN utente u ON m.id_mittente = u.id " +
                "WHERE m.id_mittente = ? AND m.eliminato_acquirente = FALSE AND m.per_admin = FALSE " +
                "ORDER BY m.data_invio DESC";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, idMittente);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapRow(rs));
            }
        }
        return lista;
    }

    public List<Messaggio> findPerAdmin() throws SQLException {
        List<Messaggio> lista = new ArrayList<>();
        String sql = "SELECT m.*, u.nome as nome_mittente, u.cognome as cognome_mittente, u.email as email_mittente, " +
                "COALESCE(rp.stato, 'NESSUNA') as stato_promozione " +
                "FROM messaggio m " +
                "JOIN utente u ON m.id_mittente = u.id " +
                "LEFT JOIN richiesta_promozione rp ON rp.id_utente = m.id_mittente AND m.per_admin = TRUE AND m.id_annuncio IS NULL " +
                "WHERE m.per_admin = TRUE " +
                "ORDER BY m.data_invio DESC";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Messaggio msg = mapRow(rs);
                    try { msg.setStatoPromozione(rs.getString("stato_promozione")); } catch (Exception ignored) {}
                    lista.add(msg);
                }
            }
        }
        return lista;
    }

    public List<Messaggio> findByAnnuncio(Long idAnnuncio) throws SQLException {
        List<Messaggio> lista = new ArrayList<>();
        String sql = "SELECT m.*, u.nome as nome_mittente, u.cognome as cognome_mittente, u.email as email_mittente " +
                "FROM messaggio m " +
                "JOIN utente u ON m.id_mittente = u.id " +
                "WHERE m.id_annuncio = ? AND m.per_admin = FALSE ORDER BY m.data_invio DESC";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, idAnnuncio);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapRow(rs));
            }
        }
        return lista;
    }

    public Messaggio save(Messaggio m) throws SQLException {
        String sql = "INSERT INTO messaggio (id_annuncio, id_mittente, oggetto, testo) VALUES (?, ?, ?, ?) RETURNING id";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, m.getIdAnnuncio());
            ps.setLong(2, m.getIdMittente());
            ps.setString(3, m.getOggetto());
            ps.setString(4, m.getTesto());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) m.setId(rs.getLong("id"));
            }
        }
        return m;
    }

    public void savePerAdmin(Messaggio m) throws SQLException {
        String sql = "INSERT INTO messaggio (id_annuncio, id_mittente, oggetto, testo, per_admin) VALUES (?, ?, ?, ?, TRUE)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            if (m.getIdAnnuncio() == null || m.getIdAnnuncio() == 0) {
                ps.setNull(1, java.sql.Types.BIGINT);
            } else {
                ps.setLong(1, m.getIdAnnuncio());
            }
            ps.setLong(2, m.getIdMittente());
            ps.setString(3, m.getOggetto());
            ps.setString(4, m.getTesto());
            ps.executeUpdate();
        }
    }

    public int countMessaggi(Long idVenditore) throws SQLException {
        String sql = "SELECT COUNT(*) FROM messaggio m " +
                "JOIN annuncio a ON m.id_annuncio = a.id " +
                "WHERE a.id_venditore = ? AND m.letto = FALSE " +
                "AND m.eliminato_venditore = FALSE AND m.per_admin = FALSE";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, idVenditore);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        return 0;
    }

    public int countNonLettiAdmin() throws SQLException {
        String sql = "SELECT COUNT(*) FROM messaggio WHERE per_admin = TRUE AND letto = FALSE";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        return 0;
    }

    public void segnaComeLetto(Long id) throws SQLException {
        String sql = "UPDATE messaggio SET letto = TRUE WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        }
    }

    public void eliminaPerVenditore(Long id) throws SQLException {
        String sql = "UPDATE messaggio SET eliminato_venditore = TRUE WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        }
    }

    public void eliminaPerAcquirente(Long id) throws SQLException {
        String sql = "UPDATE messaggio SET eliminato_acquirente = TRUE WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        }
    }

    public void eliminaTuttiPerVenditore(Long idVenditore) throws SQLException {
        String sql = "UPDATE messaggio SET eliminato_venditore = TRUE " +
                "WHERE id_annuncio IN (SELECT id FROM annuncio WHERE id_venditore = ?) AND per_admin = FALSE";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, idVenditore);
            ps.executeUpdate();
        }
    }

    public void eliminaTuttiPerAcquirente(Long idMittente) throws SQLException {
        String sql = "UPDATE messaggio SET eliminato_acquirente = TRUE WHERE id_mittente = ? AND per_admin = FALSE";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, idMittente);
            ps.executeUpdate();
        }
    }
    public void savePerVenditore(Messaggio m, Long idVenditore) throws SQLException {
            String sql = "INSERT INTO messaggio (id_annuncio, id_mittente, oggetto, testo) VALUES (?, ?, ?, ?)";
            try (Connection conn = dataSource.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                if (m.getIdAnnuncio() == null || m.getIdAnnuncio() == 0) {
                    ps.setNull(1, java.sql.Types.BIGINT);
                } else {
                    ps.setLong(1, m.getIdAnnuncio());
                }
                ps.setLong(2, m.getIdMittente());
                ps.setString(3, m.getOggetto());
                ps.setString(4, m.getTesto());
                ps.executeUpdate();
            }
        }
    public void saveNotificaUtente(Messaggio m, Long idDestinatario) throws SQLException {
        String sql = "INSERT INTO messaggio (id_annuncio, id_mittente, oggetto, testo) VALUES (?, ?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            if (m.getIdAnnuncio() == null || m.getIdAnnuncio() == 0) {
                ps.setNull(1, java.sql.Types.BIGINT);
            } else {
                ps.setLong(1, m.getIdAnnuncio());
            }
            ps.setLong(2, m.getIdMittente());
            ps.setString(3, m.getOggetto());
            ps.setString(4, m.getTesto());
            ps.executeUpdate();
        }
    }
}