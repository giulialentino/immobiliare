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
  template: `
    <app-navbar></app-navbar>

    @if (showSplash) {
      <div class="splash-screen" [class.splash-exit]="splashExit">
        <div class="splash-content">
          <div class="splash-logo-wrapper" [class.ruota]="logoRuota">
            <svg width="260" height="260" viewBox="0 0 300 300" xmlns="http://www.w3.org/2000/svg">
              <defs>
                <radialGradient id="circleGrad" cx="50%" cy="50%" r="50%">
                  <stop offset="0%" stop-color="#ffffff" />
                  <stop offset="100%" stop-color="#f4f7f9" />
                </radialGradient>
              </defs>
              <circle cx="150" cy="150" r="140" fill="url(#circleGrad)" />
              <g id="main-group" transform="translate(150, 150) scale(0.7)">
                <rect id="b1" x="-85" y="-55" width="120" height="18" fill="#b5001a"/>
                <rect id="b2" x="-85" y="-33" width="120" height="18" fill="#b5001a"/>
                <rect id="b3" x="-85" y="-11" width="120" height="18" fill="#b5001a"/>
                <rect id="b4" x="-85" y="11"  width="120" height="18" fill="#b5001a"/>
                <rect id="b5" x="-85" y="33"  width="120" height="18" fill="#b5001a"/>
                <rect id="porta" x="0" y="0" width="0" height="0" fill="#b5001a" stroke="#d1d1d1" stroke-width="0" style="opacity: 0;"/>
                <g id="finestra" style="opacity: 0;">
                  <rect x="0" y="0" width="26" height="26" fill="#e0e0e0"/>
                  <line x1="13" y1="0" x2="13" y2="26" stroke="#ffffff" stroke-width="1.5"/>
                  <line x1="0" y1="13" x2="26" y2="13" stroke="#ffffff" stroke-width="1.5"/>
                </g>
                <rect id="gray1" x="40" y="-33" width="75" height="18" fill="#d1d1d1"/>
                <rect id="gray2" x="40" y="-11" width="75" height="18" fill="#d1d1d1"/>
                <polygon id="tri1" points="40,11 80,11 40,55" fill="#111318"/>
                <polygon id="tri3" points="40,-52 40,-14 80,-14" fill="#111318" style="opacity:0; transform: translateX(-25px);"/>
              </g>
            </svg>
          </div>
          <div class="splash-title-wrapper" [class.visible]="logoVisible">
            <h1 class="splash-title">Domus<span>Italia</span></h1>
            <div class="splash-line"></div>
            <p class="splash-tagline">Eccellenza Immobiliare</p>
          </div>
          <div class="splash-progress-wrapper" [class.visible]="progressVisible">
            <div class="splash-progress-info">
              <span>{{ fraseCorrente }}</span>
              <span>{{ progressione }}%</span>
            </div>
            <div class="splash-progress-track">
              <div class="splash-progress-fill" [style.width]="progressione + '%'"></div>
            </div>
          </div>
        </div>
      </div>
    }

    @if (!showSplash) {
      <router-outlet></router-outlet>
      <app-chatbot></app-chatbot>

      @if (!utente) {
        <div class="barra-accesso-glass">
          <div class="barra-accesso-testo">
            <div class="icon-circle">
              <i class="bi bi-shield-lock-fill"></i>
            </div>
            <span>Accedi per sbloccare tutte le funzionalità</span>
          </div>
          <div class="barra-accesso-btn">
            <button class="btn-glass-accedi" (click)="modal.apriLogin()">Accedi</button>
            <button class="btn-glass-registrati" (click)="modal.apriRegistrazione()">Registrati</button>
          </div>
        </div>
      }
    }

    @if (modal.mostraLogin() || modal.mostraRegistrazione()) {
      <div class="modal-overlay" (click)="chiudiSuOverlay($event)">
        <div class="modal-box" (click)="$event.stopPropagation()">
          <button class="modal-close" (click)="modal.chiudi()">
            <i class="bi bi-x-lg"></i>
          </button>
          @if (modal.mostraLogin()) {
            <app-login (loginRiuscito)="modal.chiudi()"></app-login>
          } @else {
            <app-registrazione (registrazioneRiuscita)="modal.chiudi()"></app-registrazione>
          }
        </div>
      </div>
    }

    <app-toast></app-toast>
  `,
  styles: [`
    @import url('https://fonts.googleapis.com/css2?family=Playfair+Display:wght@700;800&family=Inter:wght@400;500;600;700&display=swap');

    /* --- SPLASH SCREEN LIGHT --- */
    .splash-screen {
      position: fixed; inset: 0; background: #ffffff;
      display: flex; align-items: center; justify-content: center; z-index: 99999;
    }
    .splash-exit { opacity: 0; transition: opacity 0.8s ease-in-out; pointer-events: none; }
    .splash-content { display: flex; flex-direction: column; align-items: center; gap: 40px; }
    .splash-logo-wrapper { transition: transform 1.2s cubic-bezier(0.2, 1, 0.3, 1); filter: drop-shadow(0 15px 30px rgba(0,0,0,0.08)); }
    .splash-logo-wrapper.ruota { transform: rotate(-90deg); }
    .splash-title-wrapper { text-align: center; opacity: 0; transform: translateY(20px); transition: all 1s ease; }
    .splash-title-wrapper.visible { opacity: 1; transform: translateY(0); }
    .splash-title { font-family: 'Playfair Display', serif; font-size: 3.2rem; font-weight: 800; color: #111318; margin: 0; }
    .splash-title span { color: #b5001a; }
    .splash-line { width: 40px; height: 3px; background: #b5001a; margin: 15px auto; border-radius: 2px; }
    .splash-tagline { font-family: 'Inter', sans-serif; font-size: 0.75rem; color: #949aab; letter-spacing: 0.4em; text-transform: uppercase; font-weight: 700; }
    .splash-progress-wrapper { width: 280px; opacity: 0; transition: opacity 0.8s ease; }
    .splash-progress-wrapper.visible { opacity: 1; }
    .splash-progress-track { width: 100%; height: 2px; background: #f0f2f5; border-radius: 10px; overflow: hidden; }
    .splash-progress-fill { height: 100%; background: #b5001a; transition: width 0.4s ease; }
    .splash-progress-info { display: flex; justify-content: space-between; font-size: 10px; color: #949aab; margin-bottom: 8px; text-transform: uppercase; font-weight: 600; }

    /* --- BARRA DI ACCESSO TRASPARENTE (GLASSMORPHISM) --- */
    .barra-accesso-glass {
      position: fixed; bottom: 30px; left: 50%; transform: translateX(-50%);
      width: min(92%, 780px);
      background: rgba(255, 255, 255, 0.4);
      backdrop-filter: blur(16px) saturate(180%);
      -webkit-backdrop-filter: blur(16px) saturate(180%);
      border: 1px solid rgba(255, 255, 255, 0.5);
      box-shadow: 0 15px 40px rgba(0,0,0,0.08);
      padding: 10px 24px;
      display: flex; align-items: center; justify-content: space-between;
      z-index: 2000; border-radius: 100px;
      font-family: 'Inter', sans-serif;
      animation: barSlideIn 1s cubic-bezier(0.16, 1, 0.3, 1) 2s both;
    }
    @keyframes barSlideIn { from { opacity: 0; transform: translate(-50%, 30px); } to { opacity: 1; transform: translate(-50%, 0); } }
    
    .barra-accesso-testo { display: flex; align-items: center; gap: 14px; }
    .icon-circle { 
      width: 36px; height: 36px; background: rgba(181, 0, 26, 0.1); 
      border-radius: 50%; display: flex; align-items: center; justify-content: center;
      color: #b5001a; font-size: 18px;
    }
    .barra-accesso-testo span { color: #111318; font-size: 14px; font-weight: 600; letter-spacing: -0.01em; }

    .barra-accesso-btn { display: flex; gap: 12px; }
    
    .btn-glass-accedi {
      padding: 8px 22px; border: 1px solid rgba(17, 19, 24, 0.1);
      background: rgba(255, 255, 255, 0.5); color: #111318; border-radius: 50px;
      font-size: 13px; font-weight: 700; cursor: pointer; transition: all 0.3s;
    }
    .btn-glass-accedi:hover { background: #ffffff; border-color: #111318; transform: translateY(-2px); }
    
    .btn-glass-registrati {
      padding: 8px 24px; background: #b5001a;
      color: white; border: none; border-radius: 50px;
      font-size: 13px; font-weight: 700; cursor: pointer; transition: all 0.3s;
      box-shadow: 0 8px 20px rgba(181, 0, 26, 0.25);
    }
    .btn-glass-registrati:hover { background: #850012; transform: translateY(-2px); box-shadow: 0 12px 25px rgba(181, 0, 26, 0.35); }

    /* --- MODALI --- */
    .modal-overlay {
      position: fixed; inset: 0; background: rgba(17, 19, 24, 0.4);
      backdrop-filter: blur(8px); z-index: 9000;
      display: flex; align-items: center; justify-content: center;
    }
    .modal-box {
      background: white; border-radius: 28px; width: min(480px, 95vw);
      max-height: 90vh; overflow-y: auto; position: relative;
      box-shadow: 0 40px 90px rgba(0,0,0,0.25);
      animation: modalIn 0.4s cubic-bezier(0.16, 1, 0.3, 1);
    }
    @keyframes modalIn { from { opacity: 0; transform: translateY(30px) scale(0.98); } to { opacity: 1; transform: translateY(0) scale(1); } }
    .modal-close {
      position: absolute; top: 20px; right: 20px; width: 38px; height: 38px;
      border-radius: 50%; background: #f4f5f7; border: none;
      color: #111318; font-size: 20px; cursor: pointer;
      display: flex; align-items: center; justify-content: center; z-index: 10;
      transition: all 0.2s;
    }
    .modal-close:hover { background: #111318; color: white; }
  `]
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