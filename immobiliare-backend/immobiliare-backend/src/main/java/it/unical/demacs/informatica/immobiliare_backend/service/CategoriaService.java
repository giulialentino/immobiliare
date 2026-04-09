package it.unical.demacs.informatica.immobiliare_backend.service;

import it.unical.demacs.informatica.immobiliare_backend.dao.CategoriaDao;
import it.unical.demacs.informatica.immobiliare_backend.model.Categoria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;

@Service
public class CategoriaService {

    @Autowired
    private CategoriaDao categoriaDao;

    public List<Categoria> getAll() throws SQLException {
        return categoriaDao.findAll();
    }
}
