import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, BehaviorSubject } from 'rxjs';
import { tap } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrl = 'http://localhost:8080/api/auth';
  private utenteSubject = new BehaviorSubject<any>(null);
  utente$ = this.utenteSubject.asObservable();

  constructor(private http: HttpClient) {
    this.getUtenteLoggato().subscribe({
      next: (u) => this.utenteSubject.next(u),
      error: () => this.utenteSubject.next(null)
    });
  }

  clearUtente() {
    this.utenteSubject.next(null);
  }

  setUtente(u: any) {
    this.utenteSubject.next(u);
  }

  login(email: string, password: string): Observable<any> {
    return this.http.post(`${this.apiUrl}/login`, { email, password }, { withCredentials: true }).pipe(
      tap(() => {
        this.getUtenteLoggato().subscribe({
          next: (u) => this.utenteSubject.next(u),
          error: () => this.utenteSubject.next(null)
        });
      })
    );
  }

  logout(): Observable<any> {
    return this.http.post(`${this.apiUrl}/logout`, {}, { withCredentials: true }).pipe(
      tap(() => this.utenteSubject.next(null))
    );
  }

  getUtenteLoggato(): Observable<any> {
    return this.http.get(`${this.apiUrl}/me`, { withCredentials: true });
  }

  registra(utente: any): Observable<any> {
    return this.http.post(`${this.apiUrl}/registra`, utente, {
      withCredentials: true,
      responseType: 'text'
    });
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
    return this.http.post(
      `${this.apiUrl}/cambia-password`,
      { vecchiaPassword, nuovaPassword },
      { withCredentials: true, responseType: 'text' }
    );
  }

  uploadFotoProfilo(file: File): Observable<any> {
    const formData = new FormData();
    formData.append('file', file);
    return this.http.post(
      `${this.apiUrl}/foto-profilo`,
      formData,
      { withCredentials: true, responseType: 'text' }
    );
  }

  rimuoviFotoProfilo(): Observable<any> {
    return this.http.delete(
      `${this.apiUrl}/foto-profilo`,
      { withCredentials: true, responseType: 'text' }
    );
  }

  getUtenteById(id: number): Observable<any> {
    return this.http.get(`${this.apiUrl}/utente/${id}`, { withCredentials: true });
  }
}