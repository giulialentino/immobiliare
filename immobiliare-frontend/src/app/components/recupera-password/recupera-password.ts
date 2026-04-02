import { Component, ChangeDetectorRef } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-recupera-password',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  template: `
    <div class="container mt-5">
      <div class="row justify-content-center">
        <div class="col-md-5">
          <div class="card shadow p-4">

            @if (!inviato) {
              <h4 class="text-center mb-3">
                <i class="bi bi-key-fill"></i> Recupera password
              </h4>
              <p class="text-muted text-center small">
                Inserisci la tua email e ti invieremo un link per reimpostare la password.
              </p>
              <div class="mb-3">
                <label class="form-label">Email</label>
                <input type="email" class="form-control" [(ngModel)]="email"
                       placeholder="La tua email" (keyup.enter)="invia()" />
              </div>
              @if (errore) {
                <div class="alert alert-danger py-2 small">{{ errore }}</div>
              }
              <button class="btn btn-dark w-100" (click)="invia()"
                      [disabled]="!email || loading">
                {{ loading ? 'Invio in corso...' : 'Invia link di recupero' }}
              </button>
              <p class="text-center mt-3 small">
                <a routerLink="/login">← Torna al login</a>
              </p>
            }

            @if (inviato) {
              <div class="text-center">
                <i class="bi bi-envelope-check-fill text-success" style="font-size: 3rem;"></i>
                <h4 class="mt-3">Email inviata!</h4>
                <p class="text-muted">
                  Se l'email è registrata, riceverai le istruzioni per recuperare la password.
                </p>
                <p class="text-muted small">Controlla anche la cartella spam.</p>
                <a routerLink="/login" class="btn btn-dark mt-2">Torna al login</a>
              </div>
            }

          </div>
        </div>
      </div>
    </div>
  `
})
export class RecuperaPassword {
  email = '';
  loading = false;
  inviato = false;
  errore = '';

  constructor(
    private http: HttpClient,
    private cdr: ChangeDetectorRef
  ) {}

  invia() {
    if (!this.email) return;
    this.loading = true;
    this.errore = '';
    this.cdr.detectChanges();

    this.http.post('http://localhost:8080/api/auth/recupera-password',
      { email: this.email },
      { responseType: 'text' }
    ).subscribe({
      next: () => {
        this.inviato = true;
        this.loading = false;
        this.cdr.detectChanges();
      },
      error: () => {
        this.inviato = true;
        this.loading = false;
        this.cdr.detectChanges();
      }
    });
  }
}