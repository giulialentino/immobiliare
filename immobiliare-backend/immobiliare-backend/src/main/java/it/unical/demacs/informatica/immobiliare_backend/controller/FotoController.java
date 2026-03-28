package it.unical.demacs.informatica.immobiliare_backend.controller;

import it.unical.demacs.informatica.immobiliare_backend.dao.FotoDao;
import it.unical.demacs.informatica.immobiliare_backend.model.Utente;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.UUID;

@RestController
@RequestMapping("/api/foto")
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
public class FotoController {

    @Autowired
    private FotoDao fotoDao;

    @Value("${upload.dir}")
    private String uploadDir;

    @PostMapping("/upload/{idAnnuncio}")
    public ResponseEntity<?> upload(@PathVariable Long idAnnuncio,
                                    @RequestParam("file") MultipartFile file,
                                    HttpSession session) {
        Utente utente = (Utente) session.getAttribute("utenteLoggato");
        if (utente == null) return ResponseEntity.status(401).body("Non autenticato");

        try {
            // Controlla estensione
            String nomeOriginale = file.getOriginalFilename();
            if (nomeOriginale == null) return ResponseEntity.badRequest().body("File non valido");

            String ext = nomeOriginale.toLowerCase().substring(nomeOriginale.lastIndexOf('.') + 1);
            if (!ext.equals("jpg") && !ext.equals("jpeg") && !ext.equals("png")) {
                return ResponseEntity.badRequest().body("Formato non supportato. Usa JPG o PNG");
            }

            File dir = new File(uploadDir);
            if (!dir.exists()) dir.mkdirs();

            String nomeFile = UUID.randomUUID() + "." + ext;
            Path percorso = Paths.get(uploadDir, nomeFile);
            Files.write(percorso, file.getBytes());

            String url = "http://localhost:8080/uploads/" + nomeFile;
            fotoDao.save(idAnnuncio, url);

            return ResponseEntity.ok(url);
        } catch (IOException | SQLException e) {
            return ResponseEntity.status(500).body("Errore upload: " + e.getMessage());
        }
    }

    @GetMapping("/annuncio/{idAnnuncio}")
    public ResponseEntity<?> getFoto(@PathVariable Long idAnnuncio) {
        try {
            return ResponseEntity.ok(fotoDao.findUrlsByAnnuncio(idAnnuncio));
        } catch (SQLException e) {
            return ResponseEntity.status(500).body("Errore server");
        }
    }
    @DeleteMapping("/{idAnnuncio}")
    @CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
    public ResponseEntity<?> elimina(@PathVariable Long idAnnuncio,
                                     @RequestBody String url,
                                     HttpSession session) {
        Utente utente = (Utente) session.getAttribute("utenteLoggato");
        if (utente == null) return ResponseEntity.status(401).body("Non autenticato");
        try {
            fotoDao.deleteByUrl(idAnnuncio, url);
            return ResponseEntity.ok("Foto eliminata");
        } catch (SQLException e) {
            return ResponseEntity.status(500).body("Errore server: " + e.getMessage());
        }
    }
}