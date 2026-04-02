import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-reset-password',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="container mt-5">
      <div class="row justify-content-center">
        <div class="col-md-5">
          <div class="card shadow p-4">

            @if (stato === 'form') {
              <h4 class="text-center mb-3">
                <i class="bi bi-shield-lock-fill"></i> Nuova password
              </h4>
              <p class="text-muted text-center small">
                Scegli una nuova password per il tuo account.
              </p>
              <div class="mb-3">
                <label class="form-label">Nuova password</label>
                <div class="input-group">
                  <input [type]="mostraPassword ? 'text' : 'password'"
                         class="form-control"
                         [(ngModel)]="nuovaPassword"
                         placeholder="Min. 8 caratteri, 1 maiuscola, 1 numero" />
                  <button class="btn btn-outline-secondary" type="button"
                          (click)="mostraPassword = !mostraPassword">
                    <i [class]="mostraPassword ? 'bi bi-eye-slash' : 'bi bi-eye'"></i>
                  </button>
                </div>

                @if (nuovaPassword) {
                  <div class="mt-2 p-3 rounded" style="background: #f8f9fa; border: 1px solid #eee;">
                    <div class="small mb-1">
                      <i [class]="nuovaPassword.length >= 8 ? 'bi bi-check-circle-fill text-success' : 'bi bi-x-circle-fill text-danger'"></i>
                      Almeno 8 caratteri
                    </div>
                    <div class="small mb-1">
                      <i [class]="haMaiuscola() ? 'bi bi-check-circle-fill text-success' : 'bi bi-x-circle-fill text-danger'"></i>
                      Almeno una lettera maiuscola
                    </div>
                    <div class="small">
                      <i [class]="haNumero() ? 'bi bi-check-circle-fill text-success' : 'bi bi-x-circle-fill text-danger'"></i>
                      Almeno un numero
                    </div>
                  </div>
                }
              </div>

              <div class="mb-3">
                <label class="form-label">Conferma password</label>
                <input type="password" class="form-control"
                       [(ngModel)]="conferma"
                       placeholder="Ripeti la password" />
                @if (conferma && nuovaPassword !== conferma) {
                  <div class="small text-danger mt-1">
                    <i class="bi bi-x-circle-fill"></i> Le password non coincidono
                  </div>
                }
                @if (conferma && nuovaPassword === conferma) {
                  <div class="small text-success mt-1">
                    <i class="bi bi-check-circle-fill"></i> Le password coincidono
                  </div>
                }
              </div>

              @if (errore) {
                <div class="alert alert-danger py-2 small">{{ errore }}</div>
              }

              <button class="btn btn-dark w-100" (click)="reset()"
                      [disabled]="!passwordValida() || nuovaPassword !== conferma || loading">
                {{ loading ? 'Salvataggio...' : 'Salva nuova password' }}
              </button>
            }

            @if (stato === 'ok') {
              <div class="text-center">
                <i class="bi bi-check-circle-fill text-success" style="font-size: 3rem;"></i>
                <h4 class="mt-3">Password aggiornata!</h4>
                <p class="text-muted">Ora puoi accedere con la nuova password.</p>
                <button class="btn btn-dark mt-2" (click)="router.navigate(['/login'])">
                  Vai al login
                </button>
              </div>
            }

            @if (stato === 'errore') {
              <div class="text-center">
                <i class="bi bi-x-circle-fill text-danger" style="font-size: 3rem;"></i>
                <h4 class="mt-3">Link scaduto</h4>
                <p class="text-muted">Il link è scaduto o non valido.</p>
                <button class="btn btn-dark mt-2"
                        (click)="router.navigate(['/recupera-password'])">
                  Richiedi nuovo link
                </button>
              </div>
            }

          </div>
        </div>
      </div>
    </div>
  `
})
export class ResetPassword implements OnInit {
  stato: 'form' | 'ok' | 'errore' = 'form';
  nuovaPassword = '';
  conferma = '';
  errore = '';
  loading = false;
  mostraPassword = false;
  token = '';

  constructor(
    private route: ActivatedRoute,
    public router: Router,
    private http: HttpClient,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit() {
    this.token = this.route.snapshot.queryParamMap.get('token') || '';
    if (!this.token) {
      this.stato = 'errore';
      this.cdr.detectChanges();
    }
  }

  haMaiuscola(): boolean {
    return /[A-Z]/.test(this.nuovaPassword);
  }

  haNumero(): boolean {
    return /[0-9]/.test(this.nuovaPassword);
  }

  passwordValida(): boolean {
    return this.nuovaPassword.length >= 8 && this.haMaiuscola() && this.haNumero();
  }

  reset() {
    this.errore = '';

    if (!this.passwordValida()) {
      this.errore = 'La password non rispetta i requisiti richiesti';
      this.cdr.detectChanges();
      return;
    }
    if (this.nuovaPassword !== this.conferma) {
      this.errore = 'Le password non coincidono';
      this.cdr.detectChanges();
      return;
    }

    this.loading = true;
    this.cdr.detectChanges();

    this.http.post('http://localhost:8080/api/auth/reset-password',
      { token: this.token, nuovaPassword: this.nuovaPassword },
      { responseType: 'text' }
    ).subscribe({
      next: () => {
        this.stato = 'ok';
        this.loading = false;
        this.cdr.detectChanges();
      },
      error: () => {
        this.stato = 'errore';
        this.loading = false;
        this.cdr.detectChanges();
      }
    });
  }
}