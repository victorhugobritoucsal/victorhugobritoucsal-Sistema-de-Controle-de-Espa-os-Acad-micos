import { Component, OnInit } from '@angular/core';
import { DatePipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ApiService, mensagemDeErro } from '../../../services/api.service';

@Component({
  selector: 'app-centros',
  imports: [FormsModule, DatePipe],
  templateUrl: './centros.component.html',
})
export class CentrosComponent implements OnInit {
  lista: any[] = [];
  listaIes: any[] = [];
  docentes: any[] = [];
  form: any = this.novo();
  erro = '';

  constructor(private api: ApiService) {}

  ngOnInit() {
    this.carregar();
    this.api.get('/admin/ies').subscribe((dados) => (this.listaIes = dados));
    this.api.get('/admin/docentes').subscribe((dados) => (this.docentes = dados));
  }

  novo() {
    return { sigla: '', nome: '', email: '', telefone: '', ies: { id: null }, coordenadorId: null };
  }

  carregar() {
    this.api.get('/admin/centros').subscribe((dados) => (this.lista = dados));
  }

  docentesDoCentro() {
    return this.docentes.filter((docente) => docente.centroAcademico.id === this.form.id);
  }

  salvar() {
    const dados = { ...this.form, coordenador: this.form.coordenadorId ? { id: this.form.coordenadorId } : null };
    const requisicao = this.form.id
      ? this.api.put('/admin/centros/' + this.form.id, dados)
      : this.api.post('/admin/centros', dados);
    requisicao.subscribe({
      next: () => {
        this.limpar();
        this.carregar();
      },
      error: (e) => (this.erro = mensagemDeErro(e)),
    });
  }

  editar(centro: any) {
    this.form = { ...centro, coordenadorId: centro.coordenador?.id ?? null };
  }

  limpar() {
    this.form = this.novo();
    this.erro = '';
  }

  alterarStatus(centro: any) {
    const acao = centro.status === 'ATIVO' ? 'inativar' : 'reativar';
    this.api.patch('/admin/centros/' + centro.id + '/' + acao).subscribe(() => this.carregar());
  }

  excluir(centro: any) {
    if (confirm('Deseja excluir o centro ' + centro.sigla + '?')) {
      this.api.delete('/admin/centros/' + centro.id).subscribe({
        next: () => this.carregar(),
        error: (e) => (this.erro = mensagemDeErro(e)),
      });
    }
  }
}
