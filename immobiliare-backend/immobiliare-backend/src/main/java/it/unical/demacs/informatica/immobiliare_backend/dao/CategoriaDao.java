package it.unical.demacs.informatica.immobiliare_backend.dao;

import it.unical.demacs.informatica.immobiliare_backend.config.DataSource;
import it.unical.demacs.informatica.immobiliare_backend.model.Categoria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class CategoriaDao {

    @Autowired
    private DataSource dataSource;

    private Categoria mapRow(ResultSet rs) throws SQLException {
        Categoria c = new Categoria();
        c.setId(rs.getLong("id"));
        c.setNome(rs.getString("nome"));
        return c;
    }

    public List<Categoria> findAll() throws SQLException {
        List<Categoria> lista = new ArrayList<>();
        String sql = "SELECT * FROM categoria";
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) lista.add(mapRow(rs));
        }
        return lista;
    }

    public Categoria findById(Long id) throws SQLException {
        String sql = "SELECT * FROM categoria WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        }
        return null;
    }
}