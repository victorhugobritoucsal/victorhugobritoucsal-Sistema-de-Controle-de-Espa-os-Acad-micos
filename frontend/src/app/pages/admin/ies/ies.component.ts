import { Component, OnInit } from '@angular/core';
import { DatePipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ApiService, mensagemDeErro } from '../../../services/api.service';

@Component({
  selector: 'app-ies',
  imports: [FormsModule, DatePipe],
  templateUrl: './ies.component.html',
})
export class IesComponent implements OnInit {
  lista: any[] = [];
  form: any = this.novo();
  erro = '';

  constructor(private api: ApiService) {}

  ngOnInit() {
    this.carregar();
  }

  novo() {
    return { sigla: '', nome: '', email: '', telefone: '', reitor: '', proReitorias: [] };
  }

  carregar() {
    this.api.get('/admin/ies').subscribe((dados) => (this.lista = dados));
  }

  salvar() {
    const requisicao = this.form.id
      ? this.api.put('/admin/ies/' + this.form.id, this.form)
      : this.api.post('/admin/ies', this.form);
    requisicao.subscribe({
      next: () => {
        this.limpar();
        this.carregar();
      },
      error: (e) => (this.erro = mensagemDeErro(e)),
    });
  }

  editar(ies: any) {
    this.form = JSON.parse(JSON.stringify(ies));
  }

  limpar() {
    this.form = this.novo();
    this.erro = '';
  }

  alterarStatus(ies: any) {
    const acao = ies.status === 'ATIVO' ? 'inativar' : 'reativar';
    this.api.patch('/admin/ies/' + ies.id + '/' + acao).subscribe(() => this.carregar());
  }

  excluir(ies: any) {
    if (confirm('Deseja excluir a IES ' + ies.sigla + '?')) {
      this.api.delete('/admin/ies/' + ies.id).subscribe({
        next: () => this.carregar(),
        error: (e) => (this.erro = mensagemDeErro(e)),
      });
    }
  }

  adicionarProReitoria() {
    this.form.proReitorias.push({ nome: '', titular: '', nomeacao: '' });
  }

  removerProReitoria(indice: number) {
    this.form.proReitorias.splice(indice, 1);
  }
}
