import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

export interface Toast {
  messaggio: string;
  tipo: 'success' | 'danger' | 'warning' | 'info';
}

@Injectable({ providedIn: 'root' })
export class ToastService {
  private toastSubject = new BehaviorSubject<Toast | null>(null);
  toast$ = this.toastSubject.asObservable();

  show(messaggio: string, tipo: Toast['tipo'] = 'info') {
    this.toastSubject.next(null);
    setTimeout(() => {
      this.toastSubject.next({ messaggio, tipo });
      setTimeout(() => this.toastSubject.next(null), 3000);
    }, 50);
  }

  success(messaggio: string) { this.show(messaggio, 'success'); }
  danger(messaggio: string)  { this.show(messaggio, 'danger');  }
  warning(messaggio: string) { this.show(messaggio, 'warning'); }
  info(messaggio: string)    { this.show(messaggio, 'info');    }
}