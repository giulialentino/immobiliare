import { DomSanitizer, SafeResourceUrl } from '@angular/platform-browser';
import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AnnuncioService } from '../../services/annuncio';
import { AuthService } from '../../services/auth';
import { AstaService } from '../../services/asta';
import { FotoService } from '../../services/foto';
import { PreferitoService } from '../../services/preferito';

@Component({
  selector: 'app-dettaglio-annuncio',
  standalone: true,
  imports: [CommonModule, RouterLink, FormsModule],
  templateUrl: './dettaglio-annuncio.html',
  styleUrl: './dettaglio-annuncio.css'
})
export class DettaglioAnnuncio implements OnInit {

  annuncio: any = null;
  utente: any = null;
  asta: any = null;
  offerte: any[] = [];
  nuovaOfferta: number = 0;
  offertaInviata = false;
  nuovaAsta: any = { prezzoBase: null, dataScadenza: '' };
  astaCreata = false;
  erroreAsta = '';
  nuovaRecensione = { punteggio: 5, commento: '' };
  messaggioInviato = false;
  recensioneInviata = false;
  nuovoPrezzo: any = null;
  prezzoRibassato = false;
  mappaUrl: SafeResourceUrl | null = null;
  fotoSelezionata = 0;
  lightboxAperto = false;
  isPreferito = false;
  oggi = new Date();

  constructor(
    private route: ActivatedRoute,
    private annuncioService: AnnuncioService,
    private authService: AuthService,
    private astaService: AstaService,
    private fotoService: FotoService,
    private preferitoService: PreferitoService,
    private router: Router,
    private sanitizer: DomSanitizer,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit() {
    this.authService.getUtenteLoggato().subscribe({
      next: (u: any) => {
        this.utente = u;
        this.cdr.detectChanges();
      },
      error: () => this.utente = null
    });

    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.annuncioService.getById(+id).subscribe({
        next: (data: any) => {
          this.annuncio = data;
          this.caricaAsta();
          if (data.latitudine && data.longitudine) {
            const url = `https://www.openstreetmap.org/export/embed.html?bbox=${data.longitudine - 0.01},${data.latitudine - 0.01},${data.longitudine + 0.01},${data.latitudine + 0.01}&layer=mapnik&marker=${data.latitudine},${data.longitudine}`;
            this.mappaUrl = this.sanitizer.bypassSecurityTrustResourceUrl(url);
          }
          this.preferitoService.isPreferito(data.id).subscribe({
            next: (result: boolean) => {
              this.isPreferito = result;
              this.cdr.detectChanges();
            },
            error: () => this.isPreferito = false
          });
          this.cdr.detectChanges();
        },
        error: (err: any) => console.error('Errore:', err)
      });
    }
  }

  get dataMinima(): string {
    const now = new Date();
    now.setMinutes(now.getMinutes() - now.getTimezoneOffset());
    return now.toISOString().slice(0, 16);
  }

  dataValida(): boolean {
    if (!this.nuovaAsta.dataScadenza) return false;
    return new Date(this.nuovaAsta.dataScadenza) > new Date();
  }

  apriLightbox(i: number) {
    this.fotoSelezionata = i;
    this.lightboxAperto = true;
  }

  chiudiLightbox() {
    this.lightboxAperto = false;
  }

  selezionaFoto(i: number) {
  this.fotoSelezionata = i;
  setTimeout(() => {
    const carousel = document.getElementById('caroselloFoto');
    if (carousel) {
      let bootstrapCarousel = (window as any).bootstrap?.Carousel?.getInstance(carousel);
      if (!bootstrapCarousel) {
        bootstrapCarousel = new (window as any).bootstrap.Carousel(carousel);
      }
      bootstrapCarousel.to(i);
    }
  }, 50);
}

  eliminaFoto(foto: string, index: number) {
    if (confirm('Eliminare questa foto?')) {
      this.fotoService.elimina(this.annuncio.id, foto).subscribe({
        next: () => {
          this.annuncio.foto.splice(index, 1);
          if (this.fotoSelezionata >= this.annuncio.foto.length) {
            this.fotoSelezionata = 0;
          }
          this.cdr.detectChanges();
        },
        error: (err: any) => console.error(err)
      });
    }
  }

  caricaAsta() {
    this.astaService.getByAnnuncio(this.annuncio.id).subscribe({
      next: (data: any) => {
        this.asta = data;
        this.caricaOfferte();
        this.cdr.detectChanges();
      },
      error: () => this.asta = null
    });
  }

  caricaOfferte() {
    if (!this.asta) return;
    this.astaService.getOfferte(this.asta.id).subscribe({
      next: (data: any) => {
        this.offerte = data;
        this.cdr.detectChanges();
      },
      error: (err: any) => console.error(err)
    });
  }

  togglePreferito() {
    if (!this.annuncio) return;
    if (this.isPreferito) {
      this.preferitoService.rimuovi(this.annuncio.id).subscribe({
        next: () => { this.isPreferito = false; this.cdr.detectChanges(); },
        error: (err: any) => console.error(err)
      });
    } else {
      this.preferitoService.aggiungi(this.annuncio.id).subscribe({
        next: () => { this.isPreferito = true; this.cdr.detectChanges(); },
        error: (err: any) => console.error(err)
      });
    }
  }

  creaAsta() {
    this.erroreAsta = '';
    if (!this.nuovaAsta.prezzoBase || !this.nuovaAsta.dataScadenza) {
      this.erroreAsta = 'Inserisci prezzo base e data scadenza';
      return;
    }
    if (!this.dataValida()) {
      this.erroreAsta = 'La data deve essere successiva alla data attuale';
      return;
    }
    const asta = {
      idAnnuncio: this.annuncio.id,
      prezzoBase: this.nuovaAsta.prezzoBase,
      dataScadenza: this.nuovaAsta.dataScadenza
    };
    this.astaService.crea(asta).subscribe({
      next: (data: any) => {
        this.asta = data;
        this.astaCreata = true;
        this.annuncio.inAsta = true;
        this.nuovaAsta = { prezzoBase: null, dataScadenza: '' };
        this.cdr.detectChanges();
      },
      error: (err: any) => console.error(err)
    });
  }

  faiOfferta() {
    if (!this.nuovaOfferta || !this.asta) return;
    this.astaService.faiOfferta(this.asta.id, this.nuovaOfferta).subscribe({
      next: () => {
        this.offertaInviata = true;
        this.asta.offertaMax = this.nuovaOfferta;
        this.caricaOfferte();
        this.cdr.detectChanges();
      },
      error: (err: any) => console.error(err)
    });
  }

  

  ribassaPrezzo() {
    if (!this.nuovoPrezzo || !this.annuncio) return;
    this.annuncioService.ribassaPrezzo(this.annuncio.id, this.nuovoPrezzo).subscribe({
      next: () => {
        this.prezzoRibassato = true;
        this.annuncio.prezzoRibassato = this.nuovoPrezzo;
        this.cdr.detectChanges();
      },
      error: (err: any) => console.error(err)
    });
  }

  annullaRibasso() {
    this.annuncioService.annullaRibasso(this.annuncio.id).subscribe({
      next: () => {
        this.annuncio.prezzoRibassato = null;
        this.prezzoRibassato = false;
        this.cdr.detectChanges();
      },
      error: (err: any) => console.error(err)
    });
  }

  eliminaAnnuncio() {
    if (!this.annuncio) return;
    if (confirm('Sei sicuro di voler eliminare questo annuncio?')) {
      this.annuncioService.elimina(this.annuncio.id).subscribe({
        next: () => this.router.navigate(['/']),
        error: (err: any) => console.error(err)
      });
    }
  }

  inviaMessaggioStandard() {
    if (!this.annuncio || !this.utente) return;
    const messaggio = {
      idAnnuncio: this.annuncio.id,
      oggetto: `Interesse per: ${this.annuncio.titolo}`,
      testo: `Salve, sono interessato/a all'annuncio "${this.annuncio.titolo}". Contattatemi via email (${this.utente.email}) per ulteriori informazioni. Grazie.`
    };
    this.annuncioService.inviaMessaggio(messaggio).subscribe({
      next: () => {
        this.messaggioInviato = true;
        this.cdr.detectChanges();
      },
      error: (err: any) => console.error(err)
    });
  }

  inviaRecensione() {
    if (!this.annuncio) return;
    const recensione = {
      idAnnuncio: this.annuncio.id,
      punteggio: this.nuovaRecensione.punteggio,
      commento: this.nuovaRecensione.commento
    };
    this.annuncioService.inviaRecensione(recensione).subscribe({
      next: () => {
        this.recensioneInviata = true;
        this.annuncio.recensioni.push(recensione);
        this.nuovaRecensione = { punteggio: 5, commento: '' };
        this.cdr.detectChanges();
      },
      error: (err: any) => console.error(err)
    });
  }

  promuoviSuFacebook() {
    const url = `http://localhost:4200/annuncio/${this.annuncio.id}`;
    const facebookUrl = `https://www.facebook.com/sharer/sharer.php?u=${encodeURIComponent(url)}&quote=${encodeURIComponent(this.annuncio.titolo + ' - €' + this.annuncio.prezzo)}`;
    window.open(facebookUrl, '_blank', 'width=600,height=400');
  }
  

  

chiudiAsta() {
  if (!this.asta) return;
  if (!confirm('Sei sicuro di voler chiudere l\'asta?')) return;
  this.astaService.chiudi(this.asta.id).subscribe({
    next: () => {
      // Ricarica annuncio e asta completamente
      this.annuncioService.getById(this.annuncio.id).subscribe({
        next: (data: any) => {
          this.annuncio = data;
          this.asta = null;
          this.offerte = [];
          this.cdr.detectChanges();
        }
      });
    },
    error: (err: any) => console.error(err)
  });
}
}