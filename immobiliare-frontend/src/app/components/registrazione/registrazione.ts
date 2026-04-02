import { Component } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../services/auth';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { ChangeDetectorRef } from '@angular/core';

@Component({
  selector: 'app-registrazione',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './registrazione.html',
  styleUrl: './registrazione.css'
})
export class Registrazione {

  nome = '';
  cognome = '';
  email = '';
  password = '';
  confermaPassword = '';
  ruolo = 'ACQUIRENTE';
  errore = '';
  successo = '';
  mostraPassword = false;
  registrazioneCompletata = false;

  constructor(private authService: AuthService, private router: Router, private cdr: ChangeDetectorRef) {}

  emailValida(): boolean {
    const regex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return regex.test(this.email);
  }

  haMaiuscola(): boolean {
    return /[A-Z]/.test(this.password);
  }

  haNumero(): boolean {
    return /[0-9]/.test(this.password);
  }

  passwordValida(): boolean {
    return this.password.length >= 8 && this.haMaiuscola() && this.haNumero();
  }

  formValido(): boolean {
    return !!this.nome && !!this.cognome && this.emailValida() 
      && this.passwordValida() && this.password === this.confermaPassword;
  }

  registra() {
    this.errore = '';
    this.successo = '';

    if (!this.formValido()) {
      this.errore = 'Compila tutti i campi correttamente';
      return;
    }

    this.authService.registra({
      nome: this.nome,
      cognome: this.cognome,
      email: this.email,
      password: this.password,
      ruolo: this.ruolo
    }).subscribe({
      next: () => {
        this.registrazioneCompletata = true;
        this.cdr.detectChanges();
      },
      error: (err: any) => {
        this.errore = err.status === 400 ? 'Email già registrata' : 'Errore durante la registrazione';
        this.cdr.detectChanges();
      }
    });
  }
}