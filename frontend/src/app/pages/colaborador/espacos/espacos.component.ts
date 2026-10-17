import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ApiService, mensagemDeErro } from '../../../services/api.service';

@Component({
  selector: 'app-espacos',
  imports: [FormsModule],
  templateUrl: './espacos.component.html',
})
export class EspacosComponent implements OnInit {
  lista: any[] = [];
  form: any = this.novo();
  erro = '';

  constructor(private api: ApiService) {}

  ngOnInit() {
    this.carregar();
  }

  novo() {
    return {
      sigla: '', nome: '', tipo: 'SALA', descricao: '', capacidade: null, localizacao: '',
      laboratorioInformatica: false, tipoComputadores: '', softwares: [],
    };
  }

  carregar() {
    this.api.get('/colaborador/espacos').subscribe((dados) => (this.lista = dados));
  }

  salvar() {
    const requisicao = this.form.id
      ? this.api.put('/colaborador/espacos/' + this.form.id, this.form)
      : this.api.post('/colaborador/espacos', this.form);
    requisicao.subscribe({
      next: () => {
        this.limpar();
        this.carregar();
      },
      error: (e) => (this.erro = mensagemDeErro(e)),
    });
  }

  editar(espaco: any) {
    this.form = JSON.parse(JSON.stringify(espaco));
  }

  limpar() {
    this.form = this.novo();
    this.erro = '';
  }

  adicionarSoftware() {
    this.form.softwares.push({ nome: '', urlDownload: '', tipo: 'LIVRE', objetivo: '', disciplinas: '' });
  }

  removerSoftware(indice: number) {
    this.form.softwares.splice(indice, 1);
  }

  inativar(espaco: any) {
    const motivo = prompt('Motivo da inativação (manutenção, desativação...):');
    if (motivo) {
      this.api.patch('/colaborador/espacos/' + espaco.id + '/inativar', { motivo }).subscribe(() => this.carregar());
    }
  }

  reativar(espaco: any) {
    this.api.patch('/colaborador/espacos/' + espaco.id + '/reativar').subscribe(() => this.carregar());
  }

  excluir(espaco: any) {
    if (confirm('Deseja excluir o espaço ' + espaco.sigla + '?')) {
      this.api.delete('/colaborador/espacos/' + espaco.id).subscribe({
        next: () => this.carregar(),
        error: (e) => (this.erro = mensagemDeErro(e)),
      });
    }
  }
}
