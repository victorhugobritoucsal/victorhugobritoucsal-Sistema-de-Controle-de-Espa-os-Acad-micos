import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class ApiService {
  constructor(private http: HttpClient) {}

  get(url: string): Observable<any> {
    return this.http.get('/api' + url);
  }

  post(url: string, dados: any = {}): Observable<any> {
    return this.http.post('/api' + url, dados);
  }

  put(url: string, dados: any): Observable<any> {
    return this.http.put('/api' + url, dados);
  }

  patch(url: string, dados: any = {}): Observable<any> {
    return this.http.patch('/api' + url, dados);
  }

  delete(url: string): Observable<any> {
    return this.http.delete('/api' + url);
  }
}

export function mensagemDeErro(erro: any): string {
  if (erro.status === 403) {
    return 'Você não tem permissão para esta ação.';
  }
  return erro.error?.mensagem ?? 'Não foi possível comunicar com o servidor.';
}
