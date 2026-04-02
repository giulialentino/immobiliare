import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-verifica-email',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="container mt-5">
      <div class="row justify-content-center">
        <div class="col-md-5">
          <div class="card shadow text-center p-4">

            @if (stato === 'caricamento') {
              <div class="spinner-border text-dark mx-auto mb-3" role="status"></div>
              <p class="text-muted">Verifica in corso...</p>
            }

            @if (stato === 'ok') {
              <i class="bi bi-check-circle-fill text-success" style="font-size: 3rem;"></i>
              <h4 class="mt-3">Email verificata!</h4>
              <p class="text-muted">Il tuo account è attivo. Ora puoi accedere.</p>
              <button class="btn btn-dark mt-2" (click)="router.navigate(['/login'])">
                Vai al login
              </button>
            }

            @if (stato === 'errore') {
              <i class="bi bi-x-circle-fill text-danger" style="font-size: 3rem;"></i>
              <h4 class="mt-3">Link non valido</h4>
              <p class="text-muted">Il link è scaduto o già utilizzato.</p>
              <button class="btn btn-dark mt-2" (click)="router.navigate(['/login'])">
                Torna al login
              </button>
            }

          </div>
        </div>
      </div>
    </div>
  `
})
export class VerificaEmail implements OnInit {
  stato: 'caricamento' | 'ok' | 'errore' = 'caricamento';

  constructor(
    private route: ActivatedRoute,
    public router: Router,
    private http: HttpClient,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit() {
    const token = this.route.snapshot.queryParamMap.get('token');
    if (!token) {
      this.stato = 'errore';
      this.cdr.detectChanges();
      return;
    }

    this.http.get('http://localhost:8080/api/auth/verifica-email',
      { params: { token }, responseType: 'text', withCredentials: true }
    ).subscribe({
      next: () => {
        this.stato = 'ok';
        this.cdr.detectChanges();
      },
      error: () => {
        this.stato = 'errore';
        this.cdr.detectChanges();
      }
    });
  }
}