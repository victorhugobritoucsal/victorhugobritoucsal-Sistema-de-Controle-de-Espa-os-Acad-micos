import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { firstValueFrom, tap } from 'rxjs';
import { ApiService } from './api.service';

@Injectable({ providedIn: 'root' })
export class AuthService {
  usuario: any = null;

  constructor(private api: ApiService, private router: Router) {}

  carregarUsuario() {
    return firstValueFrom(this.api.get('/auth/me'))
      .then((usuario) => (this.usuario = usuario))
      .catch(() => (this.usuario = null));
  }

  login(login: string, senha: string) {
    return this.api.post('/auth/login', { login, senha }).pipe(tap((usuario) => (this.usuario = usuario)));
  }

  logout() {
    this.api.post('/auth/logout').subscribe();
    this.usuario = null;
    this.router.navigate(['/login']);
  }

  temPerfil(perfil: string): boolean {
    return this.usuario?.perfis?.includes(perfil) ?? false;
  }
}
