import { ModalService } from '../../services/modal.service';
import { Component, Output, EventEmitter } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../services/auth';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { ToastService } from '../../services/toast.service';
import { ChangeDetectorRef } from '@angular/core';


@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './login.html',
  styleUrl: './login.css'
})
export class Login {
  @Output() loginRiuscito = new EventEmitter<void>();
  email = '';
  password = '';
  errore = '';
  mostraPassword = false;

  constructor(
    private authService: AuthService,
    private router: Router,
    private toast: ToastService,
    private cdr: ChangeDetectorRef,
    private modalService: ModalService
  ) {}

  login() {
    this.errore = '';
    this.authService.login(this.email, this.password).subscribe({
      next: () => {
        this.modalService.notificaLogin();
        this.loginRiuscito.emit();
          this.router.navigate(['/']);
        setTimeout(() => {
          this.toast.success('Accesso effettuato!');
        }, 500);
      },
      error: (err: any) => {
        if (err.status === 403 && err.error === 'EMAIL_NON_VERIFICATA') {
          this.errore = 'Devi verificare la tua email prima di accedere. Controlla la tua casella di posta.';
        } else if (err.status === 403) {
          this.errore = 'Il tuo account è stato bannato. Contatta l\'amministratore.';
        } else {
          this.errore = 'Email o password errati';
        }
        this.cdr.detectChanges();
      }
    });
  }
}