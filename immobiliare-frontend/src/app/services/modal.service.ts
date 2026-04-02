import { Injectable, signal } from '@angular/core';
import { Subject } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class ModalService {
  mostraLogin = signal(false);
  mostraRegistrazione = signal(false);
  loginEffettuato$ = new Subject<void>();

  apriLogin() {
    this.mostraRegistrazione.set(false);
    this.mostraLogin.set(true);
    document.body.style.overflow = 'hidden';
  }

  apriRegistrazione() {
    this.mostraLogin.set(false);
    this.mostraRegistrazione.set(true);
    document.body.style.overflow = 'hidden';
  }

  chiudi() {
    this.mostraLogin.set(false);
    this.mostraRegistrazione.set(false);
    document.body.style.overflow = '';
  }

  notificaLogin() {
    this.loginEffettuato$.next();
  }
}