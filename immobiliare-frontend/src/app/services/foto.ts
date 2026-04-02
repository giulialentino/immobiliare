import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class FotoService {

  private apiUrl = 'http://localhost:8080/api/foto';

  constructor(private http: HttpClient) {}

  upload(idAnnuncio: number, file: File): Observable<string> {
    const formData = new FormData();
    formData.append('file', file);
    return this.http.post<string>(
      `${this.apiUrl}/upload/${idAnnuncio}`,
      formData,
      { withCredentials: true, responseType: 'text' as 'json' }
    );
  }

  getFoto(idAnnuncio: number): Observable<string[]> {
    return this.http.get<string[]>(
      `${this.apiUrl}/annuncio/${idAnnuncio}`,
      { withCredentials: true }
    );
  }
elimina(idAnnuncio: number, url: string): Observable<any> {
  return this.http.delete(
    `${this.apiUrl}/${idAnnuncio}`,
    { 
      withCredentials: true, 
      responseType: 'text',
      body: url
    }
  );
}
}