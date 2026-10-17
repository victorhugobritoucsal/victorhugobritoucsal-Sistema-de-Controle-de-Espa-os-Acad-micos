import { Component, OnInit } from '@angular/core';
import { DatePipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ApiService, mensagemDeErro } from '../../../services/api.service';

@Component({
  selector: 'app-docentes',
  imports: [FormsModule, DatePipe],
  templateUrl: './docentes.component.html',
})
export class DocentesComponent implements OnInit {
  lista: any[] = [];
  centros: any[] = [];
  historico: any[] = [];
  docenteDoHistorico: any = null;
  form: any = this.novo();
  erro = '';

  constructor(private api: ApiService) {}

  ngOnInit() {
    this.carregar();
    this.api.get('/admin/centros').subscribe((dados) => (this.centros = dados));
  }

  novo() {
    return { matricula: '', nome: '', email: '', telefone: '', centroAcademico: { id: null } };
  }

  carregar() {
    this.api.get('/admin/docentes').subscribe((dados) => (this.lista = dados));
  }

  salvar() {
    const requisicao = this.form.id
      ? this.api.put('/admin/docentes/' + this.form.id, this.form)
      : this.api.post('/admin/docentes', this.form);
    requisicao.subscribe({
      next: () => {
        this.limpar();
        this.carregar();
      },
      error: (e) => (this.erro = mensagemDeErro(e)),
    });
  }

  editar(docente: any) {
    this.form = { ...docente };
  }

  limpar() {
    this.form = this.novo();
    this.erro = '';
  }

  inativar(docente: any) {
    const motivo = prompt('Motivo da inativação (afastamento, desligamento, suspensão contratual...):');
    if (motivo) {
      this.api.patch('/admin/docentes/' + docente.id + '/inativar', { motivo }).subscribe(() => this.carregar());
    }
  }

  reativar(docente: any) {
    this.api.patch('/admin/docentes/' + docente.id + '/reativar').subscribe(() => this.carregar());
  }

  verHistorico(docente: any) {
    this.docenteDoHistorico = docente;
    this.api.get('/admin/docentes/' + docente.id + '/historico').subscribe((dados) => (this.historico = dados));
  }
}
