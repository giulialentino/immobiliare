import { Component, OnInit, AfterViewInit, ChangeDetectorRef, HostListener } from '@angular/core';
import { RouterLink } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { AnnuncioService } from '../../services/annuncio';
import { CategoriaService } from '../../services/categoria';
import { AuthService } from '../../services/auth';
import { ModalService } from '../../services/modal.service';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, RouterLink, FormsModule],
  templateUrl: './home.html',
  styleUrl: './home.css'
})
export class Home implements OnInit, AfterViewInit {

  annunci: any[] = [];
  annunciFiltrati: any[] = [];
  ultimiAnnunci: any[] = [];
  categorie: any[] = [];
  loadingCategorie = true;
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
  statoPromozione = 'NESSUNA';
  mostraTornaSu = false;

  constructor(
    private annuncioService: AnnuncioService,
    private categoriaService: CategoriaService,
    private authService: AuthService,
    private http: HttpClient,
    private cdr: ChangeDetectorRef,
    public modal: ModalService // PUBLIC per l'accesso dall'HTML
  ) {}

  @HostListener('window:scroll')
  onScroll() {
    this.mostraTornaSu = window.scrollY > 300;
    this.cdr.detectChanges();
  }

  @HostListener('document:click', ['$event'])
  onDocumentClick(event: MouseEvent) {
    const target = event.target as HTMLElement;
    if (!target.closest('.autocomplete-wrapper')) {
      this.comuniFiltrati = [];
      this.indiceSelezionato = -1;
      this.cdr.detectChanges();
    }
  }

  tornaAlTop() {
    window.scrollTo({ top: 0, behavior: 'smooth' });
  }

  ngOnInit() {
    // Sottoscrizione reattiva: si aggiorna non appena l'utente logga dalla modale
    this.authService.utente$.subscribe({
      next: (u: any) => {
        this.utente = u;
        if (u) {
          if (u.ruolo === 'ACQUIRENTE') {
            this.annuncioService.getStatoPromozione().subscribe({
              next: (stato: string) => {
                this.statoPromozione = stato;
                this.cdr.detectChanges();
              },
              error: () => this.statoPromozione = 'NESSUNA'
            });
          }
          if (u.ruolo === 'VENDITORE' || u.ruolo === 'AMMINISTRATORE') {
            this.annuncioService.countMessaggi(u.id).subscribe({
              next: (count: number) => {
                this.messaggiCount = count;
                this.cdr.detectChanges();
              }
            });
          }
        } else {
          this.messaggiCount = 0;
          this.statoPromozione = 'NESSUNA';
        }
        this.cdr.detectChanges();
      }
    });

    this.http.get<any[]>('assets/comuni.json').subscribe({
      next: (data) => {
        this.comuni = data.map((c: any) => c.nome).sort();
        this.cdr.detectChanges();
      }
    });

    this.caricaCategorie();
    this.caricaAnnunci();
  }

  ngAfterViewInit() {
    const observer = new IntersectionObserver((entries) => {
      entries.forEach(entry => {
        if (entry.isIntersecting) {
          entry.target.classList.add('visible');
        }
      });
    }, { threshold: 0.1 });

    setTimeout(() => {
      document.querySelectorAll('.reveal').forEach(el => observer.observe(el));
    }, 300);
  }

  caricaCategorie() {
    this.loadingCategorie = true;
    this.categoriaService.getAll().subscribe({
      next: (data: any) => {
        this.categorie = data;
        this.loadingCategorie = false;
        this.cdr.detectChanges();
      }
    });
  }

  caricaAnnunci() {
    this.loading = true;
    this.annuncioService.getAll(this.tipoOperazione || undefined, this.idCategoria || undefined).subscribe({
      next: (data: any) => {
        this.annunci = data;
        this.ultimiAnnunci = [...data].slice(0, 3);
        this.applicaFiltri();
        this.loading = false;
        this.cdr.detectChanges();
      }
    });
  }

  applicaFiltri() {
    let lista = [...this.annunci];
    if (this.citta) lista = lista.filter(a => a.indirizzo?.toLowerCase().includes(this.citta.toLowerCase()));
    if (this.prezzoMin) lista = lista.filter(a => a.prezzo >= +this.prezzoMin);
    if (this.prezzoMax) lista = lista.filter(a => a.prezzo <= +this.prezzoMax);
    this.annunciFiltrati = lista;
    this.appliedOrdinamento();
  }

  appliedOrdinamento() {
    let lista = [...this.annunciFiltrati];
    if (this.ordinamento === 'prezzo_asc') lista.sort((a, b) => a.prezzo - b.prezzo);
    else if (this.ordinamento === 'prezzo_desc') lista.sort((a, b) => b.prezzo - a.prezzo);
    else if (this.ordinamento === 'data_desc') lista.sort((a, b) => new Date(b.dataInserimento).getTime() - new Date(a.dataInserimento).getTime());
    this.annunciFiltrati = lista;
    this.cdr.detectChanges();
  }

  filtraComuni() {
    if (this.citta.length < 2) { this.comuniFiltrati = []; return; }
    this.comuniFiltrati = this.comuni.filter(c => c.toLowerCase().startsWith(this.citta.toLowerCase())).slice(0, 8);
  }

  selezionaComune(c: string) {
    this.citta = c;
    this.comuniFiltrati = [];
    this.caricaAnnunci();
  }

  gestisciTasto(event: KeyboardEvent) {
    if (event.key === 'ArrowDown') {
      this.indiceSelezionato = Math.min(this.indiceSelezionato + 1, this.comuniFiltrati.length - 1);
    } else if (event.key === 'ArrowUp') {
      this.indiceSelezionato = Math.max(this.indiceSelezionato - 1, -1);
    } else if (event.key === 'Enter') {
      if (this.indiceSelezionato >= 0) this.citta = this.comuniFiltrati[this.indiceSelezionato];
      this.filtra();
    }
  }

  filtra() {
    this.comuniFiltrati = [];
    this.caricaAnnunci();
    setTimeout(() => document.getElementById('tutti-annunci')?.scrollIntoView({ behavior: 'smooth' }), 100);
  }

  filtraPerCategoria(id: number) {
    this.idCategoria = id;
    this.caricaAnnunci();
  }

  resetFiltri() {
    this.tipoOperazione = ''; this.idCategoria = null; this.citta = ''; this.prezzoMin = null; this.prezzoMax = null;
    this.caricaAnnunci();
  }

  richiediPromozione() {
    if (!confirm('Vuoi diventare un venditore Domus?')) return;
    this.annuncioService.richiediPromozione().subscribe(() => {
      this.statoPromozione = 'IN_ATTESA';
      this.cdr.detectChanges();
    });
  }
}