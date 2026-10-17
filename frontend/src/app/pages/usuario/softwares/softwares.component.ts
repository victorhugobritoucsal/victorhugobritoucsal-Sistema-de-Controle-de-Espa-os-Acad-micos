import { Component, OnInit } from '@angular/core';
import { DatePipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ApiService, mensagemDeErro } from '../../../services/api.service';

@Component({
  selector: 'app-softwares',
  imports: [FormsModule, DatePipe],
  templateUrl: './softwares.component.html',
})
export class SoftwaresComponent implements OnInit {
  lista: any[] = [];
  laboratorios: any[] = [];
  form: any = this.novo();
  erro = '';

  constructor(private api: ApiService) {}

  ngOnInit() {
    this.carregar();
    this.api.get('/usuario/espacos').subscribe((dados) => {
      this.laboratorios = dados.filter((espaco: any) => espaco.laboratorioInformatica);
    });
  }

  novo() {
    return { espaco: { id: null }, softwares: [{ nome: '', urlDownload: '', tipo: 'LIVRE', objetivo: '' }] };
  }

  carregar() {
    this.api.get('/usuario/solicitacoes-software').subscribe((dados) => (this.lista = dados));
  }

  salvar() {
    const requisicao = this.form.id
      ? this.api.put('/usuario/solicitacoes-software/' + this.form.id, this.form)
      : this.api.post('/usuario/solicitacoes-software', this.form);
    requisicao.subscribe({
      next: () => {
        this.limpar();
        this.carregar();
      },
      error: (e) => (this.erro = mensagemDeErro(e)),
    });
  }

  editar(pedido: any) {
    this.form = JSON.parse(JSON.stringify(pedido));
  }

  limpar() {
    this.form = this.novo();
    this.erro = '';
  }

  adicionarSoftware() {
    this.form.softwares.push({ nome: '', urlDownload: '', tipo: 'LIVRE', objetivo: '' });
  }

  removerSoftware(indice: number) {
    this.form.softwares.splice(indice, 1);
  }

  inativar(pedido: any) {
    if (confirm('Deseja inativar este pedido?')) {
      this.api.patch('/usuario/solicitacoes-software/' + pedido.id + '/inativar').subscribe(() => this.carregar());
    }
  }

  nomesDosSoftwares(pedido: any) {
    return pedido.softwares.map((software: any) => software.nome).join(', ');
  }
}
