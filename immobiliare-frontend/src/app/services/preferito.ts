import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class PreferitoService {

  private apiUrl = 'http://localhost:8080/api/preferiti';

  constructor(private http: HttpClient) {}

  getPreferiti(): Observable<any[]> {
    return this.http.get<any[]>(this.apiUrl, { withCredentials: true });
  }

  isPreferito(idAnnuncio: number): Observable<boolean> {
    return this.http.get<boolean>(`${this.apiUrl}/${idAnnuncio}/check`, { withCredentials: true });
  }

  aggiungi(idAnnuncio: number): Observable<any> {
    return this.http.post(`${this.apiUrl}/${idAnnuncio}`, {}, { withCredentials: true, responseType: 'text' });
  }

  rimuovi(idAnnuncio: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}/${idAnnuncio}`, { withCredentials: true, responseType: 'text' });
  }
}