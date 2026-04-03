# Progetto Web Applications – Annunci Immobiliari

**Corso:** Web Applications (A.A. 2025/26)  


---

## Descrizione del Progetto

Questo progetto è una **web application per annunci di affitto e vendita di immobili**. L’applicazione permette agli utenti di visualizzare, filtrare, contattare venditori e interagire con gli immobili tramite un’interfaccia moderna e responsive. 

L’ispirazione grafica del progetto è stata presa dal logo e dai colori dell’**Università della Calabria (Unical)**, visibile nella **schermata di splash all’apertura dell’app**, creando un’esperienza coerente e riconoscibile.

---

## Tecnologie Utilizzate

- **Frontend:** Angular 17+(utilizzo di Standalone Components e Nuova Control Flow Syntax @if/@for), TypeScript, HTML5, CSS3(Custom Properties e Animazioni), Bootstrap 5 per il responsive, Bootstrap Icons per l'iconografia.
- **Backend:** Java 21 con Spring Boot 3,JDBC Puro: Gestione della persistenza senza ORM (come richiesto da specifiche), REST Controllers, pattern DAO con PostgreSQL,HikariCP: Connection pooling per alte prestazioni,JavaMailSender: Invio email SMTP
- **Database:** PostgreSQL,11 Tabelle relazionate (Utenti, Annunci, Categorie, Foto, Aste, Offerte, Messaggi, Recensioni, Preferiti, RichiestePromozione, StoricoPrezzi).
- **Librerie Esterne e API:** Chart.js per statistiche e grafici sull'andamento dei prezzi, Google Maps API per la geolocalizzazione degli immobili,Facebook API per promozione degli annunci dai venditori
- **Funzionalità interessanti:** chatbot con AI (UBI),descrizione annunci generata da AI, gestione toast, upload foto, autenticazione multi-ruolo(possibilità di cambiare ruolo una volta registrati)
- **Design Pattern** DAO (Data Access Object): Isolamento totale della logica di persistenza SQL.
Proxy Pattern: Implementato AnnuncioProxy per il Lazy Loading di foto e recensioni, ottimizzando le performance del database.
Singleton: Utilizzato per la gestione della configurazione del DataSource.
MVC (Model-View-Controller): Netta separazione tra dati, logica di business e interfaccia utente.

---

## Funzionalità Principali

### Interfaccia Utente

- **Splash Screen:** logo animato ispirato all’Unical
- **Barra di navigazione:** accesso a statistiche del sito, login, registrazione e profilazione utenti
- **Hero:** video introduttivo nella home page
- **Categorie Immobili:** 6 categorie principali + categoria “Ultimi 3 annunci pubblicati”
- **Filtri Annunci:** per tipo (vendita/affitto), tipologia immobile, città e range di prezzo
- **Visualizzazione Annunci:** modalità **griglia** o **lista**
- **Mappe:** visualizzazione della posizione dell’immobile tramite Google Maps
- **Toast notifications:** gestione eventi e messaggi di conferma/errore

---

### Chatbot UBI

- Accessibile a tutti
- Chat guidata da intelligenza artificiale
- Supporta messaggi automatici generati durante interazioni importanti (es. come si pubblica un annuncio)

---

### Ruoli Utente

1. **Utente non autenticato**
    - Può visualizzare: titolo, descrizione, prezzo e posizione degli immobili
    - Funzionalità bloccate: recensioni, contatto venditore, profilo venditore

2. **Acquirente**
    - Può: contattare venditori, scrivere recensioni, salvare annunci preferiti
    - Profilo: modifica password, gestisce foto profilo, lista messaggi inviati (cancellabili dalla bacheca), lista preferiti
    - Messaggi automatici generati quando contatta venditore
    - Segnalazione annunci sospetti
    - Può scegliere di inoltare una richiesta all'admin per diventare venditore usando le stesse coordinate

3. **Venditore**
    - Tutte le funzionalità dell’acquirente
    - Pubblica, modifica e elimina propri annunci
    - Generazione descrizioni degli annunci tramite AI
    - Possibilità di ribassare il prezzo (vecchio prezzo barrato) e eventualmente annullare il ribasso
    - Profilo: lista annunci pubblicati (stato annuncio in base all'approvazione dell'admin: in attesa, approvato, rifiutato)
    - Notifiche e badge numerici quando riceve contatti da acquirenti
    - Segnalazione annunci sospetti
    - Richiesta publicazione degli annunci tramite admin

4. **Amministratore**
    - Tutte le funzionalità dei venditori
    - Gestione utenti: promuovere, bannare
    - Gestione annunci: approvare, rifiutare, eliminare
    - Pannello amministrativo per controllare approvazioni e segnalazioni

---

### Limitazioni di Sicurezza e Controllo

- Uso di Bicrypt per cifrare le password nel bd
- Massimo **10 foto per annuncio**
- Massimo **50 annunci per venditore**
- Massimo **5 MB caricabili per foto**
- Modifica di un annuncio già approvato, consentita massimo **10 volte** a differenza degli annunci non ancora approvati che non hanno limiti
- Non è possibile modificare **coordinate, città e via** dopo che l'admin approva l'annuncio al venditore
- Conferme richieste per azioni sensibili (es. eliminazione annuncio, logout)

---

### Statistiche del Sito

- Grafici con **Chart.js**:
    - Annunci totali
    - Prezzo medio degli immobili
    - Annunci con asta
    - Suddivisione per categoria e tipologia
- Aggiornamento dinamico delle statistiche con animazioni

---

### Funzionalità Extra

- **Recensioni:** solo per utenti che hanno visitato l’immobile
- **Segnalazioni:** acquirenti e venditori possono segnalare un annuncio sospetto
- **Chatbot AI (UBI)** guida e supporta utenti
- **Descrizione annuncio generata con l'AI** solo per i venditori
- **Interfaccia Responsive:** perfetta per desktop e mobile
- **Gestione notifiche:** messaggi e azioni confermate tramite toast dinamici

---

## Struttura del Progetto

- `frontend/` – Angular App
- `backend/` – Spring Boot REST API
- `database/` – dump PostgreSQL
- `README.md` – documentazione del progetto
- `src/` – componenti, servizi e moduli Angular
- `assets/` – immagini, video, icone
- `styles/` – CSS personalizzato (almeno 5 regole custom)

---