# Progetto Web Applications – Annunci Immobiliari

**Corso:** Web Applications (A.A. 2025/26)
**Autore:** Giulia Lentino — Università della Calabria

---

## Descrizione

Web application per la pubblicazione e la ricerca di annunci immobiliari (vendita, affitto, asta). Gli utenti possono registrarsi come acquirenti o venditori, contattare i proprietari, salvare annunci nei preferiti, lasciare recensioni e seguire lo stato di approvazione dei propri annunci. È presente un pannello di amministrazione per la moderazione di utenti e annunci, e un assistente conversazionale basato su AI che risponde a domande sul funzionamento della piattaforma e aiuta a generare le descrizioni degli annunci.

## Stack tecnologico

Frontend: Angular 21 (standalone components, control flow `@if`/`@for`), TypeScript, Bootstrap 5, Bootstrap Icons, Chart.js per i grafici statistici, Leaflet per la visualizzazione delle mappe.

Backend: Java 21, Spring Boot 3.4, accesso al database tramite JDBC puro con pattern DAO (nessun ORM, per scelta progettuale), HikariCP per il connection pooling, Spring Mail per l'invio di email transazionali, Spring Security Crypto (BCrypt) per l'hashing delle password.

Database: PostgreSQL, schema relazionale con 11 tabelle (utente, annuncio, categoria, foto, asta, offerta, messaggio, recensione, preferito, richiesta_promozione, storico_prezzi).

Servizi esterni: Google Gemini API per la chat assistita e la generazione di descrizioni.

## Struttura del repository

```
immobiliare/
├── immobiliare-backend/
│   └── immobiliare-backend/      Progetto Maven Spring Boot
│       ├── src/main/java/...     Controller, Service, DAO, Model
│       └── src/main/resources/   application.properties
├── immobiliare-frontend/         Progetto Angular (CLI)
│   └── src/app/
│       ├── components/           Un componente per ogni vista
│       └── services/             Client HTTP verso il backend
└── dump.sql                      Dump dello schema PostgreSQL con dati di esempio
```

## Requisiti

Java 21, Maven (o il wrapper `mvnw` incluso), Node.js 20+ e npm, PostgreSQL 14+.

## Setup del backend

Creare un database PostgreSQL e importare lo schema:

```
createdb immobiliare
psql immobiliare < dump.sql
```

Il file `application.properties` (tracciato da Git) contiene solo segnaposto, senza credenziali reali: chi esegue il progetto deve fornire le proprie. Per farlo, copiare `application.properties` in un nuovo file `application-local.properties` nella stessa cartella (già escluso dal tracking tramite `.gitignore`, quindi sicuro da popolare con dati reali) e compilarlo così:

```
spring.datasource.url=jdbc:postgresql://localhost:5432/immobiliare
spring.datasource.username=postgres
spring.datasource.password=LA_PROPRIA_PASSWORD_POSTGRES

spring.mail.username=...
spring.mail.password=...        # App Password di Gmail, non la password dell'account

gemini.api.key=...
gemini.api.url=https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash-lite:generateContent
```

Note sui singoli valori:

- `spring.datasource.password`: la password dell'utente PostgreSQL usato per creare il database al passo precedente, scelta da chi esegue il progetto sul proprio computer — non è una credenziale fornita da chi ha sviluppato il progetto.
- `spring.mail.*`: opzionali. Senza queste, il backend si avvia comunque, ma le email di verifica registrazione e di reset password non vengono inviate.
- `gemini.api.key`: una chiave Gemini personale, ottenibile gratuitamente su [Google AI Studio](https://aistudio.google.com/apikey). **Opzionale**: senza una chiave valida, il resto del sito funziona normalmente, ma il chatbot e la generazione automatica delle descrizioni annuncio restituiranno un errore quando richiamati.

Avviare il backend dalla cartella `immobiliare-backend/immobiliare-backend`:

```
./mvnw spring-boot:run
```

Il server risponde su `http://localhost:8080`.

## Setup del frontend

Dalla cartella `immobiliare-frontend`:

```
npm install
npm start
```

L'app è disponibile su `http://localhost:4200`.

## Funzionalità

### Autenticazione e gestione account

Registrazione con verifica email obbligatoria prima del primo accesso, login basato su sessione server-side, recupero password via email con token e reset, cambio password da profilo, upload e rimozione della foto profilo, descrizione personale del profilo modificabile in qualsiasi momento. Le password sono salvate con hashing BCrypt.

### Ruoli utente

L'applicazione distingue quattro livelli di accesso, ciascuno cumulativo rispetto al precedente.

Un visitatore non autenticato può sfogliare gli annunci approvati vedendo titolo, prezzo e posizione, ma non può contattare i venditori, scrivere recensioni o accedere al profilo venditore.

Un acquirente registrato può contattare i venditori tramite messaggi, salvare annunci nei preferiti, scrivere recensioni sugli immobili visitati, segnalare annunci sospetti, fare offerte sugli annunci in asta, e richiedere all'amministratore la promozione a venditore. Dal proprio profilo può modificare la password, gestire la foto profilo, consultare e cancellare i messaggi inviati, e vedere la lista dei preferiti.

Un venditore eredita tutte le funzionalità dell'acquirente. Può pubblicare, modificare ed eliminare i propri annunci (ogni nuovo annuncio o modifica resta in stato "in attesa" fino all'approvazione dell'amministratore), generare la descrizione dell'annuncio tramite AI, applicare un ribasso di prezzo mantenendo visibile il prezzo precedente barrato ed eventualmente annullarlo, e mettere un annuncio in asta. Riceve notifiche con badge numerico quando un acquirente lo contatta, e dal profilo può consultare lo stato di approvazione di ciascun annuncio pubblicato.

Un amministratore eredita tutte le funzionalità del venditore. Dal pannello di amministrazione gestisce l'approvazione o il rifiuto degli annunci in attesa, la moderazione delle segnalazioni ricevute, l'approvazione o il rifiuto delle richieste di promozione ad venditore, e può promuovere o bannare un utente.

### Annunci

Pubblicazione di annunci di vendita, affitto o asta con titolo, descrizione, prezzo, categoria, città, indirizzo, coordinate geografiche, metri quadri, numero di locali e bagni. Caricamento di un massimo di 10 foto per annuncio (5 MB ciascuna), con verifica del limite sia lato client che lato server. Visualizzazione degli annunci in modalità griglia o lista, con filtri per tipo di operazione (vendita, affitto, oppure solo gli annunci attualmente in asta), tipologia immobile, città e fascia di prezzo. Mappa di posizione dell'immobile tramite Leaflet. Limite di 50 annunci pubblicabili per venditore e di 10 modifiche consentite su un annuncio già approvato (nessun limite sulle modifiche prima dell'approvazione); indirizzo e coordinate non sono più modificabili dopo l'approvazione.

### Asta

Un venditore può mettere un proprio annuncio in asta impostando un prezzo base e una data di scadenza. Gli acquirenti possono presentare offerte fino alla scadenza, con verifica lato server che l'asta sia ancora attiva al momento dell'offerta. Lo storico delle offerte è consultabile da chiunque acceda alla pagina dell'annuncio; il nome di chi ha presentato ciascuna offerta è visibile solo al venditore dell'annuncio e agli amministratori, non agli altri acquirenti né ai visitatori. L'asta può essere chiusa manualmente dal venditore (o da un amministratore) in qualsiasi momento, oppure si chiude automaticamente alla scadenza tramite un controllo pianificato lato server che gira ogni minuto.

### Recensioni e segnalazioni

Gli utenti che hanno contattato un venditore per un determinato annuncio possono lasciare una recensione su quell'immobile. Acquirenti e venditori possono segnalare un annuncio ritenuto sospetto; le segnalazioni sono gestite dall'amministratore, che può marcarle come gestite.

### Messaggistica

Gli acquirenti contattano i venditori tramite un sistema di messaggi associati a un annuncio. I venditori vedono il conteggio dei messaggi non letti, possono marcarli come letti ed eliminarli singolarmente o in blocco. Gli acquirenti possono eliminare i messaggi che hanno inviato.

### Assistente AI

Un chatbot (Ubi), accessibile da ogni pagina indipendentemente dal login, risponde a domande sul funzionamento della piattaforma usando l'API Gemini con un prompt di sistema dedicato e una cronologia di conversazione limitata agli ultimi messaggi. Per i venditori è inoltre disponibile la generazione automatica della descrizione di un annuncio a partire da un prompt testuale. Entrambe le funzionalità sono soggette a un rate limit per sessione (intervallo minimo tra le richieste e un tetto massimo di chiamate giornaliere).

### Statistiche

Una sezione dedicata mostra, tramite Chart.js, il numero totale di annunci, il prezzo medio degli immobili, il numero di annunci in asta, e la distribuzione degli annunci per categoria e per tipologia di operazione (grafici a torta e a barre), con aggiornamento dinamico dei dati.

### Altro

Sistema di notifiche toast per confermare azioni o segnalare errori, conferma richiesta prima di azioni distruttive come l'eliminazione di un annuncio o il logout, interfaccia interamente responsive.

## Limiti applicativi

Le regole di business implementate includono: massimo 10 foto per annuncio, massimo 5 MB per foto, massimo 50 annunci per venditore, massimo 10 modifiche consentite su un annuncio già approvato (nessun limite prima dell'approvazione), impossibilità di modificare indirizzo e coordinate dopo l'approvazione, hashing delle password con BCrypt, conferma richiesta per le azioni distruttive (eliminazione annuncio, logout).

## Note

Il file `immobiliare_backup.sql` contiene dati di esempio a scopo dimostrativo. Le credenziali in `application.properties` sono segnaposto: non committare mai credenziali reali nel file tracciato da Git.