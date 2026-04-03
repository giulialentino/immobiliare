import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AnnuncioService {

  private apiUrl = 'http://localhost:8080/api/annunci';

  constructor(private http: HttpClient) {}

  getAll(tipoOperazione?: string, idCategoria?: number): Observable<any[]> {
    let params: any = {};
    if (tipoOperazione) params['tipoOperazione'] = tipoOperazione;
    if (idCategoria) params['idCategoria'] = idCategoria;
    return this.http.get<any[]>(this.apiUrl, { params, withCredentials: true });
  }

  getById(id: number): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/${id}`, { withCredentials: true });
  }

  crea(annuncio: any): Observable<any> {
    return this.http.post<any>(this.apiUrl, annuncio, { withCredentials: true });
  }

  modifica(id: number, annuncio: any): Observable<any> {
    return this.http.put<any>(`${this.apiUrl}/${id}`, annuncio, { withCredentials: true });
  }

  elimina(id: number): Observable<any> {
    return this.http.delete<any>(`${this.apiUrl}/${id}`, { withCredentials: true });
  }

  ribassaPrezzo(id: number, nuovoPrezzo: number): Observable<any> {
    return this.http.patch<any>(
      `${this.apiUrl}/${id}/ribassa`,
      { prezzo: nuovoPrezzo },
      { withCredentials: true }
    );
  }

  annullaRibasso(id: number): Observable<any> {
    return this.http.patch<any>(
      `${this.apiUrl}/${id}/annulla-ribasso`,
      {},
      { withCredentials: true }
    );
  }

  // ===== MESSAGGI =====
  inviaMessaggio(messaggio: any): Observable<any> {
    return this.http.post(
      'http://localhost:8080/api/messaggi',
      messaggio,
      { withCredentials: true }
    );
  }

  getMessaggiVenditore(idVenditore: number): Observable<any[]> {
    return this.http.get<any[]>(
      `http://localhost:8080/api/messaggi/venditore/${idVenditore}`,
      { withCredentials: true }
    );
  }

  getMessaggiInviati(): Observable<any[]> {
    return this.http.get<any[]>(
      'http://localhost:8080/api/messaggi/inviati',
      { withCredentials: true }
    );
  }

  countMessaggi(idVenditore: number): Observable<number> {
    return this.http.get<number>(
      `http://localhost:8080/api/messaggi/count/${idVenditore}`,
      { withCredentials: true }
    );
  }

  eliminaMessaggio(id: number): Observable<any> {
    return this.http.delete(
      `http://localhost:8080/api/messaggi/${id}`,
      { withCredentials: true, responseType: 'text' }
    );
  }

  eliminaTuttiMessaggi(idVenditore: number): Observable<any> {
    return this.http.delete(
      `http://localhost:8080/api/messaggi/tutti/${idVenditore}`,
      { withCredentials: true, responseType: 'text' }
    );
  }

  eliminaMieiMessaggi(): Observable<any> {
    return this.http.delete(
      'http://localhost:8080/api/messaggi/miei',
      { withCredentials: true, responseType: 'text' }
    );
  }
  segnaMessaggioLetto(id: number): Observable<any> {
  return this.http.patch(
    `http://localhost:8080/api/messaggi/${id}/letto`,
    {},
    { withCredentials: true, responseType: 'text' }
  );
}
getInAttesa(): Observable<any[]> {
  return this.http.get<any[]>(
    `${this.apiUrl}/in-attesa`,
    { withCredentials: true }
  );
}

aggiornaStato(id: number, stato: string): Observable<any> {
  return this.http.patch(
    `${this.apiUrl}/${id}/stato`,
    { stato },
    { withCredentials: true, responseType: 'text' }
  );
}

  // ===== RECENSIONI =====
  inviaRecensione(recensione: any): Observable<any> {
    return this.http.post(
      'http://localhost:8080/api/recensioni',
      recensione,
      { withCredentials: true }
    );
  }
  countInAttesa(): Observable<number> {
  return this.http.get<number>(
    `${this.apiUrl}/count-in-attesa`,
    { withCredentials: true }
  );
}
getMessaggiAdmin(): Observable<any[]> {
  return this.http.get<any[]>(
    'http://localhost:8080/api/messaggi/admin',
    { withCredentials: true }
  );
}

countMessaggiAdmin(): Observable<number> {
  return this.http.get<number>(
    'http://localhost:8080/api/messaggi/count-admin',
    { withCredentials: true }
  );
}
getTuttiAnnunciVenditore(idVenditore: number): Observable<any[]> {
  return this.http.get<any[]>(
    `${this.apiUrl}/venditore/${idVenditore}`,
    { withCredentials: true }
  );
}
richiediPromozione(): Observable<any> {
  return this.http.post(
    'http://localhost:8080/api/promozione/richiedi',
    {},
    { withCredentials: true, responseType: 'text' }
  );
}

approvaPromozione(idUtente: number): Observable<any> {
  return this.http.post(
    `http://localhost:8080/api/promozione/approva/${idUtente}`,
    {},
    { withCredentials: true, responseType: 'text' }
  );
}

rifiutaPromozione(idUtente: number): Observable<any> {
  return this.http.post(
    `http://localhost:8080/api/promozione/rifiuta/${idUtente}`,
    {},
    { withCredentials: true, responseType: 'text' }
  );
}
getStatoPromozione(): Observable<string> {
  return this.http.get(
    'http://localhost:8080/api/promozione/stato',
    { withCredentials: true, responseType: 'text' }
  );
}

segnaGestita(id: number): Observable<any> {
  return this.http.patch(
    `http://localhost:8080/api/segnalazioni/${id}/gestita`,
    {},
    { withCredentials: true, responseType: 'text' }
  );
}

getSegnalazioni(): Observable<any[]> {
  return this.http.get<any[]>(
    'http://localhost:8080/api/segnalazioni',
    { withCredentials: true }
  );
}
}