package it.unical.demacs.informatica.immobiliare_backend.controller;

import it.unical.demacs.informatica.immobiliare_backend.config.PasswordUtil;
import it.unical.demacs.informatica.immobiliare_backend.dao.UtenteDao;
import it.unical.demacs.informatica.immobiliare_backend.model.Utente;
import it.unical.demacs.informatica.immobiliare_backend.service.EmailService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
public class AuthController {

    @Value("${upload.dir}")
    private String uploadDir;

    @Autowired
    private UtenteDao utenteDao;

    @Autowired
    private PasswordUtil passwordUtil;

    @Autowired
    private EmailService emailService;

    // ── Metodo di utilità — pulisce i dati sensibili e aggiorna la sessione ──
    private void aggiornaSessione(HttpSession session, Utente utente) {
        utente.setPassword(null);
        utente.setTokenVerifica(null);
        utente.setTokenReset(null);
        utente.setTokenResetScadenza(null);
        session.setAttribute("utenteLoggato", utente);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Utente credenziali, HttpSession session) {
        try {
            Utente utente = utenteDao.findByEmail(credenziali.getEmail());
            if (utente == null) {
                return ResponseEntity.status(401).body("Credenziali errate");
            }
            if (utente.isBannato()) {
                return ResponseEntity.status(403).body("Utente bannato");
            }
            if (!utente.isEmailVerificata()) {
                return ResponseEntity.status(403).body("EMAIL_NON_VERIFICATA");
            }
            boolean passwordOk = passwordUtil.verifica(credenziali.getPassword(), utente.getPassword());
            if (!passwordOk) {
                return ResponseEntity.status(401).body("Credenziali errate");
            }
            aggiornaSessione(session, utente);
            return ResponseEntity.ok(utente);
        } catch (SQLException e) {
            return ResponseEntity.status(500).body("Errore server");
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok("Logout effettuato");
    }

    @GetMapping("/me")
    public ResponseEntity<?> getUtenteLoggato(HttpSession session) {
        Utente utente = (Utente) session.getAttribute("utenteLoggato");
        if (utente == null) {
            return ResponseEntity.status(401).body("Non autenticato");
        }
        return ResponseEntity.ok(utente);
    }

    @GetMapping("/utente/{id}")
    public ResponseEntity<?> getUtenteById(@PathVariable Long id) {
        try {
            Utente utente = utenteDao.findById(id);
            if (utente == null) return ResponseEntity.notFound().build();
            utente.setPassword(null);
            utente.setTokenVerifica(null);
            utente.setTokenReset(null);
            utente.setTokenResetScadenza(null);
            return ResponseEntity.ok(utente);
        } catch (SQLException e) {
            return ResponseEntity.status(500).body("Errore server");
        }
    }

    @PostMapping("/registra")
    public ResponseEntity<?> registra(@RequestBody Utente nuovo) {
        try {
            if (nuovo.getEmail() == null || !nuovo.getEmail().matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
                return ResponseEntity.badRequest().body("Email non valida");
            }
            if (nuovo.getPassword() == null || nuovo.getPassword().length() < 8) {
                return ResponseEntity.badRequest().body("Password troppo corta");
            }
            Utente esistente = utenteDao.findByEmail(nuovo.getEmail());
            if (esistente != null) {
                return ResponseEntity.status(400).body("Email già registrata");
            }
            nuovo.setPassword(passwordUtil.cifra(nuovo.getPassword()));
            if (nuovo.getRuolo() == null || nuovo.getRuolo().isBlank()) {
                nuovo.setRuolo("ACQUIRENTE");
            }
            if (!nuovo.getRuolo().equals("ACQUIRENTE") && !nuovo.getRuolo().equals("VENDITORE")) {
                nuovo.setRuolo("ACQUIRENTE");
            }
            nuovo.setBannato(false);
            nuovo.setEmailVerificata(false);
            Utente salvato = utenteDao.save(nuovo);

            String token = UUID.randomUUID().toString();
            utenteDao.salvaTokenVerifica(salvato.getId(), token);
            emailService.inviaVerificaEmail(salvato.getEmail(), token);

            salvato.setPassword(null);
            return ResponseEntity.ok("Registrazione avvenuta! Controlla la tua email per verificare l'account.");
        } catch (SQLException e) {
            return ResponseEntity.status(500).body("Errore server");
        }
    }

    @GetMapping("/verifica-email")
    public ResponseEntity<?> verificaEmail(@RequestParam String token) {
        try {
            Utente utente = utenteDao.findByTokenVerifica(token);
            if (utente == null) {
                return ResponseEntity.status(400).body("Token non valido o già utilizzato");
            }
            utenteDao.verificaEmail(utente.getId());
            return ResponseEntity.ok("Email verificata con successo!");
        } catch (SQLException e) {
            return ResponseEntity.status(500).body("Errore server");
        }
    }

    @PostMapping("/recupera-password")
    public ResponseEntity<?> recuperaPassword(@RequestBody Map<String, String> body) {
        try {
            String email = body.get("email");
            if (email == null || email.isBlank()) {
                return ResponseEntity.badRequest().body("Email mancante");
            }
            Utente utente = utenteDao.findByEmail(email);
            if (utente != null) {
                String token = UUID.randomUUID().toString();
                java.time.LocalDateTime scadenza = java.time.LocalDateTime.now().plusHours(1);
                utenteDao.salvaTokenReset(utente.getId(), token, scadenza);
                emailService.inviaResetPassword(utente.getEmail(), token);
            }
            return ResponseEntity.ok("Se l'email è registrata, riceverai le istruzioni.");
        } catch (SQLException e) {
            return ResponseEntity.status(500).body("Errore server");
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> body) {
        try {
            String token = body.get("token");
            String nuovaPassword = body.get("nuovaPassword");

            if (token == null || token.isBlank()) {
                return ResponseEntity.badRequest().body("Token mancante");
            }
            Utente utente = utenteDao.findByTokenReset(token);
            if (utente == null) {
                return ResponseEntity.status(400).body("Token non valido");
            }
            if (utente.getTokenResetScadenza().isBefore(java.time.LocalDateTime.now())) {
                return ResponseEntity.status(400).body("Token scaduto. Richiedi un nuovo recupero.");
            }
            if (nuovaPassword == null || nuovaPassword.length() < 8) {
                return ResponseEntity.status(400).body("La password deve essere di almeno 8 caratteri");
            }
            utenteDao.aggiornaPassword(utente.getId(), passwordUtil.cifra(nuovaPassword));
            utenteDao.cancellaTokenReset(utente.getId());
            return ResponseEntity.ok("Password aggiornata con successo!");
        } catch (SQLException e) {
            return ResponseEntity.status(500).body("Errore server");
        }
    }

    @PostMapping("/cambia-password")
    public ResponseEntity<?> cambiaPassword(@RequestBody Map<String, String> body,
                                            HttpSession session) {
        Utente utente = (Utente) session.getAttribute("utenteLoggato");
        if (utente == null) return ResponseEntity.status(401).body("Non autenticato");
        try {
            String vecchia = body.get("vecchiaPassword");
            String nuova = body.get("nuovaPassword");

            if (vecchia == null || nuova == null) {
                return ResponseEntity.badRequest().body("Campi mancanti");
            }
            Utente u = utenteDao.findByEmail(utente.getEmail());
            if (!passwordUtil.verifica(vecchia, u.getPassword())) {
                return ResponseEntity.status(400).body("Password attuale non corretta");
            }
            if (nuova.length() < 8) {
                return ResponseEntity.status(400).body("La nuova password deve essere di almeno 8 caratteri");
            }
            utenteDao.aggiornaPassword(utente.getId(), passwordUtil.cifra(nuova));
            return ResponseEntity.ok("Password aggiornata");
        } catch (SQLException e) {
            return ResponseEntity.status(500).body("Errore server");
        }
    }

    @PostMapping("/foto-profilo")
    public ResponseEntity<?> uploadFotoProfilo(@RequestParam("file") MultipartFile file,
                                               HttpSession session) {
        Utente utente = (Utente) session.getAttribute("utenteLoggato");
        if (utente == null) return ResponseEntity.status(401).body("Non autenticato");

        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("File vuoto");
        }
        if (file.getSize() > 5 * 1024 * 1024) {
            return ResponseEntity.badRequest().body("File troppo grande. Massimo 5MB.");
        }

        try {
            String nomeOriginale = file.getOriginalFilename();
            if (nomeOriginale == null) return ResponseEntity.badRequest().body("File non valido");

            String ext = nomeOriginale.toLowerCase()
                    .substring(nomeOriginale.lastIndexOf('.') + 1);
            if (!ext.equals("jpg") && !ext.equals("jpeg")) {
                return ResponseEntity.badRequest().body("Solo JPG accettato");
            }
            File dir = new File(uploadDir);
            if (!dir.exists()) dir.mkdirs();
            String nomeFile = "profilo_" + utente.getId() + "_" + UUID.randomUUID() + "." + ext;
            Path percorso = Paths.get(uploadDir, nomeFile);
            Files.write(percorso, file.getBytes());
            String url = "http://localhost:8080/uploads/" + nomeFile;
            utenteDao.aggiornaFotoProfilo(utente.getId(), url);
            utente.setFotoProfilo(url);
            aggiornaSessione(session, utente);
            return ResponseEntity.ok(url);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Errore upload: " + e.getMessage());
        }
    }

    @DeleteMapping("/foto-profilo")
    public ResponseEntity<?> rimuoviFotoProfilo(HttpSession session) {
        Utente utente = (Utente) session.getAttribute("utenteLoggato");
        if (utente == null) return ResponseEntity.status(401).body("Non autenticato");
        try {
            utenteDao.aggiornaFotoProfilo(utente.getId(), null);
            utente.setFotoProfilo(null);
            aggiornaSessione(session, utente);
            return ResponseEntity.ok("Foto rimossa");
        } catch (SQLException e) {
            return ResponseEntity.status(500).body("Errore server");
        }
    }
}