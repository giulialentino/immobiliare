package it.unical.demacs.informatica.immobiliare_backend.controller;

import it.unical.demacs.informatica.immobiliare_backend.model.Utente;
import it.unical.demacs.informatica.immobiliare_backend.service.FotoService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.SQLException;

@RestController
@RequestMapping("/api/foto")
public class FotoController {

    @Autowired
    private FotoService fotoService;

    @GetMapping("/annuncio/{idAnnuncio}")
    public ResponseEntity<?> getFoto(@PathVariable Long idAnnuncio) throws SQLException {
        return ResponseEntity.ok(fotoService.getFotoByAnnuncio(idAnnuncio));
    }

    @DeleteMapping("/{idAnnuncio}")
    public ResponseEntity<?> elimina(@PathVariable Long idAnnuncio,
                                     @RequestBody String url,
                                     HttpSession session) throws SQLException {
        Utente utente = (Utente) session.getAttribute("utenteLoggato");
        if (utente == null) return ResponseEntity.status(401).body("Non autenticato");
        fotoService.eliminaFoto(idAnnuncio, url);
        return ResponseEntity.ok("Foto eliminata");
    }

    @PostMapping("/upload/{idAnnuncio}")
    public ResponseEntity<?> upload(@PathVariable Long idAnnuncio,
                                    @RequestParam("file") MultipartFile file,
                                    HttpSession session) throws SQLException, IOException {
        Utente utente = (Utente) session.getAttribute("utenteLoggato");
        if (utente == null) return ResponseEntity.status(401).body("Non autenticato");
        return ResponseEntity.ok(fotoService.uploadFoto(idAnnuncio, file));
    }
}
