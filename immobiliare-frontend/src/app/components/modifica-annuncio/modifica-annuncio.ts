import { Component, OnInit, ChangeDetectorRef, HostListener } from '@angular/core';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { AnnuncioService } from '../../services/annuncio';
import { CategoriaService } from '../../services/categoria';
import { AuthService } from '../../services/auth';
import { FotoService } from '../../services/foto';
import { AstaService } from '../../services/asta';

@Component({
  selector: 'app-modifica-annuncio',
  standalone: true,
  imports: [CommonModule, RouterLink, FormsModule],
  templateUrl: './modifica-annuncio.html',
  styleUrl: './modifica-annuncio.css'
})
export class ModificaAnnuncio implements OnInit {

  categorie: any[] = [];
  errore = '';
  fotoEsistenti: string[] = [];
  nuoveFoto: File[] = [];
  comuni: string[] = [];
  comuniFiltrati: string[] = [];
  cittaInput = '';
  via = '';
  indiceSelezionato = -1;
  oggi = new Date();
  generandoDescrizione = false;

  annuncio: any = {
    titolo: '',
    descrizione: '',
    prezzo: null,
    metriQuadri: null,
    tipoOperazione: 'VENDITA',
    indirizzo: '',
    latitudine: null,
    longitudine: null,
    inAsta: false,
    idCategoria: null,
    dataInserimento: null
  };

  asta = {
    prezzoBase: null,
    dataScadenza: ''
  };

  constructor(
    private route: ActivatedRoute,
    private annuncioService: AnnuncioService,
    private categoriaService: CategoriaService,
    private authService: AuthService,
    private fotoService: FotoService,
    private astaService: AstaService,
    private router: Router,
    private http: HttpClient,
    private cdr: ChangeDetectorRef
  ) {}

  @HostListener('document:click', ['$event'])
  onDocumentClick(event: MouseEvent) {
    const target = event.target as HTMLElement;
    if (!target.closest('.autocomplete-wrapper')) {
      this.comuniFiltrati = [];
      this.indiceSelezionato = -1;
      this.cdr.detectChanges();
    }
  }

  ngOnInit() {
    this.authService.getUtenteLoggato().subscribe({
      next: (u: any) => {
        if (!u || (u.ruolo !== 'VENDITORE' && u.ruolo !== 'AMMINISTRATORE')) {
          this.router.navigate(['/']);
        }
      },
      error: () => this.router.navigate(['/login'])
    });

    this.categoriaService.getAll().subscribe({
      next: (data: any) => { this.categorie = data; this.cdr.detectChanges(); },
      error: (err: any) => console.error(err)
    });

    this.http.get<any[]>('assets/comuni.json').subscribe({
      next: (data) => {
        this.comuni = data.map((c: any) => c.nome).sort();
        this.cdr.detectChanges();
      },
      error: () => console.error('Errore caricamento comuni')
    });

    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.annuncioService.getById(+id).subscribe({
        next: (data: any) => {
          this.annuncio = data;
          this.fotoEsistenti = data.foto || [];
          if (data.indirizzo) {
            const parti = data.indirizzo.split(',');
            if (parti.length >= 2) {
              this.cittaInput = parti[0].trim();
              this.via = parti.slice(1).join(',').trim();
            } else {
              this.cittaInput = data.indirizzo.trim();
            }
          }
          this.cdr.detectChanges();
        },
        error: (err: any) => console.error(err)
      });
    }
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
    this.cdr.detectChanges();
  }

  scrollToSelected() {
    setTimeout(() => {
      const lista = document.querySelector('.autocomplete-list') as HTMLElement;
      if (!lista || this.indiceSelezionato < 0) return;
      const items = lista.querySelectorAll('.autocomplete-item');
      if (!items || items.length === 0) return;
      const item = items[this.indiceSelezionato] as HTMLElement;
      if (!item) return;
      item.scrollIntoView({ block: 'nearest' });
    }, 50);
  }

  gestisciTasto(event: KeyboardEvent) {
    if (this.comuniFiltrati.length === 0) return;
    if (event.key === 'ArrowDown') {
      event.preventDefault();
      this.indiceSelezionato = Math.min(this.indiceSelezionato + 1, this.comuniFiltrati.length - 1);
      this.cdr.detectChanges();
      this.scrollToSelected();
    } else if (event.key === 'ArrowUp') {
      event.preventDefault();
      this.indiceSelezionato = Math.max(this.indiceSelezionato - 1, -1);
      this.cdr.detectChanges();
      this.scrollToSelected();
    } else if (event.key === 'Enter') {
      if (this.indiceSelezionato >= 0) {
        this.selezionaComune(this.comuniFiltrati[this.indiceSelezionato]);
      }
    } else if (event.key === 'Escape') {
      this.comuniFiltrati = [];
      this.indiceSelezionato = -1;
      this.cdr.detectChanges();
    }
  }

  onFileSelected(event: any) {
    const files: File[] = Array.from(event.target.files);
    const nonValidi = files.filter(f => {
      const ext = f.name.toLowerCase().split('.').pop();
      return !['jpg', 'jpeg', 'png'].includes(ext || '');
    });
    if (nonValidi.length > 0) {
      this.errore = 'Sono accettati solo file JPG e PNG';
      event.target.value = '';
      this.nuoveFoto = [];
      return;
    }
    this.errore = '';
    this.nuoveFoto = files;
  }

  rimuoviFoto(url: string) {
    this.fotoEsistenti = this.fotoEsistenti.filter(f => f !== url);
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
      this.errore = "Inserisci prezzo base e data scadenza per l'asta";
      return;
    }
    if (this.annuncio.inAsta && !this.dataValida()) {
      this.errore = 'La data di scadenza asta deve essere successiva alla data attuale';
      return;
    }

    this.annuncio.indirizzo = this.via
      ? `${this.cittaInput}, ${this.via}`
      : this.cittaInput;

    this.annuncioService.modifica(this.annuncio.id, this.annuncio).subscribe({
      next: () => {
        const dopoFoto = () => {
          if (this.annuncio.inAsta && this.asta.prezzoBase && this.asta.dataScadenza) {
            this.astaService.crea({
              idAnnuncio: this.annuncio.id,
              prezzoBase: this.asta.prezzoBase,
              dataScadenza: this.asta.dataScadenza
            }).subscribe({
              next: () => this.router.navigate(['/annuncio', this.annuncio.id]),
              error: () => this.router.navigate(['/annuncio', this.annuncio.id])
            });
          } else {
            this.router.navigate(['/annuncio', this.annuncio.id]);
          }
        };

        if (this.nuoveFoto.length > 0) {
          let caricati = 0;
          this.nuoveFoto.forEach(file => {
            this.fotoService.upload(this.annuncio.id, file).subscribe({
              next: () => {
                caricati++;
                if (caricati === this.nuoveFoto.length) dopoFoto();
              },
              error: (err: any) => console.error(err)
            });
          });
        } else {
          dopoFoto();
        }
      },
      error: (err: any) => {
        if (err.error === 'LIMITE_MODIFICHE') {
          this.errore = 'Hai raggiunto il limite massimo di 10 modifiche per questo annuncio.';
        } else {
          this.errore = 'Errore durante il salvataggio';
        }
        this.cdr.detectChanges();
      }
    });
  }

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
Regole: solo testo della descrizione senza titoli, massimo 3 frasi, italiano, tono professionale ma accessibile agli acquirenti o affittuari.`;

    this.http.post<any>(
      'http://localhost:8080/api/ai/descrizione',
      { prompt: testoPrompt },
      { withCredentials: true }
    ).subscribe({
      next: (data) => {
        const testo = data?.candidates?.[0]?.content?.parts?.[0]?.text || '';
        this.annuncio.descrizione = testo.trim();
        this.generandoDescrizione = false;
        this.cdr.detectChanges();
      },
      error: (err) => {
        this.errore = err.status === 429
          ? 'Servizio AI sovraccarico. Riprova tra qualche secondo.'
          : 'Impossibile generare la descrizione al momento.';
        this.generandoDescrizione = false;
        this.cdr.detectChanges();
      }
    });
  }
}