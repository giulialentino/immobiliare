import { Component, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';

interface Messaggio {
  ruolo: 'utente' | 'assistente';
  testo: string;
  loading?: boolean;
}

@Component({
  selector: 'app-chatbot',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <!-- Bottone flottante -->
    <div class="chat-toggle-wrapper"
         [style.bottom.px]="toggleBottom"
         [style.right.px]="toggleRight"
         (mousedown)="iniziaDragToggle($event)"
         (touchstart)="iniziaDragToggleMobile($event)">
      <button class="chat-toggle" (click)="onClickToggle($event)">
        <div class="toggle-avatar">
          <i class="bi bi-person-badge-fill"></i>
        </div>
        <div class="toggle-testo">
          <span class="toggle-nome">Ubi</span>
          <span class="toggle-ruolo">Agente Immobiliare</span>
        </div>
        <div class="toggle-online"></div>
      </button>
    </div>

    <!-- Finestra chat -->
    @if (aperta) {
      <div class="chat-window"
           [style.bottom.px]="windowBottom"
           [style.right.px]="windowRight"
           (mousedown)="iniziaDragWindow($event)"
           (touchstart)="iniziaDragWindowMobile($event)">

        <!-- Header -->
        <div class="chat-header">
          <div class="header-avatar">
            <i class="bi bi-person-badge-fill"></i>
            <div class="header-online"></div>
          </div>
          <div class="header-info">
            <div class="header-nome">Ubi</div>
            <div class="header-stato">
              <span class="stato-dot"></span> Online ora
            </div>
          </div>
          <button class="chat-close" (click)="toggleChat()">
            <i class="bi bi-x-lg"></i>
          </button>
        </div>

        <!-- Messaggi -->
        <div class="chat-messages" (mousedown)="$event.stopPropagation()"
             (touchstart)="$event.stopPropagation()">

          @if (messaggi.length === 0) {
            <div class="chat-benvenuto">
              <div class="benvenuto-card">
                <p class="benvenuto-testo">
                  Ciao! Sono <strong>Ubi</strong>, il tuo agente immobiliare virtuale. 
                  Sono qui per aiutarti a trovare la casa dei tuoi sogni o rispondere 
                  a qualsiasi domanda sulla piattaforma.
                </p>
              </div>
              <p class="suggerimenti-label">Cosa posso fare per te?</p>
              <div class="suggerimenti">
                @for (s of suggerimenti; track $index) {
                  <button class="suggerimento" (click)="inviaSuggerimento(s)">
                    {{ s }}
                  </button>
                }
              </div>
            </div>
          }

          @for (msg of messaggi; track $index) {
            <div class="messaggio-wrapper" [class.utente]="msg.ruolo === 'utente'">
              @if (msg.ruolo === 'assistente') {
                <div class="msg-avatar-small">
                  <i class="bi bi-person-badge-fill"></i>
                </div>
              }
              <div class="msg-bolla" [class.utente]="msg.ruolo === 'utente'"
                   [class.assistente]="msg.ruolo === 'assistente'">
                @if (msg.loading) {
                  <div class="typing">
                    <span></span><span></span><span></span>
                  </div>
                } @else {
                  {{ msg.testo }}
                }
              </div>
            </div>
          }
        </div>

        <!-- Input -->
        <div class="chat-input" (mousedown)="$event.stopPropagation()"
             (touchstart)="$event.stopPropagation()">
          @if (errore) {
            <div class="chat-errore">{{ errore }}</div>
          }
          <div class="input-row">
            <input type="text" [(ngModel)]="inputTesto"
                   placeholder="Scrivi a Ubi..."
                   (keyup.enter)="invia()"
                   [disabled]="loading" />
            <button class="send-btn" (click)="invia()"
                    [disabled]="!inputTesto.trim() || loading">
              <i class="bi bi-send-fill"></i>
            </button>
          </div>
          <div class="chat-footer">Powered by AI · Immobiliare Italia</div>
        </div>
      </div>
    }
  `,
  styles: [`
    /* ── Bottone flottante ── */
    .chat-toggle-wrapper {
      position: fixed;
      z-index: 1002;
      cursor: grab;
      user-select: none;
    }

    .chat-toggle-wrapper:active { cursor: grabbing; }

    .chat-toggle {
      display: flex;
      align-items: center;
      gap: 10px;
      padding: 8px 16px 8px 8px;
      background: linear-gradient(135deg, #1a1a2e 0%, #16213e 100%);
      border: 1px solid rgba(255,255,255,0.1);
      border-radius: 50px;
      cursor: pointer;
      box-shadow: 0 4px 20px rgba(0,0,0,0.3), 0 0 0 1px rgba(192,0,26,0.2);
      transition: transform 0.2s, box-shadow 0.2s;
      position: relative;
    }

    .chat-toggle:hover {
      transform: translateY(-2px);
      box-shadow: 0 8px 28px rgba(0,0,0,0.4), 0 0 0 1px rgba(192,0,26,0.4);
    }

    .toggle-avatar {
      width: 40px;
      height: 40px;
      background: linear-gradient(135deg, #c0001a, #8b0012);
      border-radius: 50%;
      display: flex;
      align-items: center;
      justify-content: center;
      color: white;
      font-size: 18px;
      flex-shrink: 0;
    }

    .toggle-testo {
      display: flex;
      flex-direction: column;
      align-items: flex-start;
    }

    .toggle-nome {
      color: white;
      font-size: 13px;
      font-weight: 700;
      line-height: 1.2;
    }

    .toggle-ruolo {
      color: rgba(255,255,255,0.5);
      font-size: 10px;
      line-height: 1.2;
    }

    .toggle-online {
      width: 8px;
      height: 8px;
      background: #22c55e;
      border-radius: 50%;
      border: 2px solid #1a1a2e;
      position: absolute;
      bottom: 10px;
      left: 42px;
      animation: pulse-green 2s infinite;
    }

    @keyframes pulse-green {
      0%, 100% { box-shadow: 0 0 0 0 rgba(34,197,94,0.4); }
      50% { box-shadow: 0 0 0 4px rgba(34,197,94,0); }
    }

    /* ── Finestra chat ── */
    .chat-window {
      position: fixed;
      width: 380px;
      height: 500px;
      background: #ffffff;
      border-radius: 20px;
      box-shadow: 0 20px 60px rgba(0,0,0,0.2), 0 0 0 1px rgba(0,0,0,0.05);
      z-index: 1001;
      display: flex;
      flex-direction: column;
      overflow: hidden;
      animation: slideUp 0.3s cubic-bezier(0.34, 1.56, 0.64, 1);
    }

    @keyframes slideUp {
      from { opacity: 0; transform: translateY(30px) scale(0.95); }
      to { opacity: 1; transform: translateY(0) scale(1); }
    }

    /* ── Header ── */
    .chat-header {
      background: linear-gradient(135deg, #1a1a2e 0%, #16213e 100%);
      padding: 16px 20px;
      display: flex;
      align-items: center;
      gap: 12px;
      cursor: grab;
      position: relative;
    }

    .chat-header::after {
      content: '';
      position: absolute;
      bottom: 0;
      left: 0;
      right: 0;
      height: 2px;
      background: linear-gradient(90deg, #c0001a, transparent);
    }

    .chat-header:active { cursor: grabbing; }

    .header-avatar {
      width: 44px;
      height: 44px;
      background: linear-gradient(135deg, #c0001a, #8b0012);
      border-radius: 50%;
      display: flex;
      align-items: center;
      justify-content: center;
      color: white;
      font-size: 20px;
      flex-shrink: 0;
      position: relative;
    }

    .header-online {
      width: 10px;
      height: 10px;
      background: #22c55e;
      border-radius: 50%;
      border: 2px solid #1a1a2e;
      position: absolute;
      bottom: 1px;
      right: 1px;
    }

    .header-info { flex: 1; }

    .header-nome {
      color: white;
      font-weight: 700;
      font-size: 15px;
    }

    .header-stato {
      color: rgba(255,255,255,0.5);
      font-size: 11px;
      display: flex;
      align-items: center;
      gap: 4px;
      margin-top: 2px;
    }

    .stato-dot {
      width: 6px;
      height: 6px;
      background: #22c55e;
      border-radius: 50%;
      display: inline-block;
    }

    .chat-close {
      background: rgba(255,255,255,0.1);
      border: none;
      color: rgba(255,255,255,0.7);
      cursor: pointer;
      width: 30px;
      height: 30px;
      border-radius: 50%;
      display: flex;
      align-items: center;
      justify-content: center;
      font-size: 12px;
      transition: background 0.2s, color 0.2s;
    }

    .chat-close:hover {
      background: rgba(192,0,26,0.3);
      color: white;
    }

    /* ── Messaggi ── */
    .chat-messages {
      flex: 1;
      overflow-y: auto;
      padding: 20px 16px;
      display: flex;
      flex-direction: column;
      gap: 12px;
      background: #f8f9fb;
      cursor: default;
    }

    .chat-messages::-webkit-scrollbar { width: 4px; }
    .chat-messages::-webkit-scrollbar-track { background: transparent; }
    .chat-messages::-webkit-scrollbar-thumb { background: #ddd; border-radius: 2px; }

    /* ── Benvenuto ── */
    .chat-benvenuto { display: flex; flex-direction: column; gap: 12px; }

    .benvenuto-card {
      background: white;
      border-radius: 16px;
      padding: 16px;
      border-left: 3px solid #c0001a;
      box-shadow: 0 2px 8px rgba(0,0,0,0.06);
    }

    .benvenuto-testo {
      font-size: 13px;
      line-height: 1.6;
      color: #444;
      margin: 0;
    }

    .suggerimenti-label {
      font-size: 11px;
      font-weight: 600;
      color: #999;
      text-transform: uppercase;
      letter-spacing: 0.05em;
      margin: 4px 0 0 0;
    }

    .suggerimenti {
      display: flex;
      flex-direction: column;
      gap: 6px;
    }

    .suggerimento {
      background: white;
      border: 1px solid #e8e8e8;
      border-radius: 12px;
      padding: 8px 14px;
      font-size: 12px;
      cursor: pointer;
      text-align: left;
      color: #333;
      transition: all 0.15s;
      box-shadow: 0 1px 3px rgba(0,0,0,0.04);
    }

    .suggerimento:hover {
      border-color: #c0001a;
      color: #c0001a;
      transform: translateX(3px);
    }

    /* ── Messaggi bubble ── */
    .messaggio-wrapper {
      display: flex;
      gap: 8px;
      align-items: flex-end;
      animation: fadeInMsg 0.2s ease;
    }

    .messaggio-wrapper.utente {
      flex-direction: row-reverse;
    }

    @keyframes fadeInMsg {
      from { opacity: 0; transform: translateY(8px); }
      to { opacity: 1; transform: translateY(0); }
    }

    .msg-avatar-small {
      width: 30px;
      height: 30px;
      background: linear-gradient(135deg, #c0001a, #8b0012);
      color: white;
      border-radius: 50%;
      display: flex;
      align-items: center;
      justify-content: center;
      font-size: 13px;
      flex-shrink: 0;
    }

    .msg-bolla {
      max-width: 72%;
      padding: 10px 14px;
      border-radius: 18px;
      font-size: 13px;
      line-height: 1.55;
      box-shadow: 0 1px 3px rgba(0,0,0,0.08);
    }

    .msg-bolla.assistente {
      background: white;
      color: #222;
      border-bottom-left-radius: 4px;
    }

    .msg-bolla.utente {
      background: linear-gradient(135deg, #1a1a2e, #16213e);
      color: white;
      border-bottom-right-radius: 4px;
    }

    /* ── Typing ── */
    .typing {
      display: flex;
      gap: 4px;
      align-items: center;
      padding: 2px 0;
    }

    .typing span {
      width: 7px;
      height: 7px;
      background: #bbb;
      border-radius: 50%;
      animation: bounce 1.2s infinite;
    }

    .typing span:nth-child(2) { animation-delay: 0.2s; }
    .typing span:nth-child(3) { animation-delay: 0.4s; }

    @keyframes bounce {
      0%, 60%, 100% { transform: translateY(0); }
      30% { transform: translateY(-6px); }
    }

    /* ── Input ── */
    .chat-input {
      background: white;
      border-top: 1px solid #f0f0f0;
      padding: 12px 16px 8px;
    }

    .chat-errore {
      font-size: 11px;
      color: #c0001a;
      margin-bottom: 6px;
      text-align: center;
    }

    .input-row {
      display: flex;
      gap: 8px;
      align-items: center;
    }

    .input-row input {
      flex: 1;
      border: 1.5px solid #eee;
      border-radius: 24px;
      padding: 10px 16px;
      font-size: 13px;
      outline: none;
      transition: border-color 0.2s, box-shadow 0.2s;
      background: #f8f9fb;
      color: #222;
    }

    .input-row input:focus {
      border-color: #c0001a;
      box-shadow: 0 0 0 3px rgba(192,0,26,0.08);
      background: white;
    }

    .input-row input::placeholder { color: #aaa; }

    .send-btn {
      width: 40px;
      height: 40px;
      background: linear-gradient(135deg, #c0001a, #8b0012);
      color: white;
      border: none;
      border-radius: 50%;
      cursor: pointer;
      display: flex;
      align-items: center;
      justify-content: center;
      font-size: 14px;
      transition: transform 0.15s, box-shadow 0.15s;
      flex-shrink: 0;
      box-shadow: 0 2px 8px rgba(192,0,26,0.3);
    }

    .send-btn:hover:not(:disabled) {
      transform: scale(1.08);
      box-shadow: 0 4px 12px rgba(192,0,26,0.4);
    }

    .send-btn:disabled { opacity: 0.4; cursor: not-allowed; }

    .chat-footer {
      text-align: center;
      font-size: 10px;
      color: #ccc;
      margin-top: 6px;
      letter-spacing: 0.03em;
    }

    @media (max-width: 400px) {
      .chat-window { width: calc(100vw - 32px); }
      .chat-toggle { padding: 8px 12px 8px 8px; }
    }
  `]
})
export class Chatbot {
  aperta = false;
  inputTesto = '';
  messaggi: Messaggio[] = [];
  cronologia: { ruolo: string; testo: string }[] = [];
  loading = false;
  errore = '';

  toggleBottom = 100;
  toggleRight = 24;
  windowBottom = 170;
  windowRight = 24;

  private draggingToggle = false;
  private draggingWindow = false;
  private dragStartX = 0;
  private dragStartY = 0;
  private dragStartBottom = 0;
  private dragStartRight = 0;
  private dragMoved = false;

  suggerimenti = [
    'Come pubblico un annuncio?',
    "Come funziona l'asta?",
    'Come divento venditore?',
    'Come contatto un venditore?'
  ];

  constructor(
    private http: HttpClient,
    private cdr: ChangeDetectorRef
  ) {
    document.addEventListener('mousemove', (e) => this.handleMove(e.clientX, e.clientY));
    document.addEventListener('mouseup', () => this.onMouseUp());
    document.addEventListener('touchmove', (e) => {
      if (this.draggingToggle || this.draggingWindow) {
        e.preventDefault();
        this.handleMove(e.touches[0].clientX, e.touches[0].clientY);
      }
    }, { passive: false });
    document.addEventListener('touchend', () => this.onMouseUp());
  }

  iniziaDragToggle(e: MouseEvent) {
    this.draggingToggle = true;
    this.dragMoved = false;
    this.dragStartX = e.clientX;
    this.dragStartY = e.clientY;
    this.dragStartBottom = this.toggleBottom;
    this.dragStartRight = this.toggleRight;
    e.preventDefault();
  }

  iniziaDragToggleMobile(e: TouchEvent) {
    this.draggingToggle = true;
    this.dragMoved = false;
    this.dragStartX = e.touches[0].clientX;
    this.dragStartY = e.touches[0].clientY;
    this.dragStartBottom = this.toggleBottom;
    this.dragStartRight = this.toggleRight;
  }

  iniziaDragWindow(e: MouseEvent) {
    const target = e.target as HTMLElement;
    if (target.closest('.chat-input') || target.closest('.chat-messages')) return;
    this.draggingWindow = true;
    this.dragMoved = false;
    this.dragStartX = e.clientX;
    this.dragStartY = e.clientY;
    this.dragStartBottom = this.windowBottom;
    this.dragStartRight = this.windowRight;
    e.preventDefault();
  }

  iniziaDragWindowMobile(e: TouchEvent) {
    const target = e.target as HTMLElement;
    if (target.closest('.chat-input') || target.closest('.chat-messages')) return;
    this.draggingWindow = true;
    this.dragMoved = false;
    this.dragStartX = e.touches[0].clientX;
    this.dragStartY = e.touches[0].clientY;
    this.dragStartBottom = this.windowBottom;
    this.dragStartRight = this.windowRight;
  }

  private handleMove(clientX: number, clientY: number) {
    const dx = clientX - this.dragStartX;
    const dy = clientY - this.dragStartY;

    if (Math.abs(dx) > 5 || Math.abs(dy) > 5) {
      this.dragMoved = true;
    }

    if (this.draggingToggle && this.dragMoved) {
      this.toggleBottom = Math.max(0, this.dragStartBottom - dy);
      this.toggleRight = Math.max(0, this.dragStartRight - dx);
      this.cdr.detectChanges();
    }

    if (this.draggingWindow && this.dragMoved) {
      this.windowBottom = Math.max(0, this.dragStartBottom - dy);
      this.windowRight = Math.max(0, this.dragStartRight - dx);
      this.cdr.detectChanges();
    }
  }

  private onMouseUp() {
    this.draggingToggle = false;
    this.draggingWindow = false;
  }

  onClickToggle(e: MouseEvent) {
    if (this.dragMoved) {
      this.dragMoved = false;
      return;
    }
    this.toggleChat();
  }

  toggleChat() {
    this.aperta = !this.aperta;
    this.cdr.detectChanges();
  }

  inviaSuggerimento(testo: string) {
    this.inputTesto = testo;
    this.invia();
  }

  invia() {
    const testo = this.inputTesto.trim();
    if (!testo || this.loading) return;

    this.errore = '';
    this.inputTesto = '';
    this.loading = true;

    this.messaggi.push({ ruolo: 'utente', testo });
    const loadingMsg: Messaggio = { ruolo: 'assistente', testo: '', loading: true };
    this.messaggi.push(loadingMsg);
    this.cdr.detectChanges();
    this.scrollDown();

    this.http.post<any>(
      'http://localhost:8080/api/ai/chat',
      { messaggio: testo, cronologia: this.cronologia },
      { withCredentials: true }
    ).subscribe({
      next: (res) => {
        const risposta = res?.candidates?.[0]?.content?.parts?.[0]?.text || 'Non ho capito, puoi riformulare?';
        const idx = this.messaggi.indexOf(loadingMsg);
        if (idx !== -1) {
          this.messaggi[idx] = { ruolo: 'assistente', testo: risposta.trim() };
        }
        this.cronologia.push({ ruolo: 'utente', testo });
        this.cronologia.push({ ruolo: 'assistente', testo: risposta.trim() });
        this.loading = false;
        this.cdr.detectChanges();
        this.scrollDown();
      },
      error: (err) => {
        const idx = this.messaggi.indexOf(loadingMsg);
        if (idx !== -1) {
          this.messaggi[idx] = {
            ruolo: 'assistente',
            testo: err.status === 429
              ? 'Servizio temporaneamente sovraccarico. Riprova tra qualche secondo.'
              : 'Si è verificato un errore. Riprova.'
          };
        }
        this.loading = false;
        this.cdr.detectChanges();
        this.scrollDown();
      }
    });
  }

  private scrollDown() {
    setTimeout(() => {
      const container = document.querySelector('.chat-messages');
      if (container) container.scrollTop = container.scrollHeight;
    }, 50);
  }
}