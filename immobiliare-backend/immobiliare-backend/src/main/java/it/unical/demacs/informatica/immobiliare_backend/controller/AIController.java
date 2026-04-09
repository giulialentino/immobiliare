package it.unical.demacs.informatica.immobiliare_backend.controller;

import it.unical.demacs.informatica.immobiliare_backend.service.AIService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/ai")
public class AIController {

    @Autowired
    private AIService aiService;

    @PostMapping("/descrizione")
    public ResponseEntity<?> generaDescrizione(@RequestBody Map<String, Object> body,
                                               HttpSession session) throws Exception {
        String prompt = (String) body.get("prompt");
        return ResponseEntity.ok(aiService.generaDescrizione(prompt, session));
    }

    @PostMapping("/chat")
    public ResponseEntity<?> chat(@RequestBody Map<String, Object> body,
                                  HttpSession session) throws Exception {
        String messaggio = (String) body.get("messaggio");
        Object cronologia = body.get("cronologia");
        return ResponseEntity.ok(aiService.chat(messaggio, cronologia, session));
    }
}
