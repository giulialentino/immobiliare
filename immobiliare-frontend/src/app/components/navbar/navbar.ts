import { Component, OnInit, OnDestroy, ChangeDetectorRef, ApplicationRef } from '@angular/core';
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
  private subs: Subscription = new Subscription();
  mobileOpen = false;

  constructor(
    private authService: AuthService,
    private annuncioService: AnnuncioService,
    private router: Router,
    private cdr: ChangeDetectorRef,
    private appRef: ApplicationRef,
    public modal: ModalService,
    private toast: ToastService,
    private badgeService: BadgeService
  ) {}

  ngOnInit() {
    // 1. Ascolto reattivo utente
    this.subs.add(
      this.authService.utente$.subscribe({
        next: (u) => {
          this.utente = u;
          if (u) {
            this.aggiornaBadge();
          } else {
            this.utente = null;
            this.messaggiCount = 0;
            this.badgeService.reset();
          }
          this.cdr.detectChanges();
          this.appRef.tick();
        }
      })
    );

    // 2. Ascolto badge messaggi
    this.subs.add(
      this.badgeService.count$.subscribe(count => {
        this.messaggiCount = count;
        this.cdr.detectChanges();
      })
    );

    // 3. Polling periodico ogni 30 secondi
    this.subs.add(
      interval(30000).subscribe(() => {
        if (this.utente) this.aggiornaBadge();
      })
    );

    // 4. Ricarica utente quando login dal modal
    this.subs.add(
      this.modal.loginEffettuato$.subscribe(() => {
        setTimeout(() => {
          this.authService.getUtenteLoggato().subscribe({
            next: (u) => {
              this.utente = u;
              if (u) this.aggiornaBadge();
              this.cdr.detectChanges();
              this.appRef.tick();
            },
            error: () => {}
          });
        }, 200);
      })
    );
  }

  ngOnDestroy() {
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
        },
        error: () => {
          this.authService.clearUtente();
          this.router.navigate(['/']);
        }
      });
    }
  }

  apriLogin() { this.modal.apriLogin(); }
  apriRegistrazione() { this.modal.apriRegistrazione(); }
}