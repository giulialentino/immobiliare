import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { RouterLink } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { AnnuncioService } from '../../services/annuncio';
import { CategoriaService } from '../../services/categoria';
import { AuthService } from '../../services/auth';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, RouterLink, FormsModule],
  templateUrl: './home.html',
  styleUrl: './home.css'
})
export class Home implements OnInit {

  annunci: any[] = [];
  annunciFiltrati: any[] = [];
  ultimiAnnunci: any[] = [];
  categorie: any[] = [];
  utente: any = null;
  messaggiCount = 0;
  tipoOperazione = '';
  idCategoria: any = null;
  ordinamento = '';
  citta = '';
  prezzoMin: any = null;
  prezzoMax: any = null;
  vistaGriglia = true;
  loading = true;
  comuni: string[] = [];
  comuniFiltrati: string[] = [];
  indiceSelezionato = -1;
  richiestaInviata = false;
  richiestaErrore = '';
  statoPromozione='NESSUNA';

  constructor(
    private annuncioService: AnnuncioService,
    private categoriaService: CategoriaService,
    private authService: AuthService,
    private http: HttpClient,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit() {
    this.authService.getUtenteLoggato().subscribe({
      next: (u: any) => {
        this.utente = u;
        this.cdr.detectChanges();
        if (u && u.ruolo === 'ACQUIRENTE') {
  this.annuncioService.getStatoPromozione().subscribe({
    next: (stato: string) => {
      this.statoPromozione = stato;
      this.cdr.detectChanges();
    },
    error: () => this.statoPromozione = 'NESSUNA'
  });
}
        if (u && (u.ruolo === 'VENDITORE' || u.ruolo === 'AMMINISTRATORE')) {
          this.annuncioService.countMessaggi(u.id).subscribe({
            next: (count: number) => {
              this.messaggiCount = count;
              this.cdr.detectChanges();
            },
            error: () => this.messaggiCount = 0
          });
        } else {
          this.messaggiCount = 0;
        }
      },
      error: () => this.utente = null
    });

    this.http.get<any[]>('assets/comuni.json').subscribe({
      next: (data) => {
        this.comuni = data.map((c: any) => c.nome).sort();
        this.cdr.detectChanges();
      },
      error: () => console.error('Errore caricamento comuni')
    });

    this.caricaCategorie();
    this.caricaAnnunci();
  }

  caricaCategorie() {
    this.categoriaService.getAll().subscribe({
      next: (data: any) => { this.categorie = data; this.cdr.detectChanges(); },
      error: (err: any) => console.error(err)
    });
  }

  caricaAnnunci() {
    this.loading = true;
    this.cdr.detectChanges();
    this.annuncioService.getAll(
      this.tipoOperazione || undefined,
      this.idCategoria || undefined
    ).subscribe({
      next: (data: any) => {
        this.annunci = data;
        this.ultimiAnnunci = [...data].slice(0, 3);
        this.applicaFiltri();
        this.loading = false;
        this.cdr.detectChanges();
      },
      error: (err: any) => {
        console.error(err);
        this.loading = false;
        this.cdr.detectChanges();
      }
    });
  }

  applicaFiltri() {
    let lista = [...this.annunci];
    if (this.citta) {
      lista = lista.filter(a =>
        a.indirizzo && a.indirizzo.toLowerCase().includes(this.citta.toLowerCase())
      );
    }
    if (this.prezzoMin) lista = lista.filter(a => a.prezzo >= +this.prezzoMin);
    if (this.prezzoMax) lista = lista.filter(a => a.prezzo <= +this.prezzoMax);
    this.annunciFiltrati = lista;
    this.appliedOrdinamento();
    this.cdr.detectChanges();
  }

  appliedOrdinamento() {
    let lista = [...this.annunciFiltrati];
    if (this.ordinamento === 'prezzo_asc') lista.sort((a, b) => a.prezzo - b.prezzo);
    else if (this.ordinamento === 'prezzo_desc') lista.sort((a, b) => b.prezzo - a.prezzo);
    else if (this.ordinamento === 'mq_asc') lista.sort((a, b) => a.metriQuadri - b.metriQuadri);
    else if (this.ordinamento === 'mq_desc') lista.sort((a, b) => b.metriQuadri - a.metriQuadri);
    else if (this.ordinamento === 'data_desc') lista.sort((a, b) =>
      new Date(b.dataInserimento).getTime() - new Date(a.dataInserimento).getTime());
    else if (this.ordinamento === 'data_asc') lista.sort((a, b) =>
      new Date(a.dataInserimento).getTime() - new Date(b.dataInserimento).getTime());
    this.annunciFiltrati = lista;
    this.cdr.detectChanges();
  }

  filtraComuni() {
    if (this.citta.length < 2) {
      this.comuniFiltrati = [];
      this.indiceSelezionato = -1;
      return;
    }
    this.comuniFiltrati = this.comuni
      .filter(c => c.toLowerCase().startsWith(this.citta.toLowerCase()))
      .slice(0, 8);
    this.indiceSelezionato = -1;
  }

  selezionaComune(c: string) {
    this.citta = c;
    this.comuniFiltrati = [];
    this.indiceSelezionato = -1;
    this.caricaAnnunci();
  }

  gestisciTasto(event: KeyboardEvent) {
    if (this.comuniFiltrati.length === 0) {
      if (event.key === 'Enter') this.filtra();
      return;
    }
    if (event.key === 'ArrowDown') {
      event.preventDefault();
      this.indiceSelezionato = Math.min(this.indiceSelezionato + 1, this.comuniFiltrati.length - 1);
    } else if (event.key === 'ArrowUp') {
      event.preventDefault();
      this.indiceSelezionato = Math.max(this.indiceSelezionato - 1, -1);
    } else if (event.key === 'Enter') {
      if (this.indiceSelezionato >= 0) {
        this.citta = this.comuniFiltrati[this.indiceSelezionato];
        this.comuniFiltrati = [];
        this.indiceSelezionato = -1;
      }
      this.filtra();
    } else if (event.key === 'Escape') {
      this.comuniFiltrati = [];
      this.indiceSelezionato = -1;
    }
  }

  filtra() {
    this.comuniFiltrati = [];
    this.loading = true;
    this.cdr.detectChanges();
    this.annuncioService.getAll(
      this.tipoOperazione || undefined,
      this.idCategoria || undefined
    ).subscribe({
      next: (data: any) => {
        this.annunci = data;
        this.ultimiAnnunci = [...data].slice(0, 3);
        this.applicaFiltri();
        this.loading = false;
        this.cdr.detectChanges();
        setTimeout(() => {
          const el = document.getElementById('tutti-annunci');
          if (el) el.scrollIntoView({ behavior: 'smooth' });
        }, 100);
      },
      error: (err: any) => {
        console.error(err);
        this.loading = false;
        this.cdr.detectChanges();
      }
    });
  }

  filtraPerCategoria(id: number) {
    this.idCategoria = id;
    this.caricaAnnunci();
  }

  resetFiltri() {
    this.tipoOperazione = '';
    this.idCategoria = null;
    this.ordinamento = '';
    this.citta = '';
    this.prezzoMin = null;
    this.prezzoMax = null;
    this.comuniFiltrati = [];
    this.caricaAnnunci();
  }

  richiediPromozione() {
  this.richiestaErrore = '';
  if (!confirm('Sei sicuro di voler richiedere di diventare venditore?')) return;
  this.annuncioService.richiediPromozione().subscribe({
    next: () => {
      this.statoPromozione = 'IN_ATTESA';
      this.richiestaInviata = true;
      this.cdr.detectChanges();
    },
    error: (err: any) => {
      this.richiestaErrore = err.error || 'Errore durante la richiesta';
      this.cdr.detectChanges();
    }
  });
}
}