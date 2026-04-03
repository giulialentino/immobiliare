import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { RouterOutlet, RouterLink, Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { Navbar } from './components/navbar/navbar';
import { ToastComponent } from './components/toast/toast';
import { Chatbot } from './components/chatbot/chatbot';
import { Login } from './components/login/login';
import { Registrazione } from './components/registrazione/registrazione';
import { AuthService } from './services/auth';
import { ModalService } from './services/modal.service';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, RouterLink, Navbar, ToastComponent, CommonModule, Chatbot, Login, Registrazione],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App implements OnInit {
  showSplash = true;
  splashExit = false;
  progressione = 0;
  fraseCorrente = '';
  logoVisible = false;
  logoRuota = false;
  progressVisible = false;
  utente: any = null;

  private frasi = ['Benvenuto in Domus', 'Ricerca eccellenza...', 'Qualità garantita...', 'Pronti ad iniziare'];

  constructor(
    private cdr: ChangeDetectorRef,
    private authService: AuthService,
    public modal: ModalService,
    private router: Router
  ) {}

  ngOnInit() {
    this.avviaSequenza();
    this.authService.utente$.subscribe(u => {
      this.utente = u;
      this.cdr.detectChanges();
    });
  }

  chiudiSuOverlay(event: MouseEvent) {
    if ((event.target as HTMLElement).classList.contains('modal-overlay')) {
      this.modal.chiudi();
    }
  }

  async avviaSequenza() {
    await this.wait(400);
    this.logoVisible = true;
    this.cdr.detectChanges();
    await this.wait(1200);
    this.logoRuota = true;
    this.cdr.detectChanges();
    await this.wait(800);
    this.costruisciLogo();
    await this.wait(1500);
    this.trasformaInCasaGirata();
    await this.wait(500);
    this.progressVisible = true;
    this.avviaCaricamentoFinale();
  }

  costruisciLogo() {
    const g1 = document.getElementById('gray1');
    const g2 = document.getElementById('gray2');
    const tri3 = document.getElementById('tri3');
    if (g1) { g1.style.transition = 'all 0.6s ease'; g1.style.transform = 'translateX(60px)'; g1.style.opacity = '0'; }
    if (g2) { g2.style.transition = 'all 0.6s ease'; g2.setAttribute('width', '40'); }
    if (tri3) { tri3.style.opacity = '1'; tri3.style.transition = 'all 0.6s cubic-bezier(0.175, 0.885, 0.32, 1.275)'; tri3.style.transform = 'translateX(0)'; }
    this.cdr.detectChanges();
  }

  trasformaInCasaGirata() {
    const duration = '1.3s';
    const easing = 'cubic-bezier(0.6, 0, 0.1, 1)';
    const mainGroup = document.getElementById('main-group');
    if (mainGroup) { mainGroup.style.transition = `transform ${duration} ${easing}`; mainGroup.setAttribute('transform', 'translate(135, 150) scale(0.65)'); }
    ['b1','b2','b3','b4','b5'].forEach(id => {
      const el = document.getElementById(id);
      if (el) { el.style.transition = `all ${duration} ${easing}`; el.setAttribute('x', '-50'); el.setAttribute('y', '-60'); el.setAttribute('width', '100'); el.setAttribute('height', '120'); }
    });
    const tri1 = document.getElementById('tri1');
    const tri3 = document.getElementById('tri3');
    const roofPoints = '50,-60 120,0 50,60';
    if (tri1) { tri1.style.transition = `all ${duration} ${easing}`; tri1.setAttribute('points', roofPoints); }
    if (tri3) { tri3.style.transition = `all ${duration} ${easing}`; tri3.setAttribute('points', roofPoints); }
    const porta = document.getElementById('porta');
    if (porta) { porta.style.transition = `all ${duration} ${easing}`; porta.style.opacity = '1'; porta.setAttribute('x', '-50'); porta.setAttribute('y', '0'); porta.setAttribute('width', '20'); porta.setAttribute('height', '30'); porta.setAttribute('stroke-width', '2'); }
    const finestra = document.getElementById('finestra');
    if (finestra) { finestra.style.transition = `all ${duration} ${easing}`; finestra.style.opacity = '1'; finestra.setAttribute('transform', 'translate(5, 5)'); }
    const g2 = document.getElementById('gray2');
    if (g2) g2.style.opacity = '0';
    this.cdr.detectChanges();
  }

  avviaCaricamentoFinale() {
    const intervallo = setInterval(() => {
      this.progressione += 2;
      const index = Math.min(Math.floor(this.progressione / 26), this.frasi.length - 1);
      this.fraseCorrente = this.frasi[index];
      this.cdr.detectChanges();
      if (this.progressione >= 100) {
        clearInterval(intervallo);
        setTimeout(() => {
          this.splashExit = true;
          this.cdr.detectChanges();
          setTimeout(() => {
            this.showSplash = false;
            this.cdr.detectChanges();
          }, 850);
        }, 600);
      }
    }, 50);
  }

  private wait(ms: number) { return new Promise(resolve => setTimeout(resolve, ms)); }
}