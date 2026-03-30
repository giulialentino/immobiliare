package it.unical.demacs.informatica.immobiliare_backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String mittente;

    @Value("${app.base-url}")
    private String baseUrl;

    public void inviaVerificaEmail(String destinatario, String token) {
        String link = baseUrl + "/verifica-email?token=" + token;
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setFrom(mittente);
        msg.setTo(destinatario);
        msg.setSubject("UNICAL Immobiliare – Verifica il tuo account");
        msg.setText(
                "Benvenuto su UNICAL Immobiliare!\n\n" +
                        "Clicca il link seguente per verificare il tuo account:\n" +
                        link + "\n\n" +
                        "Il link è valido per 24 ore.\n\n" +
                        "Se non hai creato un account, ignora questa email."
        );
        mailSender.send(msg);
    }

    public void inviaResetPassword(String destinatario, String token) {
        String link = baseUrl + "/reset-password?token=" + token;
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setFrom(mittente);
        msg.setTo(destinatario);
        msg.setSubject("UNICAL Immobiliare – Recupero password");
        msg.setText(
                "Hai richiesto il recupero della password.\n\n" +
                        "Clicca il link seguente per impostare una nuova password:\n" +
                        link + "\n\n" +
                        "Il link scade tra 1 ora.\n\n" +
                        "Se non hai richiesto il recupero, ignora questa email."
        );
        mailSender.send(msg);
    }
}