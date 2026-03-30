package it.unical.demacs.informatica.immobiliare_backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ai")
//@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
public class AIController {

    private static final String API_KEY = "AIzaSyDRcfxjEPeAP8WA5U9ULGt3DIf45ftZl38";
    private static final String API_URL =  "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash-lite:generateContent";

    private final ObjectMapper objectMapper = new ObjectMapper();

    @PostMapping("/descrizione")
    public ResponseEntity<?> generaDescrizione(@RequestBody Map<String,Object> body,
                                               HttpSession session) {
        try {
            String prompt = (String) body.get("prompt");

            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, Object> requestMap = Map.of(
                    "contents", List.of(
                            Map.of("parts", List.of(
                                    Map.of("text", prompt)
                            ))
                    )
            );

            String requestBody = objectMapper.writeValueAsString(requestMap);

            HttpEntity<String> request = new HttpEntity<>(requestBody, headers);
            ResponseEntity<Map> response = restTemplate.postForEntity(
                    API_URL + "?key=" + API_KEY, request, Map.class
            );

            return ResponseEntity.ok(response.getBody());
        } catch (Exception e) {
            e.printStackTrace();
            // Aggiungi questa riga:
            System.err.println("CAUSA: " + e.getCause());
            System.err.println("MESSAGGIO: " + e.getMessage());
            return ResponseEntity.status(500).body("Errore AI: " + e.getMessage());
        }
    }
}