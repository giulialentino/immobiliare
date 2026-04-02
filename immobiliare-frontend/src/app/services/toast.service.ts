import { Injectable } from '@angular/core';
import { Subject } from 'rxjs';

export interface Toast {
  messaggio: string;
  tipo: 'success' | 'danger' | 'warning' | 'info';
}

@Injectable({ providedIn: 'root' })
export class ToastService {
  private toastSubject = new Subject<Toast | null>();
  toast$ = this.toastSubject.asObservable();

  show(messaggio: string, tipo: Toast['tipo'] = 'info') {
    this.toastSubject.next({ messaggio, tipo });
    setTimeout(() => this.toastSubject.next(null), 3000);
  }

  success(messaggio: string) { this.show(messaggio, 'success'); }
  danger(messaggio: string)  { this.show(messaggio, 'danger');  }
  warning(messaggio: string) { this.show(messaggio, 'warning'); }
  info(messaggio: string)    { this.show(messaggio, 'info');    }
}