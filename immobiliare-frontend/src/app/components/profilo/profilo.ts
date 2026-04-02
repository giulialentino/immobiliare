import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink, ActivatedRoute, Router } from '@angular/router';
import { AuthService } from '../../services/auth';
import { AnnuncioService } from '../../services/annuncio';
import { PreferitoService } from '../../services/preferito';
import { BadgeService } from '../../services/badge';

@Component({
  selector: 'app-profilo',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './profilo.html',
  styleUrl: './profilo.css'
})
export class Profilo implements OnInit {

  utente: any = null;
  annunci: any[] = [];
  annunciInAttesa: any[] = [];
  annunciRifiutati: any[] = [];
  messaggi: any[] = [];
  messaggiInviati: any[] = [];
  messaggiAdmin: any[] = [];
  modificando = false;
  descrizione = '';
  salvatoOk = false;
  profiloProprio = true;
  sezioneAttiva = 'annunci';
  preferiti: any[] = [];
  messaggioAperto: any = null;
  messaggioInviatoAperto: any = null;
  messaggioAdminAperto: any = null;
  statoPromozione = 'NESSUNA';
  fotoProfilo: string | null = null;
  fileAvatar: File | null = null;
  vecchiaPassword = '';
  nuovaPassword = '';
  confermaNuovaPassword = '';
  cambioPasswordOk = false;
  cambioPasswordErrore = '';
  mostraCambioPassword = false;

  constructor(
    private authService: AuthService,
    private annuncioService: AnnuncioService,
    private route: ActivatedRoute,
    private router: Router,
    private preferitoService: PreferitoService,
    private cdr: ChangeDetectorRef,
    private badgeService: BadgeService
  ) {}

  ngOnInit() {
    this.authService.getUtenteLoggato().subscribe({
      next: (u: any) => {
        this.utente = u;
        this.fotoProfilo = u.fotoProfilo || null;
        const id = this.route.snapshot.paramMap.get('id');
        if (id) {
  this.profiloProprio = false;
  this.authService.getUtenteById(+id).subscribe({
    next: (u: any) => {
      this.utente = u;
      this.fotoProfilo = u.fotoProfilo || null;
      this.caricaAnnunci(+id);
      this.cdr.detectChanges();
    },
    error: () => {
      this.utente = { id: +id, nome: 'Venditore', cognome: '', ruolo: 'VENDITORE' };
      this.caricaAnnunci(+id);
    }
  });
}else {
          this.profiloProprio = true;
          this.descrizione = u.descrizione || '';
          this.preferitoService.getPreferiti().subscribe({
            next: (data: any) => { this.preferiti = data; this.cdr.detectChanges(); },
            error: () => this.preferiti = []
          });
          if (u.ruolo === 'VENDITORE') {
            this.caricaAnnunci(u.id);
            this.caricaMessaggi(u.id);
            this.sezioneAttiva = 'annunci';
          } else if (u.ruolo === 'AMMINISTRATORE') {
            this.caricaAnnunci(u.id);
            this.caricaMessaggiAdmin();
            this.sezioneAttiva = 'messaggi_admin';
          } else if (u.ruolo === 'ACQUIRENTE') {
            this.caricaMessaggiInviati();
            this.sezioneAttiva = 'preferiti';
            this.annuncioService.getStatoPromozione().subscribe({
              next: (stato: string) => {
                this.statoPromozione = stato;
                this.cdr.detectChanges();
              },
              error: () => this.statoPromozione = 'NESSUNA'
            });
          }
        }
        this.cdr.detectChanges();
      },
      error: () => { this.utente = null; this.cdr.detectChanges(); }
    });
  }

  caricaAnnunci(idVenditore: number) {
    this.annuncioService.getTuttiAnnunciVenditore(idVenditore).subscribe({
      next: (tutti: any) => {
        this.annunci = tutti.filter((a: any) => a.stato === 'APPROVATO');
        this.annunciInAttesa = tutti.filter((a: any) => a.stato === 'IN_ATTESA');
        this.annunciRifiutati = tutti.filter((a: any) => a.stato === 'RIFIUTATO');
        this.cdr.detectChanges();
      },
      error: (err: any) => console.error(err)
    });
  }

  caricaMessaggi(idVenditore: number) {
    this.annuncioService.getMessaggiVenditore(idVenditore).subscribe({
      next: (data: any) => {
        this.messaggi = data;
        const nonLetti = data.filter((m: any) => !m.letto).length;
        this.badgeService.setCount(nonLetti);
        this.cdr.detectChanges();
      },
      error: (err: any) => console.error(err)
    });
  }

  caricaMessaggiInviati() {
    this.annuncioService.getMessaggiInviati().subscribe({
      next: (data: any) => { this.messaggiInviati = data; this.cdr.detectChanges(); },
      error: () => this.messaggiInviati = []
    });
  }

  caricaMessaggiAdmin() {
    this.annuncioService.getMessaggiAdmin().subscribe({
      next: (data: any) => {
        this.messaggiAdmin = data;
        const nonLetti = data.filter((m: any) => !m.letto).length;
        this.badgeService.setCount(nonLetti);
        this.cdr.detectChanges();
      },
      error: (err: any) => console.error(err)
    });
  }

  apriMessaggio(m: any) {
    this.messaggioAperto = this.messaggioAperto?.id === m.id ? null : m;
    if (!m.letto) {
      m.letto = true;
      this.annuncioService.segnaMessaggioLetto(m.id).subscribe({
        next: () => {
          this.badgeService.decrement(1);
          this.cdr.detectChanges();
        }
      });
    }
    this.cdr.detectChanges();
  }

  apriMessaggioInviato(m: any) {
    this.messaggioInviatoAperto = this.messaggioInviatoAperto?.id === m.id ? null : m;
    this.cdr.detectChanges();
  }

  apriMessaggioAdmin(m: any) {
    this.messaggioAdminAperto = this.messaggioAdminAperto?.id === m.id ? null : m;
    if (!m.letto) {
      m.letto = true;
      this.annuncioService.segnaMessaggioLetto(m.id).subscribe({
        next: () => {
          this.badgeService.decrement(1);
          this.cdr.detectChanges();
        }
      });
    }
    this.cdr.detectChanges();
  }

  vaiAdApprovazioni() {
    this.router.navigate(['/admin']);
  }

  eliminaMessaggio(m: any) {
    if (!confirm('Eliminare questo messaggio?')) return;
    this.annuncioService.eliminaMessaggio(m.id).subscribe({
      next: () => {
        this.messaggi = this.messaggi.filter(msg => msg.id !== m.id);
        if (this.messaggioAperto?.id === m.id) this.messaggioAperto = null;
        const nonLetti = this.messaggi.filter(msg => !msg.letto).length;
        this.badgeService.setCount(nonLetti);
        this.cdr.detectChanges();
      },
      error: (err: any) => console.error(err)
    });
  }

  eliminaMessaggioInviato(m: any) {
    if (!confirm('Eliminare questo messaggio?')) return;
    this.annuncioService.eliminaMessaggio(m.id).subscribe({
      next: () => {
        this.messaggiInviati = this.messaggiInviati.filter(msg => msg.id !== m.id);
        if (this.messaggioInviatoAperto?.id === m.id) this.messaggioInviatoAperto = null;
        this.cdr.detectChanges();
      },
      error: (err: any) => console.error(err)
    });
  }

  eliminaTuttiVenditore() {
    if (!confirm('Eliminare tutti i messaggi?')) return;
    this.annuncioService.eliminaTuttiMessaggi(this.utente.id).subscribe({
      next: () => {
        this.messaggi = [];
        this.messaggioAperto = null;
        this.badgeService.reset();
        this.cdr.detectChanges();
      },
      error: (err: any) => console.error(err)
    });
  }

  eliminaTuttiAcquirente() {
    if (!confirm('Eliminare tutti i messaggi inviati?')) return;
    this.annuncioService.eliminaMieiMessaggi().subscribe({
      next: () => {
        this.messaggiInviati = [];
        this.messaggioInviatoAperto = null;
        this.cdr.detectChanges();
      },
      error: (err: any) => console.error(err)
    });
  }

  get nonLettiCount(): number {
    return this.messaggi.filter(m => !m.letto).length;
  }

  get nonLettiAdminCount(): number {
    return this.messaggiAdmin.filter(m => !m.letto).length;
  }

  salvaDescrizione() {
    this.salvatoOk = true;
    this.modificando = false;
    setTimeout(() => this.salvatoOk = false, 3000);
  }

  cambiaPassword() {
    this.cambioPasswordErrore = '';
    this.cambioPasswordOk = false;
    if (!this.vecchiaPassword || !this.nuovaPassword || !this.confermaNuovaPassword) {
      this.cambioPasswordErrore = 'Compila tutti i campi';
      return;
    }
    if (this.nuovaPassword !== this.confermaNuovaPassword) {
      this.cambioPasswordErrore = 'Le password non coincidono';
      return;
    }
    if (this.nuovaPassword.length < 6) {
      this.cambioPasswordErrore = 'La password deve essere di almeno 6 caratteri';
      return;
    }
    this.authService.cambiaPassword(this.vecchiaPassword, this.nuovaPassword).subscribe({
      next: () => {
        this.cambioPasswordOk = true;
        this.vecchiaPassword = '';
        this.nuovaPassword = '';
        this.confermaNuovaPassword = '';
        this.mostraCambioPassword = false;
        setTimeout(() => this.cambioPasswordOk = false, 3000);
        this.cdr.detectChanges();
      },
      error: (err: any) => {
        this.cambioPasswordErrore = err.error || 'Errore durante il cambio password';
        this.cdr.detectChanges();
      }
    });
  }

  onFotoProfiloSelected(event: any) {
    const file = event.target.files[0];
    if (!file) return;
    const ext = file.name.toLowerCase().split('.').pop();
    if (!['jpg', 'jpeg'].includes(ext || '')) {
      alert('Solo JPG accettato');
      return;
    }
    this.fileAvatar = file;
    this.uploadFotoProfilo();
  }

  uploadFotoProfilo() {
    if (!this.fileAvatar) return;
    this.authService.uploadFotoProfilo(this.fileAvatar).subscribe({
      next: (url: any) => {
        this.fotoProfilo = url;
        this.cdr.detectChanges();
      },
      error: (err: any) => console.error(err)
    });
  }

  rimuoviFotoProfilo() {
    if (!confirm('Rimuovere la foto profilo?')) return;
    this.authService.rimuoviFotoProfilo().subscribe({
      next: () => {
        this.fotoProfilo = null;
        this.cdr.detectChanges();
      },
      error: (err: any) => console.error(err)
    });
  }
}