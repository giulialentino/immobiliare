import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { RouterOutlet, RouterLink } from '@angular/router';
import { CommonModule } from '@angular/common';
import { Navbar } from './components/navbar/navbar';
import { ToastComponent } from './components/toast/toast';
import { Chatbot } from './components/chatbot/chatbot';
import { AuthService } from './services/auth';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, RouterLink, Navbar, ToastComponent, CommonModule, Chatbot],
  template: `
    <app-navbar></app-navbar>

    @if (showSplash) {
      <div class="splash-screen" [class.splash-exit]="splashExit">
        <div class="splash-content">
          <div class="splash-logo-wrapper" [class.ruota]="logoRuota">
            <svg width="280" height="280" viewBox="0 0 300 300" xmlns="http://www.w3.org/2000/svg">
              <defs>
                <radialGradient id="circleGrad" cx="50%" cy="50%" r="50%">
                  <stop offset="0%" stop-color="#ffffff" />
                  <stop offset="100%" stop-color="#f8f9fa" />
                </radialGradient>
              </defs>
              <circle cx="150" cy="150" r="140" fill="url(#circleGrad)" />
              <g id="main-group" transform="translate(150, 150) scale(0.7)">
                <rect id="b1" x="-85" y="-55" width="120" height="18" fill="#8b1a2b"/>
                <rect id="b2" x="-85" y="-33" width="120" height="18" fill="#8b1a2b"/>
                <rect id="b3" x="-85" y="-11" width="120" height="18" fill="#8b1a2b"/>
                <rect id="b4" x="-85" y="11"  width="120" height="18" fill="#8b1a2b"/>
                <rect id="b5" x="-85" y="33"  width="120" height="18" fill="#8b1a2b"/>
                <rect id="porta" x="0" y="0" width="0" height="0" fill="#8b1a2b" stroke="#d1d1d1" stroke-width="0" style="opacity: 0;"/>
                <g id="finestra" style="opacity: 0;">
                  <rect x="0" y="0" width="26" height="26" fill="#c0c0c0"/>
                  <line x1="13" y1="0" x2="13" y2="26" stroke="#1a1a1a" stroke-width="1.5"/>
                  <line x1="0" y1="13" x2="26" y2="13" stroke="#1a1a1a" stroke-width="1.5"/>
                </g>
                <rect id="gray1" x="40" y="-33" width="75" height="18" fill="#c0c0c0"/>
                <rect id="gray2" x="40" y="-11" width="75" height="18" fill="#c0c0c0"/>
                <polygon id="tri1" points="40,11 80,11 40,55" fill="#1a1a1a"/>
                <polygon id="tri3" points="40,-52 40,-14 80,-14" fill="#1a1a1a" style="opacity:0; transform: translateX(-25px);"/>
              </g>
            </svg>
          </div>
          <div class="splash-title-wrapper" [class.visible]="logoVisible">
            <h1 class="splash-title">Domus</h1>
            <div class="splash-line"></div>
            <p class="splash-tagline">L'eccellenza nel settore immobiliare</p>
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
  <div class="barra-accesso">
    <div class="barra-accesso-testo">
      <i class="bi bi-lock-fill barra-lucchetto"></i>
      <span>Accedi o registrati per scoprire tutte le funzionalità</span>
    </div>
    <div class="barra-accesso-btn">
      <a routerLink="/login" class="barra-btn-accedi">Accedi</a>
      <a routerLink="/registrazione" class="barra-btn-registrati">Registrati</a>
    </div>
  </div>
}
    }

    <app-toast></app-toast>
  `,
  styles: [`
    @import url('https://fonts.googleapis.com/css2?family=Playfair+Display:wght@700&family=DM+Sans:wght@400;500&display=swap');

    .splash-screen {
      position: fixed; inset: 0; background: #0a0a0a;
      display: flex; align-items: center; justify-content: center; z-index: 99999;
    }
    .splash-exit { opacity: 0; transition: opacity 0.8s ease-in-out; }
    .splash-content { display: flex; flex-direction: column; align-items: center; gap: 50px; }

    .splash-logo-wrapper { transition: transform 1.2s cubic-bezier(0.2, 1, 0.3, 1); }
    .splash-logo-wrapper.ruota { transform: rotate(-90deg); }

    .splash-title-wrapper { text-align: center; opacity: 0; transform: translateY(20px); transition: all 1s ease; }
    .splash-title-wrapper.visible { opacity: 1; transform: translateY(0); }
    .splash-title {
      font-family: 'Playfair Display', serif; font-size: 3rem; letter-spacing: 8px;
      text-transform: uppercase; color: #ffffff; margin: 0;
      background: linear-gradient(to bottom, #fff 0%, #ccc 100%);
      -webkit-background-clip: text; -webkit-text-fill-color: transparent;
    }
    .splash-line { width: 40px; height: 2px; background: #8b1a2b; margin: 15px auto; }
    .splash-tagline { font-family: 'DM Sans', sans-serif; font-size: 0.9rem; color: #888; letter-spacing: 3px; text-transform: uppercase; }

    .splash-progress-wrapper { width: 300px; opacity: 0; transition: opacity 0.8s ease; }
    .splash-progress-wrapper.visible { opacity: 1; }
    .splash-progress-track { width: 100%; height: 3px; background: rgba(255,255,255,0.05); border-radius: 10px; overflow: hidden; }
    .splash-progress-fill { height: 100%; background: linear-gradient(90deg, #8b1a2b, #c0001a); transition: width 0.4s ease; }
    .splash-progress-info { display: flex; justify-content: space-between; font-size: 0.7rem; color: #666; margin-bottom: 8px; text-transform: uppercase; }

    .barra-accesso {
      position: fixed;
      bottom: 0; left: 0; right: 0;
      background: #0f0c0c;
      border-top: 1px solid rgba(139, 26, 43, 0.4);
      padding: 14px 24px;
      display: flex;
      align-items: center;
      justify-content: space-between;
      z-index: 800;
      font-family: 'DM Sans', sans-serif;
    }
    .barra-accesso span { color: rgba(255,255,255,0.6); font-size: 14px; }
    .barra-accesso-btn { display: flex; gap: 10px; }
    .barra-btn-accedi {
      padding: 8px 20px;
      border: 1px solid rgba(255,255,255,0.2);
      color: white; text-decoration: none;
      border-radius: 8px; font-size: 14px; font-weight: 500;
      transition: all 0.2s;
    }
    .barra-btn-accedi:hover { background: rgba(255,255,255,0.07); }
    .barra-btn-registrati {
      padding: 8px 20px;
      background: linear-gradient(135deg, #8b1a2b, #c0001a);
      color: white; text-decoration: none;
      border-radius: 8px; font-size: 14px; font-weight: 600;
      transition: opacity 0.2s;
      box-shadow: 0 2px 8px rgba(192,0,26,0.3);
    }
    .barra-btn-registrati:hover { opacity: 0.9; }
  `]
})
export class App implements OnInit {
  showSplash = true; splashExit = false; progressione = 0;
  fraseCorrente = ''; logoVisible = false; logoRuota = false; progressVisible = false;
  utente: any = null;

  private frasi = ['Sognando lo spazio...', 'Dettagli che contano...', 'Rifiniture di pregio..', 'Finalmente a casa'];

  constructor(private cdr: ChangeDetectorRef, private authService: AuthService) {}

  ngOnInit() {
  this.avviaSequenza();
  this.authService.utente$.subscribe(u => {
    this.utente = u;
    this.cdr.detectChanges();
  });
}

  async avviaSequenza() {
    await this.wait(400);
    this.logoVisible = true;
    this.cdr.detectChanges();

    await this.wait(1500);
    this.logoRuota = true;
    this.cdr.detectChanges();

    await this.wait(800);
    this.costruisciLogo();

    await this.wait(1800);
    this.trasformaInCasaGirata();

    await this.wait(600);
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
    ['b1', 'b2', 'b3', 'b4', 'b5'].forEach((id) => {
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