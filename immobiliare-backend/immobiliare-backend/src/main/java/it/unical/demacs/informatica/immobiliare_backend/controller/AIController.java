package it.unical.demacs.informatica.immobiliare_backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api/ai")
public class AIController {

    @Value("${gemini.api.key}")
    private String apiKey;

    @Value("${gemini.api.url}")
    private String apiUrl;

    private final ObjectMapper objectMapper = new ObjectMapper();

    // Rate limiter — millisecondi minimi tra una chiamata e l'altra per sessione
    private final ConcurrentHashMap<String, Long> ultimaChiamataDescrizione = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Long> ultimaChiamataChat = new ConcurrentHashMap<>();

    // Contatore chiamate giornaliere per sessione
    private final ConcurrentHashMap<String, Integer> contatoreGiornaliero = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Long> resetGiornaliero = new ConcurrentHashMap<>();

    private static final long INTERVALLO_DESCRIZIONE_MS = 5000;  // 5 secondi tra descrizioni
    private static final long INTERVALLO_CHAT_MS = 2000;         // 2 secondi tra messaggi chat
    private static final int MAX_CHIAMATE_GIORNO = 50;           // max 50 chiamate AI al giorno per sessione

    private boolean isRateLimited(HttpSession session, ConcurrentHashMap<String, Long> mappa, long intervalloMs) {
        String sessionId = session.getId();
        long ora = System.currentTimeMillis();
        Long ultima = mappa.get(sessionId);
        if (ultima != null && (ora - ultima) < intervalloMs) {
            return true;
        }
        mappa.put(sessionId, ora);
        return false;
    }

    private boolean superaLimiteGiornaliero(HttpSession session) {
        String sessionId = session.getId();
        long ora = System.currentTimeMillis();
        long unGiorno = 24 * 60 * 60 * 1000L;

        // Reset contatore se è passato un giorno
        Long ultimoReset = resetGiornaliero.get(sessionId);
        if (ultimoReset == null || (ora - ultimoReset) > unGiorno) {
            contatoreGiornaliero.put(sessionId, 0);
            resetGiornaliero.put(sessionId, ora);
        }

        int count = contatoreGiornaliero.getOrDefault(sessionId, 0);
        if (count >= MAX_CHIAMATE_GIORNO) {
            return true;
        }
        contatoreGiornaliero.put(sessionId, count + 1);
        return false;
    }

    @PostMapping("/descrizione")
    public ResponseEntity<?> generaDescrizione(@RequestBody Map<String, Object> body,
                                               HttpSession session) {
        // Rate limit — 5 secondi tra una descrizione e l'altra
        if (isRateLimited(session, ultimaChiamataDescrizione, INTERVALLO_DESCRIZIONE_MS)) {
            return ResponseEntity.status(429).body("Aspetta qualche secondo prima di rigenerare.");
        }

        // Limite giornaliero
        if (superaLimiteGiornaliero(session)) {
            return ResponseEntity.status(429).body("Hai raggiunto il limite giornaliero di generazioni AI.");
        }

        try {
            String prompt = (String) body.get("prompt");

            if (prompt == null || prompt.isBlank()) {
                return ResponseEntity.badRequest().body("Prompt mancante");
            }

            // Limite lunghezza prompt — evita prompt enormi
            if (prompt.length() > 1000) {
                return ResponseEntity.badRequest().body("Prompt troppo lungo");
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

    @PostMapping("/chat")
    public ResponseEntity<?> chat(@RequestBody Map<String, Object> body,
                                  HttpSession session) {
        // Rate limit — 2 secondi tra un messaggio e l'altro
        if (isRateLimited(session, ultimaChiamataChat, INTERVALLO_CHAT_MS)) {
            return ResponseEntity.status(429).body("Stai scrivendo troppo velocemente. Aspetta un momento.");
        }

        // Limite giornaliero
        if (superaLimiteGiornaliero(session)) {
            return ResponseEntity.status(429).body("Hai raggiunto il limite giornaliero di messaggi AI.");
        }

        try {
            String messaggio = (String) body.get("messaggio");
            Object cronologiaObj = body.get("cronologia");

            if (messaggio == null || messaggio.isBlank()) {
                return ResponseEntity.badRequest().body("Messaggio mancante");
            }

            // Limite lunghezza messaggio
            if (messaggio.length() > 500) {
                return ResponseEntity.badRequest().body("Messaggio troppo lungo. Massimo 500 caratteri.");
            }

            String sistemPrompt = """
                    Sei un assistente virtuale di una piattaforma immobiliare italiana chiamata Immobiliare.
                    Rispondi solo a domande riguardanti la piattaforma e il settore immobiliare in generale.
                    Se ti vengono fatte domande non pertinenti, rispondi educatamente che puoi aiutare solo con argomenti immobiliari.
                    Rispondi sempre in italiano, in modo cordiale e professionale.
                    Sii conciso — massimo 4 frasi per risposta.
                    
                    Ecco come funziona la piattaforma:
                    - Gli utenti si registrano come ACQUIRENTI o VENDITORI
                    - Un acquirente può richiedere la promozione a venditore — la richiesta viene valutata dall'amministratore
                    - I venditori pubblicano annunci di vendita, affitto o asta
                    - Ogni annuncio deve essere approvato dall'amministratore prima di essere pubblicato
                    - Gli acquirenti possono contattare i venditori tramite messaggi se interessati a un annuncio
                    - Gli utenti possono salvare annunci nei preferiti
                    - È possibile fare offerte sugli annunci in asta entro la data di scadenza
                    - L'amministratore può promuovere un utente ad amministratore o bannare utenti
                    - La piattaforma opera solo in Italia
                    - Per pubblicare un annuncio servono: titolo, prezzo, categoria, città
                    - Gli annunci possono avere foto, descrizione, metri quadri, locali, bagni
                    - Dopo la registrazione è necessario verificare la propria email prima di accedere
                    - La password deve avere almeno 8 caratteri, una maiuscola e un numero
                    """;

            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            List<Map<String, Object>> contents = new ArrayList<>();

            Map<String, Object> sistemaPart = new HashMap<>();
            sistemaPart.put("text", sistemPrompt);
            Map<String, Object> sistemaContent = new HashMap<>();
            sistemaContent.put("role", "user");
            sistemaContent.put("parts", List.of(sistemaPart));
            contents.add(sistemaContent);

            Map<String, Object> sistemaRisposta = new HashMap<>();
            sistemaRisposta.put("role", "model");
            sistemaRisposta.put("parts", List.of(Map.of("text", "Capito! Sono pronto ad assistere gli utenti della piattaforma immobiliare.")));
            contents.add(sistemaRisposta);

            // Limite cronologia — max 10 messaggi precedenti per non mandare token enormi
            if (cronologiaObj instanceof List<?> cronologia) {
                int start = Math.max(0, cronologia.size() - 10);
                for (int i = start; i < cronologia.size(); i++) {
                    Object item = cronologia.get(i);
                    if (item instanceof Map<?, ?> msg) {
                        String ruolo = (String) msg.get("ruolo");
                        String testo = (String) msg.get("testo");
                        if (testo != null && testo.length() <= 500) {
                            Map<String, Object> contentItem = new HashMap<>();
                            contentItem.put("role", ruolo.equals("utente") ? "user" : "model");
                            contentItem.put("parts", List.of(Map.of("text", testo)));
                            contents.add(contentItem);
                        }
                    }
                }
            }

            Map<String, Object> messaggioCorrente = new HashMap<>();
            messaggioCorrente.put("role", "user");
            messaggioCorrente.put("parts", List.of(Map.of("text", messaggio)));
            contents.add(messaggioCorrente);

            Map<String, Object> requestMap = new HashMap<>();
            requestMap.put("contents", contents);

            String requestBody = objectMapper.writeValueAsString(requestMap);
            HttpEntity<String> request = new HttpEntity<>(requestBody, headers);

            ResponseEntity<Map> response = restTemplate.postForEntity(
                    apiUrl + "?key=" + apiKey, request, Map.class
            );

            return ResponseEntity.ok(response.getBody());

        } catch (HttpClientErrorException e) {
            if (e.getStatusCode().value() == 429) {
                return ResponseEntity.status(429).body("Servizio AI temporaneamente non disponibile.");
            }
            return ResponseEntity.status(500).body("Errore AI: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Errore AI: " + e.getMessage());
        }
    }
}