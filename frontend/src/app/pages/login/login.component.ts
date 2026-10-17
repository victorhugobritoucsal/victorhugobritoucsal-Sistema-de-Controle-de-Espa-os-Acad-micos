import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { mensagemDeErro } from '../../services/api.service';

@Component({
  selector: 'app-login',
  imports: [FormsModule],
  templateUrl: './login.component.html',
})
export class LoginComponent {
  login = '';
  senha = '';
  erro = '';

  constructor(private auth: AuthService, private router: Router) {}

  entrar() {
    this.erro = '';
    this.auth.login(this.login, this.senha).subscribe({
      next: () => this.router.navigate(['/inicio']),
      error: (e) => (this.erro = mensagemDeErro(e)),
    });
  }
}
