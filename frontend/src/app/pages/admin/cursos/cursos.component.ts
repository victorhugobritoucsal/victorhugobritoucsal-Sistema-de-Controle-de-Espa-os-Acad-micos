import { Component, OnInit } from '@angular/core';
import { DatePipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ApiService, mensagemDeErro } from '../../../services/api.service';

@Component({
  selector: 'app-cursos',
  imports: [FormsModule, DatePipe],
  templateUrl: './cursos.component.html',
})
export class CursosComponent implements OnInit {
  lista: any[] = [];
  centros: any[] = [];
  docentes: any[] = [];
  turnos = ['Matutino', 'Vespertino', 'Noturno', 'Integral'];
  form: any = this.novo();
  erro = '';

  constructor(private api: ApiService) {}

  ngOnInit() {
    this.carregar();
    this.api.get('/admin/centros').subscribe((dados) => (this.centros = dados));
    this.api.get('/admin/docentes').subscribe((dados) => (this.docentes = dados));
  }

  novo() {
    return { sigla: '', descricao: '', centroAcademico: { id: null }, turnos: [], coordenadorId: null };
  }

  carregar() {
    this.api.get('/admin/cursos').subscribe((dados) => (this.lista = dados));
  }

  alternarTurno(turno: string) {
    if (this.form.turnos.includes(turno)) {
      this.form.turnos = this.form.turnos.filter((t: string) => t !== turno);
    } else {
      this.form.turnos.push(turno);
    }
  }

  salvar() {
    const dados = { ...this.form, coordenador: this.form.coordenadorId ? { id: this.form.coordenadorId } : null };
    const requisicao = this.form.id
      ? this.api.put('/admin/cursos/' + this.form.id, dados)
      : this.api.post('/admin/cursos', dados);
    requisicao.subscribe({
      next: () => {
        this.limpar();
        this.carregar();
      },
      error: (e) => (this.erro = mensagemDeErro(e)),
    });
  }

  editar(curso: any) {
    this.form = { ...curso, turnos: [...curso.turnos], coordenadorId: curso.coordenador?.id ?? null };
  }

  limpar() {
    this.form = this.novo();
    this.erro = '';
  }

  alterarStatus(curso: any) {
    const acao = curso.status === 'ATIVO' ? 'inativar' : 'reativar';
    this.api.patch('/admin/cursos/' + curso.id + '/' + acao).subscribe(() => this.carregar());
  }

  excluir(curso: any) {
    if (confirm('Deseja excluir o curso ' + curso.sigla + '?')) {
      this.api.delete('/admin/cursos/' + curso.id).subscribe(() => this.carregar());
    }
  }
}
