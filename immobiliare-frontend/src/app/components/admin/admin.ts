import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../services/auth';
import { AnnuncioService } from '../../services/annuncio';
import { BadgeService } from '../../services/badge';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-admin',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './admin.html',
  styleUrl: './admin.css'
})
export class Admin implements OnInit {

  utenti: any[] = [];
  annunci: any[] = [];
  annunciInAttesa: any[] = [];
  messaggiAdmin: any[] = [];
  messaggioAdminAperto: any = null;
  sezione = 'messaggi';
  approvazioniCount = 0;
  segnalazioni: any[] = [];
  segnalazioniInAttesa: any[] = [];

  constructor(
    private authService: AuthService,
    private annuncioService: AnnuncioService,
    private router: Router,
    private cdr: ChangeDetectorRef,
    private badgeService: BadgeService,
    private http: HttpClient
  ) {}

  ngOnInit() {
    this.authService.getUtenteLoggato().subscribe({
      next: (u) => {
        if (!u || u.ruolo !== 'AMMINISTRATORE') {
          this.router.navigate(['/']);
        } else {
          this.caricaUtenti();
          this.caricaAnnunci();
          this.caricaInAttesa();
          this.caricaMessaggiAdmin();
          this.caricaSegnalazioni();
        }
      },
      error: () => this.router.navigate(['/login'])
    });
  }

  caricaUtenti() {
    this.authService.getUtenti().subscribe({
      next: (data) => { this.utenti = data; this.cdr.detectChanges(); },
      error: (err: any) => console.error(err)
    });
  }

  caricaAnnunci() {
    this.annuncioService.getAll().subscribe({
      next: (data) => { this.annunci = data; this.cdr.detectChanges(); },
      error: (err: any) => console.error(err)
    });
  }

  caricaInAttesa() {
    this.annuncioService.getInAttesa().subscribe({
      next: (data) => { this.annunciInAttesa = data; this.cdr.detectChanges(); },
      error: (err: any) => console.error(err)
    });
  }

  caricaMessaggiAdmin() {
    this.annuncioService.getMessaggiAdmin().subscribe({
      next: (data) => {
        this.messaggiAdmin = data;
        this.approvazioniCount = data.filter((m: any) => !m.letto).length;
        this.badgeService.setCount(this.approvazioniCount);
        this.cdr.detectChanges();
      },
      error: (err: any) => console.error(err)
    });
  }

  apriMessaggioAdmin(m: any) {
    this.messaggioAdminAperto = this.messaggioAdminAperto?.id === m.id ? null : m;
    if (!m.letto) {
      m.letto = true;
      this.approvazioniCount = Math.max(0, this.approvazioniCount - 1);
      this.badgeService.setCount(this.approvazioniCount);
      this.annuncioService.segnaMessaggioLetto(m.id).subscribe();
      this.cdr.detectChanges();
    }
    this.cdr.detectChanges();
  }

  get nonLettiAdminCount(): number {
    return this.messaggiAdmin.filter((m: any) => !m.letto).length;
  }

  getStatoAnnuncio(idAnnuncio: number): string {
    const tuttiAnnunci = [...this.annunci, ...this.annunciInAttesa];
    const a = tuttiAnnunci.find(ann => ann.id === idAnnuncio);
    return a ? a.stato : 'IN_ATTESA';
  }

  approva(idAnnuncio: number) {
    if (!confirm('Sei sicuro di voler approvare questo annuncio?')) return;
    this.annuncioService.aggiornaStato(idAnnuncio, 'APPROVATO').subscribe({
      next: () => {
        this.annunciInAttesa = this.annunciInAttesa.filter(a => a.id !== idAnnuncio);
        const a = this.annunci.find(ann => ann.id === idAnnuncio);
        if (a) a.stato = 'APPROVATO';
        else this.annunci.push({ id: idAnnuncio, stato: 'APPROVATO' });
        this.cdr.detectChanges();
      },
      error: (err: any) => console.error(err)
    });
  }

  rifiuta(idAnnuncio: number) {
    if (!confirm('Sei sicuro di voler rifiutare questo annuncio?')) return;
    this.annuncioService.aggiornaStato(idAnnuncio, 'RIFIUTATO').subscribe({
      next: () => {
        this.annunciInAttesa = this.annunciInAttesa.filter(a => a.id !== idAnnuncio);
        const a = this.annunci.find(ann => ann.id === idAnnuncio);
        if (a) a.stato = 'RIFIUTATO';
        else this.annunci.push({ id: idAnnuncio, stato: 'RIFIUTATO' });
        this.cdr.detectChanges();
      },
      error: (err: any) => console.error(err)
    });
  }

  banna(id: number) {
    this.authService.banna(id).subscribe({
      next: () => { this.caricaUtenti(); this.cdr.detectChanges(); },
      error: (err: any) => console.error(err)
    });
  }

  promuovi(id: number) {
    this.authService.promuovi(id).subscribe({
      next: () => { this.caricaUtenti(); this.cdr.detectChanges(); },
      error: (err: any) => console.error(err)
    });
  }

  eliminaAnnuncio(id: number) {
    if (!confirm('Eliminare questo annuncio?')) return;
    this.annuncioService.elimina(id).subscribe({
      next: () => { this.caricaAnnunci(); this.cdr.detectChanges(); },
      error: (err: any) => console.error(err)
    });
  }

  caricaSegnalazioni() {
    this.http.get<any[]>('http://localhost:8080/api/segnalazioni', { withCredentials: true }).subscribe({
      next: (data) => {
        this.segnalazioni = data;
        this.segnalazioniInAttesa = data.filter((s: any) => s.stato === 'IN_ATTESA');
        this.cdr.detectChanges();
      },
      error: (err: any) => console.error(err)
    });
  }

  segnaGestita(id: number) {
    this.http.patch(`http://localhost:8080/api/segnalazioni/${id}/gestita`, {}, { withCredentials: true }).subscribe({
      next: () => {
        this.segnalazioni = this.segnalazioni.map(seg =>
          seg.id === id ? { ...seg, stato: 'GESTITA' } : seg
        );
        this.segnalazioniInAttesa = this.segnalazioni.filter((seg: any) => seg.stato === 'IN_ATTESA');
        this.cdr.detectChanges();
      },
      error: (err: any) => console.error(err)
    });
  }

  approvaPromozione(idUtente: number) {
    if (!confirm('Sei sicuro di voler promuovere questo utente a venditore?')) return;
    this.annuncioService.approvaPromozione(idUtente).subscribe({
      next: () => {
        const u = this.utenti.find(ut => ut.id === idUtente);
        if (u) u.ruolo = 'VENDITORE';
        const m = this.messaggiAdmin.find(msg =>
          msg.idMittente === idUtente && msg.oggetto.includes('promozione')
        );
        if (m) m.statoPromozione = 'APPROVATO';
        this.messaggioAdminAperto = null;
        this.cdr.detectChanges();
      },
      error: (err: any) => console.error(err)
    });
  }

  rifiutaPromozione(idUtente: number) {
    if (!confirm('Sei sicuro di voler rifiutare questa richiesta?')) return;
    this.annuncioService.rifiutaPromozione(idUtente).subscribe({
      next: () => {
        const m = this.messaggiAdmin.find(msg =>
          msg.idMittente === idUtente && msg.oggetto.includes('promozione')
        );
        if (m) m.statoPromozione = 'RIFIUTATO';
        this.messaggioAdminAperto = null;
        this.cdr.detectChanges();
      },
      error: (err: any) => console.error(err)
    });
  }
}