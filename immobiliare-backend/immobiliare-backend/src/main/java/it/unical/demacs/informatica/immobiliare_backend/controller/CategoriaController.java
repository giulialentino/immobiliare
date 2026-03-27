package it.unical.demacs.informatica.immobiliare_backend.controller;

import it.unical.demacs.informatica.immobiliare_backend.dao.CategoriaDao;
import it.unical.demacs.informatica.immobiliare_backend.model.Categoria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.List;

@RestController
@RequestMapping("/api/categorie")
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
public class CategoriaController {

    @Autowired
    private CategoriaDao categoriaDao;

    @GetMapping
    public ResponseEntity<?> getAll() {
        try {
            List<Categoria> lista = categoriaDao.findAll();
            return ResponseEntity.ok(lista);
        } catch (SQLException e) {
            return ResponseEntity.status(500).body("Errore server");
        }
    }
}