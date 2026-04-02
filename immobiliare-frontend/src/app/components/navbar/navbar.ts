import { Component, OnInit, OnDestroy, ChangeDetectorRef } from '@angular/core';
import { ModalService } from '../../services/modal.service';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../services/auth';
import { AnnuncioService } from '../../services/annuncio';
import { CommonModule } from '@angular/common';
import { Subscription, interval } from 'rxjs';
import { ToastService } from '../../services/toast.service';
import { BadgeService } from '../../services/badge';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './navbar.html',
  styleUrl: './navbar.css'
})
export class Navbar implements OnInit, OnDestroy {
  utente: any = null;
  messaggiCount = 0;
  private subs: Subscription = new Subscription(); // Contenitore per tutte le sottoscrizioni
  mobileOpen = false;

  constructor(
    private authService: AuthService,
    private annuncioService: AnnuncioService,
    private router: Router,
    private cdr: ChangeDetectorRef,
    public modal: ModalService,
    private toast: ToastService,
    private badgeService: BadgeService
  ) {}

  ngOnInit() {
    // 1. ASCOLTO REATTIVO UTENTE (Cervello della Navbar)
    this.subs.add(
      this.authService.utente$.subscribe({
        next: (u) => {
          this.utente = u;
          if (u) {
            // Se l'utente entra, carichiamo i suoi dati
            this.aggiornaBadge();
          } else {
            // Se l'utente esce, PULIZIA TOTALE ISTANTANEA
            this.utente = null;
            this.messaggiCount = 0;
            this.badgeService.reset(); // Reset del servizio badge
          }
          this.cdr.detectChanges(); // Notifica Angular del cambiamento
        }
      })
    );

    // 2. ASCOLTO BADGE MESSAGGI
    this.subs.add(
      this.badgeService.count$.subscribe(count => {
        this.messaggiCount = count;
        this.cdr.detectChanges();
      })
    );

    // 3. POLLING PERIODICO (ogni 30 secondi)
    this.subs.add(
      interval(30000).subscribe(() => {
        if (this.utente) this.aggiornaBadge();
      })
    );
  }

  ngOnDestroy() {
    // Cancella tutte le iscrizioni quando il componente viene distrutto
    this.subs.unsubscribe();
  }

  aggiornaBadge() {
    if (!this.utente) return;
    
    if (this.utente.ruolo === 'VENDITORE') {
      this.annuncioService.countMessaggi(this.utente.id).subscribe({
        next: (count: number) => this.badgeService.setCount(count),
        error: () => {}
      });
    } else if (this.utente.ruolo === 'AMMINISTRATORE') {
      this.annuncioService.countMessaggiAdmin().subscribe({
        next: (count: number) => this.badgeService.setCount(count),
        error: () => {}
      });
    }
  }

  logout() {
    if (confirm('Sei sicuro di voler uscire?')) {
      this.authService.logout().subscribe({
        next: () => {
          this.toast.success('Logout effettuato!');
          this.router.navigate(['/']);
          // Non serve azzerare l'utente qui: lo farà il subscribe nel ngOnInit!
        },
        error: () => {
          // Anche se il server fallisce, puliamo la sessione lato client
          this.authService.clearUtente();
          this.router.navigate(['/']);
        }
      });
    }
  }

  apriLogin() { this.modal.apriLogin(); }
  apriRegistrazione() { this.modal.apriRegistrazione(); }
}