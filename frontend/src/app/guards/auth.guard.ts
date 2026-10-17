import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

export const authGuard: CanActivateFn = (route) => {
  const auth = inject(AuthService);
  const router = inject(Router);

  if (!auth.usuario) {
    return router.parseUrl('/login');
  }
  const perfil = route.data['perfil'];
  if (perfil && !auth.temPerfil(perfil)) {
    return router.parseUrl('/inicio');
  }
  return true;
};
