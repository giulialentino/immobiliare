import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AstaService {

  private apiUrl = 'http://localhost:8080/api/aste';

  constructor(private http: HttpClient) {}

  getByAnnuncio(idAnnuncio: number): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/annuncio/${idAnnuncio}`, { withCredentials: true });
  }

  crea(asta: any): Observable<any> {
    return this.http.post<any>(this.apiUrl, asta, { withCredentials: true });
  }

  faiOfferta(idAsta: number, importo: number): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/${idAsta}/offerta`, { importo }, { withCredentials: true });
  }

  getOfferte(idAsta: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/${idAsta}/offerte`, { withCredentials: true });
  }

  chiudi(idAsta: number): Observable<any> {
    return this.http.patch<any>(`${this.apiUrl}/${idAsta}/chiudi`, {}, { withCredentials: true });
  }
  uploadFotoProfilo(file: File): Observable<any> {
  const formData = new FormData();
  formData.append('file', file);
  return this.http.post(
    'http://localhost:8080/api/auth/foto-profilo',
    formData,
    { withCredentials: true, responseType: 'text' }
  );
}

rimuoviFotoProfilo(): Observable<any> {
  return this.http.delete(
    'http://localhost:8080/api/auth/foto-profilo',
    { withCredentials: true, responseType: 'text' }
  );
}
}