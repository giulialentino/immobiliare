package it.unical.demacs.informatica.immobiliare_backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ai")
public class AIController {

    @Value("${gemini.api.key}")
    private String apiKey;

    @Value("${gemini.api.url}")
    private String apiUrl;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @PostMapping("/descrizione")
    public ResponseEntity<?> generaDescrizione(@RequestBody Map<String, Object> body,
                                               HttpSession session) {
        try {
            String prompt = (String) body.get("prompt");

            if (prompt == null || prompt.isBlank()) {
                return ResponseEntity.badRequest().body("Prompt mancante");
            }

            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, Object> part = new HashMap<>();
            part.put("text", prompt);

            Map<String, Object> content = new HashMap<>();
            content.put("parts", List.of(part));

            Map<String, Object> requestMap = new HashMap<>();
            requestMap.put("contents", List.of(content));

            String requestBody = objectMapper.writeValueAsString(requestMap);
            HttpEntity<String> request = new HttpEntity<>(requestBody, headers);

            ResponseEntity<Map> response = restTemplate.postForEntity(
                    apiUrl + "?key=" + apiKey, request, Map.class
            );

            return ResponseEntity.ok(response.getBody());

        } catch (HttpClientErrorException e) {
            if (e.getStatusCode().value() == 429) {
                return ResponseEntity.status(429).body("Servizio AI temporaneamente non disponibile. Riprova tra qualche secondo.");
            }
            return ResponseEntity.status(500).body("Errore AI: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Errore AI: " + e.getMessage());
        }
    }
}