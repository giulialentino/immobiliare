package it.unical.demacs.informatica.immobiliare_backend.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AIService {

    @Value("${gemini.api.key}")
    private String apiKey;

    @Value("${gemini.api.url}")
    private String apiUrl;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final RestTemplate restTemplate = new RestTemplate();

    private final ConcurrentHashMap<String, Long> ultimaChiamataDescrizione = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Long> ultimaChiamataChat = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Integer> contatoreGiornaliero = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Long> resetGiornaliero = new ConcurrentHashMap<>();

    private static final long INTERVALLO_DESCRIZIONE_MS = 5000;
    private static final long INTERVALLO_CHAT_MS = 2000;
    private static final int MAX_CHIAMATE_GIORNO = 50;

    private static final String SYSTEM_PROMPT = """
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

    public Map generaDescrizione(String prompt, HttpSession session) throws Exception {
        if (isRateLimited(session, ultimaChiamataDescrizione, INTERVALLO_DESCRIZIONE_MS)) {
            throw new IllegalStateException("Aspetta qualche secondo prima di rigenerare.");
        }
        if (superaLimiteGiornaliero(session)) {
            throw new IllegalStateException("Hai raggiunto il limite giornaliero di generazioni AI.");
        }
        if (prompt == null || prompt.isBlank()) {
            throw new IllegalArgumentException("Prompt mancante");
        }
        if (prompt.length() > 1000) {
            throw new IllegalArgumentException("Prompt troppo lungo");
        }

        Map<String, Object> part = new HashMap<>();
        part.put("text", prompt);

        Map<String, Object> content = new HashMap<>();
        content.put("parts", List.of(part));

        Map<String, Object> requestMap = new HashMap<>();
        requestMap.put("contents", List.of(content));

        return chiamaGemini(requestMap);
    }

    public Map chat(String messaggio, Object cronologiaObj, HttpSession session) throws Exception {
        if (isRateLimited(session, ultimaChiamataChat, INTERVALLO_CHAT_MS)) {
            throw new IllegalStateException("Stai scrivendo troppo velocemente. Aspetta un momento.");
        }
        if (superaLimiteGiornaliero(session)) {
            throw new IllegalStateException("Hai raggiunto il limite giornaliero di messaggi AI.");
        }
        if (messaggio == null || messaggio.isBlank()) {
            throw new IllegalArgumentException("Messaggio mancante");
        }
        if (messaggio.length() > 500) {
            throw new IllegalArgumentException("Messaggio troppo lungo. Massimo 500 caratteri.");
        }

        List<Map<String, Object>> contents = new ArrayList<>();

        // System prompt
        Map<String, Object> sistemaContent = new HashMap<>();
        sistemaContent.put("role", "user");
        sistemaContent.put("parts", List.of(Map.of("text", SYSTEM_PROMPT)));
        contents.add(sistemaContent);

        Map<String, Object> sistemaRisposta = new HashMap<>();
        sistemaRisposta.put("role", "model");
        sistemaRisposta.put("parts", List.of(Map.of("text", "Capito! Sono pronto ad assistere gli utenti della piattaforma immobiliare.")));
        contents.add(sistemaRisposta);

        // Cronologia (max 10 messaggi)
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

        // Messaggio corrente
        Map<String, Object> messaggioCorrente = new HashMap<>();
        messaggioCorrente.put("role", "user");
        messaggioCorrente.put("parts", List.of(Map.of("text", messaggio)));
        contents.add(messaggioCorrente);

        Map<String, Object> requestMap = new HashMap<>();
        requestMap.put("contents", contents);

        return chiamaGemini(requestMap);
    }

    private Map chiamaGemini(Map<String, Object> requestMap) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String requestBody = objectMapper.writeValueAsString(requestMap);
        HttpEntity<String> request = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(
                    apiUrl + "?key=" + apiKey, request, Map.class
            );
            return response.getBody();
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode().value() == 429) {
                throw new IllegalStateException("Servizio AI temporaneamente non disponibile. Riprova tra qualche secondo.");
            }
            throw new Exception("Errore AI: " + e.getMessage());
        }
    }
}