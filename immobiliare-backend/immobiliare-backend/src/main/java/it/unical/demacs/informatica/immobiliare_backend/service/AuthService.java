package it.unical.demacs.informatica.immobiliare_backend.service;

import it.unical.demacs.informatica.immobiliare_backend.config.PasswordUtil;
import it.unical.demacs.informatica.immobiliare_backend.dao.UtenteDao;
import it.unical.demacs.informatica.immobiliare_backend.model.Utente;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.UUID;

@Service
public class AuthService {

    @Value("${upload.dir}")
    private String uploadDir;

    @Value("${app.upload-url}")
    private String uploadUrl;

    @Autowired
    private UtenteDao utenteDao;

    @Autowired
    private PasswordUtil passwordUtil;

    @Autowired
    private EmailService emailService;

    public void aggiornaSessione(HttpSession session, Utente utente) {
        utente.setPassword(null);
        utente.setTokenVerifica(null);
        utente.setTokenReset(null);
        utente.setTokenResetScadenza(null);
        session.setAttribute("utenteLoggato", utente);
    }

    public Utente login(String email, String password) throws SQLException {
        Utente utente = utenteDao.findByEmail(email);
        if (utente == null) {
            throw new IllegalArgumentException("Credenziali errate");
        }
        if (utente.isBannato()) {
            throw new SecurityException("Utente bannato");
        }
        if (!utente.isEmailVerificata()) {
            throw new IllegalStateException("EMAIL_NON_VERIFICATA");
        }
        boolean passwordOk = passwordUtil.verifica(password, utente.getPassword());
        if (!passwordOk) {
            throw new IllegalArgumentException("Credenziali errate");
        }
        return utente;
    }

    public Utente registra(Utente nuovo) throws SQLException {
        if (nuovo.getEmail() == null || !nuovo.getEmail().matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
            throw new IllegalArgumentException("Email non valida");
        }
        if (nuovo.getPassword() == null || nuovo.getPassword().length() < 8) {
            throw new IllegalArgumentException("Password troppo corta");
        }
        Utente esistente = utenteDao.findByEmail(nuovo.getEmail());
        if (esistente != null) {
            throw new IllegalStateException("Email già registrata");
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

        return salvato;
    }

    public void verificaEmail(String token) throws SQLException {
        Utente utente = utenteDao.findByTokenVerifica(token);
        if (utente == null) {
            throw new IllegalArgumentException("Token non valido o già utilizzato");
        }
        utenteDao.verificaEmail(utente.getId());
    }

    public void recuperaPassword(String email) throws SQLException {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email mancante");
        }
        Utente utente = utenteDao.findByEmail(email);
        if (utente != null) {
            String token = UUID.randomUUID().toString();
            java.time.LocalDateTime scadenza = java.time.LocalDateTime.now().plusHours(1);
            utenteDao.salvaTokenReset(utente.getId(), token, scadenza);
            emailService.inviaResetPassword(utente.getEmail(), token);
        }
    }

    public void resetPassword(String token, String nuovaPassword) throws SQLException {
        if (token == null || token.isBlank()) {
            throw new IllegalArgumentException("Token mancante");
        }
        Utente utente = utenteDao.findByTokenReset(token);
        if (utente == null) {
            throw new IllegalArgumentException("Token non valido");
        }
        if (utente.getTokenResetScadenza().isBefore(java.time.LocalDateTime.now())) {
            throw new IllegalStateException("Token scaduto. Richiedi un nuovo recupero.");
        }
        if (nuovaPassword == null || nuovaPassword.length() < 8) {
            throw new IllegalArgumentException("La password deve essere di almeno 8 caratteri");
        }
        utenteDao.aggiornaPassword(utente.getId(), passwordUtil.cifra(nuovaPassword));
        utenteDao.cancellaTokenReset(utente.getId());
    }

    public void cambiaPassword(Utente utente, String vecchiaPassword, String nuovaPassword) throws SQLException {
        if (vecchiaPassword == null || nuovaPassword == null) {
            throw new IllegalArgumentException("Campi mancanti");
        }
        Utente u = utenteDao.findByEmail(utente.getEmail());
        if (!passwordUtil.verifica(vecchiaPassword, u.getPassword())) {
            throw new IllegalArgumentException("Password attuale non corretta");
        }
        if (nuovaPassword.length() < 8) {
            throw new IllegalArgumentException("La nuova password deve essere di almeno 8 caratteri");
        }
        utenteDao.aggiornaPassword(utente.getId(), passwordUtil.cifra(nuovaPassword));
    }

    public String uploadFotoProfilo(Utente utente, MultipartFile file) throws SQLException, IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File vuoto");
        }
        if (file.getSize() > 5 * 1024 * 1024) {
            throw new IllegalArgumentException("File troppo grande. Massimo 5MB.");
        }

        String nomeOriginale = file.getOriginalFilename();
        if (nomeOriginale == null) throw new IllegalArgumentException("File non valido");

        String ext = nomeOriginale.toLowerCase().substring(nomeOriginale.lastIndexOf('.') + 1);
        if (!ext.equals("jpg") && !ext.equals("jpeg")) {
            throw new IllegalArgumentException("Solo JPG accettato");
        }

        File dir = new File(uploadDir);
        if (!dir.exists()) dir.mkdirs();

        String nomeFile = "profilo_" + utente.getId() + "_" + UUID.randomUUID() + "." + ext;
        Path percorso = Paths.get(uploadDir, nomeFile);
        Files.write(percorso, file.getBytes());

        String url = uploadUrl + nomeFile;
        utenteDao.aggiornaFotoProfilo(utente.getId(), url);
        utente.setFotoProfilo(url);

        return url;
    }

    public void rimuoviFotoProfilo(Utente utente) throws SQLException {
        utenteDao.aggiornaFotoProfilo(utente.getId(), null);
        utente.setFotoProfilo(null);
    }

    public Utente getUtenteById(Long id) throws SQLException {
        Utente utente = utenteDao.findById(id);
        if (utente == null) return null;
        utente.setPassword(null);
        utente.setTokenVerifica(null);
        utente.setTokenReset(null);
        utente.setTokenResetScadenza(null);
        return utente;
    }
}
