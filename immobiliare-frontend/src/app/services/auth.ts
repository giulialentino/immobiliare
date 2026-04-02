import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, BehaviorSubject } from 'rxjs';
import { tap, switchMap, catchError } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrl = 'http://localhost:8080/api/auth';
  
  // Il BehaviorSubject inizializzato a null (utente non loggato)
  private utenteSubject = new BehaviorSubject<any>(null);
  
  // Observable a cui si iscriverà la Navbar
  utente$ = this.utenteSubject.asObservable();

  constructor(private http: HttpClient) {
    // Al caricamento dell'app, verifichiamo se c'è una sessione attiva
    this.checkSession();
  }

  private checkSession() {
    this.getUtenteLoggato().subscribe({
      next: (u) => this.utenteSubject.next(u),
      error: () => this.utenteSubject.next(null)
    });
  }

  // Metodo per forzare il reset manuale se necessario
  clearUtente() {
    this.utenteSubject.next(null);
  }

  // Metodo per aggiornare l'utente manualmente
  setUtente(u: any) {
    this.utenteSubject.next(u);
  }

  login(email: string, password: string): Observable<any> {
    return this.http.post(`${this.apiUrl}/login`, { email, password }, { withCredentials: true }).pipe(
      // switchMap concatena il login con la chiamata per ottenere i dati utente
      switchMap(() => this.getUtenteLoggato()),
      tap((u) => {
        this.utenteSubject.next(u); // Emette l'utente: la Navbar si aggiornerà SUBITO
      }),
      catchError(err => {
        this.utenteSubject.next(null);
        throw err;
      })
    );
  }

  logout(): Observable<any> {
    return this.http.post(`${this.apiUrl}/logout`, {}, { withCredentials: true }).pipe(
      tap(() => {
        this.utenteSubject.next(null); // Svuota l'utente: la Navbar nasconderà i tasti SUBITO
      }),
      catchError(err => {
        this.utenteSubject.next(null); // Anche in caso di errore, puliamo il client
        throw err;
      })
    );
  }

  getUtenteLoggato(): Observable<any> {
    return this.http.get(`${this.apiUrl}/me`, { withCredentials: true });
  }

  // --- Altri metodi gestionali ---
  registra(utente: any): Observable<any> {
    return this.http.post(`${this.apiUrl}/registra`, utente, { withCredentials: true, responseType: 'text' });
  }

  getUtenti(): Observable<any[]> {
    return this.http.get<any[]>('http://localhost:8080/api/utenti', { withCredentials: true });
  }

  banna(id: number): Observable<any> {
    return this.http.patch(`http://localhost:8080/api/utenti/${id}/banna`, {}, { withCredentials: true });
  }

  promuovi(id: number): Observable<any> {
    return this.http.patch(`http://localhost:8080/api/utenti/${id}/promuovi`, {}, { withCredentials: true });
  }

  cambiaPassword(vecchiaPassword: string, nuovaPassword: string): Observable<any> {
    return this.http.post(`${this.apiUrl}/cambia-password`, { vecchiaPassword, nuovaPassword }, { withCredentials: true, responseType: 'text' });
  }

  uploadFotoProfilo(file: File): Observable<any> {
    const formData = new FormData();
    formData.append('file', file);
    return this.http.post(`${this.apiUrl}/foto-profilo`, formData, { withCredentials: true, responseType: 'text' });
  }

  rimuoviFotoProfilo(): Observable<any> {
    return this.http.delete(`${this.apiUrl}/foto-profilo`, { withCredentials: true, responseType: 'text' });
  }

  getUtenteById(id: number): Observable<any> {
    return this.http.get(`${this.apiUrl}/utente/${id}`, { withCredentials: true });
  }
}