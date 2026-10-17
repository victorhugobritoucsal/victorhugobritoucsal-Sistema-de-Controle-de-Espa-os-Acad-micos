import { Component, OnInit } from '@angular/core';
import { DatePipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ApiService, mensagemDeErro } from '../../../services/api.service';

@Component({
  selector: 'app-reservas',
  imports: [FormsModule, DatePipe],
  templateUrl: './reservas.component.html',
})
export class ReservasComponent implements OnInit {
  lista: any[] = [];
  espacos: any[] = [];
  form: any = this.novo();
  erro = '';

  constructor(private api: ApiService) {}

  ngOnInit() {
    this.carregar();
    this.api.get('/usuario/espacos').subscribe((dados) => (this.espacos = dados));
  }

  novo() {
    return { espaco: { id: null }, data: '', horaInicio: '', horaFim: '' };
  }

  carregar() {
    this.api.get('/usuario/solicitacoes-espaco').subscribe((dados) => (this.lista = dados));
  }

  salvar() {
    const requisicao = this.form.id
      ? this.api.put('/usuario/solicitacoes-espaco/' + this.form.id, this.form)
      : this.api.post('/usuario/solicitacoes-espaco', this.form);
    requisicao.subscribe({
      next: () => {
        this.limpar();
        this.carregar();
      },
      error: (e) => (this.erro = mensagemDeErro(e)),
    });
  }

  editar(reserva: any) {
    this.form = {
      id: reserva.id,
      espaco: { id: reserva.espaco.id },
      data: reserva.data,
      horaInicio: reserva.horaInicio.slice(0, 5),
      horaFim: reserva.horaFim.slice(0, 5),
    };
  }

  limpar() {
    this.form = this.novo();
    this.erro = '';
  }

  inativar(reserva: any) {
    if (confirm('Deseja inativar esta solicitação?')) {
      this.api.patch('/usuario/solicitacoes-espaco/' + reserva.id + '/inativar').subscribe(() => this.carregar());
    }
  }

  liberar(reserva: any) {
    if (confirm('Confirma que terminou de usar o espaço?')) {
      this.api.patch('/usuario/solicitacoes-espaco/' + reserva.id + '/liberar').subscribe(() => this.carregar());
    }
  }
}
