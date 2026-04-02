import { Injectable, signal } from '@angular/core';

@Injectable({ providedIn: 'root' })
export class ModalService {
  mostraLogin = signal(false);
  mostraRegistrazione = signal(false);

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
}