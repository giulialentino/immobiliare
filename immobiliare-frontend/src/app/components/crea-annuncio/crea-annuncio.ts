import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { AnnuncioService } from '../../services/annuncio';
import { CategoriaService } from '../../services/categoria';
import { AuthService } from '../../services/auth';
import { FotoService } from '../../services/foto';
import { AstaService } from '../../services/asta';

@Component({
  selector: 'app-crea-annuncio',
  standalone: true,
  imports: [CommonModule, RouterLink, FormsModule],
  templateUrl: './crea-annuncio.html',
  styleUrl: './crea-annuncio.css'
})
export class CreaAnnuncio implements OnInit {

  categorie: any[] = [];
  errore = '';
  fileFoto: File[] = [];
  comuni: string[] = [];
  comuniFiltrati: string[] = [];
  cittaInput = '';
  via = '';
  indiceSelezionato = -1;
  oggi = new Date();

 annuncio = {
  titolo: '',
  descrizione: '',
  prezzo: null,
  metriQuadri: null,
  numLocali: null,
  numBagni: null,
  tipoOperazione: 'VENDITA',
  indirizzo: '',
  latitudine: null,
  longitudine: null,
  inAsta: false,
  idCategoria: null
};

  asta = {
    prezzoBase: null,
    dataScadenza: ''
  };

  constructor(
    private annuncioService: AnnuncioService,
    private categoriaService: CategoriaService,
    private authService: AuthService,
    private fotoService: FotoService,
    private astaService: AstaService,
    private router: Router,
    private http: HttpClient,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit() {
    this.authService.getUtenteLoggato().subscribe({
      next: (u) => {
        if (!u || (u.ruolo !== 'VENDITORE' && u.ruolo !== 'AMMINISTRATORE')) {
          this.router.navigate(['/']);
        }
      },
      error: () => this.router.navigate(['/login'])
    });
    this.categoriaService.getAll().subscribe({
      next: (data) => { this.categorie = data; this.cdr.detectChanges(); },
      error: (err: any) => console.error(err)
    });
    this.http.get<any[]>('assets/comuni.json').subscribe({
      next: (data) => {
        this.comuni = data.map((c: any) => c.nome).sort();
        this.cdr.detectChanges();
      },
      error: () => console.error('Errore caricamento comuni')
    });
  }

  get dataMinima(): string {
    const now = new Date();
    now.setMinutes(now.getMinutes() - now.getTimezoneOffset());
    return now.toISOString().slice(0, 16);
  }

  dataValida(): boolean {
    if (!this.asta.dataScadenza) return false;
    return new Date(this.asta.dataScadenza) > new Date();
  }

  filtraComuni() {
    if (this.cittaInput.length < 2) {
      this.comuniFiltrati = [];
      this.indiceSelezionato = -1;
      return;
    }
    this.comuniFiltrati = this.comuni
      .filter(c => c.toLowerCase().startsWith(this.cittaInput.toLowerCase()))
      .slice(0, 8);
    this.indiceSelezionato = -1;
  }

  selezionaComune(c: string) {
    this.cittaInput = c;
    this.comuniFiltrati = [];
    this.indiceSelezionato = -1;
  }

  gestisciTasto(event: KeyboardEvent) {
    if (this.comuniFiltrati.length === 0) return;
    if (event.key === 'ArrowDown') {
      event.preventDefault();
      this.indiceSelezionato = Math.min(this.indiceSelezionato + 1, this.comuniFiltrati.length - 1);
    } else if (event.key === 'ArrowUp') {
      event.preventDefault();
      this.indiceSelezionato = Math.max(this.indiceSelezionato - 1, -1);
    } else if (event.key === 'Enter') {
      if (this.indiceSelezionato >= 0) {
        this.selezionaComune(this.comuniFiltrati[this.indiceSelezionato]);
      }
    } else if (event.key === 'Escape') {
      this.comuniFiltrati = [];
      this.indiceSelezionato = -1;
    }
  }

  onFileSelected(event: any) {
    const files: File[] = Array.from(event.target.files);
    const nonValidi = files.filter(f => {
      const ext = f.name.toLowerCase().split('.').pop();
      return !['jpg', 'jpeg', 'png'].includes(ext || '');
    });
    if (nonValidi.length > 0) {
      this.errore = 'Sono accettati solo file JPG,PNG,JPEG';
      event.target.value = '';
      this.fileFoto = [];
      return;
    }
    this.errore = '';
    this.fileFoto = files;
  }

  salva() {
    this.errore = '';
    if (!this.annuncio.titolo || !this.annuncio.prezzo || !this.annuncio.idCategoria) {
      this.errore = 'Compila tutti i campi obbligatori';
      return;
    }
    if (!this.cittaInput) {
      this.errore = 'Seleziona una città';
      return;
    }
    if (this.annuncio.inAsta && (!this.asta.prezzoBase || !this.asta.dataScadenza)) {
      this.errore = 'Inserisci prezzo base e data scadenza per l\'asta';
      return;
    }
    if (this.annuncio.inAsta && !this.dataValida()) {
      this.errore = 'La data di scadenza asta deve essere successiva alla data attuale';
      return;
    }

    this.annuncio.indirizzo = this.via
      ? `${this.cittaInput}, ${this.via}`
      : this.cittaInput;

    this.annuncioService.crea(this.annuncio).subscribe({
      next: (annuncioSalvato) => {
        const dopoFoto = () => {
          if (this.annuncio.inAsta && this.asta.prezzoBase && this.asta.dataScadenza) {
            this.astaService.crea({
              idAnnuncio: annuncioSalvato.id,
              prezzoBase: this.asta.prezzoBase,
              dataScadenza: this.asta.dataScadenza
            }).subscribe({
              next: () => this.router.navigate(['/annuncio', annuncioSalvato.id]),
              error: () => this.router.navigate(['/annuncio', annuncioSalvato.id])
            });
          } else {
            this.router.navigate(['/annuncio', annuncioSalvato.id]);
          }
        };

        if (this.fileFoto.length > 0) {
          let caricati = 0;
          this.fileFoto.forEach(file => {
            this.fotoService.upload(annuncioSalvato.id, file).subscribe({
              next: () => {
                caricati++;
                if (caricati === this.fileFoto.length) dopoFoto();
              },
              error: (err: any) => console.error(err)
            });
          });
        } else {
          dopoFoto();
        }
      },
      error: () => this.errore = 'Errore durante il salvataggio'
    });
  }
  generandoDescrizione = false;

generaDescrizioneAI() {
  if (!this.annuncio.titolo) {
    this.errore = 'Inserisci almeno il titolo per generare una descrizione';
    return;
  }

  this.generandoDescrizione = true;
  this.cdr.detectChanges();

  const testoPrompt = `Sei un agente immobiliare professionale italiano.
Genera una descrizione accattivante per questo annuncio immobiliare:
- Tipo operazione: ${this.annuncio.tipoOperazione || 'N/D'}
- Titolo: ${this.annuncio.titolo}
- Metri quadri: ${this.annuncio.metriQuadri || 'N/D'}
- Locali: ${this.annuncio.numLocali || 'N/D'}
- Bagni: ${this.annuncio.numBagni || 'N/D'}
- Localita: ${this.cittaInput}
Regole: solo testo della descrizione senza titoli, massimo 3 frasi, italiano, tono professionale ma accessibile agli acquirenti o affituari.`;

  // Il backend si aspetta { prompt: "..." } — NON la struttura Gemini
  const body = { prompt: testoPrompt };

  this.http.post<any>(
    'http://localhost:8080/api/ai/descrizione',
    body,
    { withCredentials: true }
  ).subscribe({
    next: (data) => {
      // Estrazione dalla struttura Gemini che restituisce il backend
      const testo = data?.candidates?.[0]?.content?.parts?.[0]?.text || '';
      this.annuncio.descrizione = testo.trim();
      this.generandoDescrizione = false;
      this.cdr.detectChanges();
    },
    error: (err) => {
      if (err.status === 429) {
        this.errore = 'Servizio AI sovraccarico. Riprova tra qualche secondo.';
      } else {
        this.errore = 'Impossibile generare la descrizione al momento.';
      }
      this.generandoDescrizione = false;
      this.cdr.detectChanges();
    }
  });
}}