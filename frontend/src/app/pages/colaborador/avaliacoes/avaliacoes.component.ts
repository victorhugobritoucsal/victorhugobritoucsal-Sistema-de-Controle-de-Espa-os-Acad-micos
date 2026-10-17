import { Component, OnInit } from '@angular/core';
import { DatePipe } from '@angular/common';
import { ApiService, mensagemDeErro } from '../../../services/api.service';

@Component({
  selector: 'app-avaliacoes',
  imports: [DatePipe],
  templateUrl: './avaliacoes.component.html',
})
export class AvaliacoesComponent implements OnInit {
  reservas: any[] = [];
  pedidosSoftware: any[] = [];
  erro = '';

  constructor(private api: ApiService) {}

  ngOnInit() {
    this.carregar();
  }

  carregar() {
    this.api.get('/colaborador/solicitacoes-espaco').subscribe((dados) => (this.reservas = dados));
    this.api.get('/colaborador/solicitacoes-software').subscribe((dados) => (this.pedidosSoftware = dados));
  }

  aprovar(tipo: string, id: number) {
    this.erro = '';
    this.api.patch('/colaborador/' + tipo + '/' + id + '/aprovar').subscribe({
      next: () => this.carregar(),
      error: (e) => (this.erro = mensagemDeErro(e)),
    });
  }

  reprovar(tipo: string, id: number) {
    const justificativa = prompt('Justificativa da reprovação:');
    if (justificativa) {
      this.api.patch('/colaborador/' + tipo + '/' + id + '/reprovar', { justificativa }).subscribe({
        next: () => this.carregar(),
        error: (e) => (this.erro = mensagemDeErro(e)),
      });
    }
  }

  nomesDosSoftwares(pedido: any) {
    return pedido.softwares.map((software: any) => software.nome).join(', ');
  }
}
