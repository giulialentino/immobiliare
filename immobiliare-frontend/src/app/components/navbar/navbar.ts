import { Component, OnInit, OnDestroy, ChangeDetectorRef } from '@angular/core';
import { Router, RouterLink, NavigationEnd } from '@angular/router';
import { AuthService } from '../../services/auth';
import { AnnuncioService } from '../../services/annuncio';
import { CommonModule } from '@angular/common';
import { filter } from 'rxjs/operators';
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
  private routerSub!: Subscription;
  private pollSub!: Subscription;
  private badgeSub!: Subscription;
  mobileOpen = false;

  constructor(
    private authService: AuthService,
    private annuncioService: AnnuncioService,
    private router: Router,
    private cdr: ChangeDetectorRef,
    private toast: ToastService,
    private badgeService: BadgeService
  ) {}

  ngOnInit() {
    this.badgeSub = this.badgeService.count$.subscribe(count => {
      this.messaggiCount = count;
      this.cdr.detectChanges();
    });
    this.caricaUtente();
    this.routerSub = this.router.events.pipe(
      filter(event => event instanceof NavigationEnd)
    ).subscribe(() => {
      this.caricaUtente();
    });
    this.pollSub = interval(30000).subscribe(() => {
      this.aggiornaBadge();
    });
  }

  ngOnDestroy() {
    if (this.routerSub) this.routerSub.unsubscribe();
    if (this.pollSub) this.pollSub.unsubscribe();
    if (this.badgeSub) this.badgeSub.unsubscribe();
  }

  caricaUtente() {
    this.authService.getUtenteLoggato().subscribe({
      next: (u: any) => {
        this.utente = u;
        this.aggiornaBadge();
        this.cdr.detectChanges();
      },
      error: () => {
        this.utente = null;
        this.messaggiCount = 0;
        this.badgeService.reset();
        this.authService.clearUtente();
        this.cdr.detectChanges();
      }
    });
  }

  aggiornaBadge() {
    if (!this.utente) return;
    if (this.utente.ruolo === 'VENDITORE') {
      this.annuncioService.countMessaggi(this.utente.id).subscribe({
        next: (count: number) => { this.badgeService.setCount(count); this.cdr.detectChanges(); },
        error: () => {}
      });
    } else if (this.utente.ruolo === 'AMMINISTRATORE') {
      this.annuncioService.countMessaggiAdmin().subscribe({
        next: (count: number) => { this.badgeService.setCount(count); this.cdr.detectChanges(); },
        error: () => {}
      });
    } else {
      this.badgeService.setCount(0);
    }
  }

  logout() {
    if (confirm('Sei sicuro di voler uscire?')) {
      this.authService.logout().subscribe({
        next: () => {
          this.utente = null;
          this.messaggiCount = 0;
          this.badgeService.reset();
          this.authService.clearUtente();
          this.cdr.detectChanges();
          this.toast.success('Logout effettuato!');
          this.router.navigate(['/']);
        },
        error: () => {
          this.utente = null;
          this.authService.clearUtente();
          this.cdr.detectChanges();
          this.router.navigate(['/']);
        }
      });
    }
  }
}