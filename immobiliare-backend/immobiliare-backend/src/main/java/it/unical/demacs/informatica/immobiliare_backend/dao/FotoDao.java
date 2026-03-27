package it.unical.demacs.informatica.immobiliare_backend.dao;

import it.unical.demacs.informatica.immobiliare_backend.config.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class FotoDao {

    @Autowired
    private DataSource dataSource;
    private FotoDao fotoDao;

    public List<String> findUrlsByAnnuncio(Long idAnnuncio) throws SQLException {
        List<String> urls = new ArrayList<>();
        String sql = "SELECT url FROM foto WHERE id_annuncio = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, idAnnuncio);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) urls.add(rs.getString("url"));
            }
        }
        return urls;
    }

    public void save(Long idAnnuncio, String url) throws SQLException {
        String sql = "INSERT INTO foto (id_annuncio, url) VALUES (?, ?)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, idAnnuncio);
            ps.setString(2, url);
            ps.executeUpdate();
        }
    }

    public void deleteByAnnuncio(Long idAnnuncio) throws SQLException {
        String sql = "DELETE FROM foto WHERE id_annuncio = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, idAnnuncio);
            ps.executeUpdate();
        }
    }
    public void deleteByUrl(Long idAnnuncio, String url) throws SQLException {
        String sql = "DELETE FROM foto WHERE id_annuncio = ? AND url = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, idAnnuncio);
            ps.setString(2, url);
            ps.executeUpdate();
        }
    }
}