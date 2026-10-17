import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ApiService, mensagemDeErro } from '../../../services/api.service';

@Component({
  selector: 'app-usuarios',
  imports: [FormsModule],
  templateUrl: './usuarios.component.html',
})
export class UsuariosComponent implements OnInit {
  lista: any[] = [];
  docentes: any[] = [];
  perfis = ['ADMINISTRADOR', 'COLABORADOR', 'USUARIO'];
  form: any = this.novo();
  erro = '';

  constructor(private api: ApiService) {}

  ngOnInit() {
    this.carregar();
    this.api.get('/admin/docentes').subscribe((dados) => (this.docentes = dados));
  }

  novo() {
    return { login: '', nome: '', senha: '', docenteId: null, perfis: [] };
  }

  carregar() {
    this.api.get('/admin/usuarios').subscribe((dados) => (this.lista = dados));
  }

  alternarPerfil(perfil: string) {
    if (this.form.perfis.includes(perfil)) {
      this.form.perfis = this.form.perfis.filter((p: string) => p !== perfil);
    } else {
      this.form.perfis.push(perfil);
    }
  }

  salvar() {
    const dados = { ...this.form, docente: this.form.docenteId ? { id: this.form.docenteId } : null };
    const requisicao = this.form.id
      ? this.api.put('/admin/usuarios/' + this.form.id, dados)
      : this.api.post('/admin/usuarios', dados);
    requisicao.subscribe({
      next: () => {
        this.limpar();
        this.carregar();
      },
      error: (e) => (this.erro = mensagemDeErro(e)),
    });
  }

  editar(usuario: any) {
    this.form = { ...usuario, senha: '', perfis: [...usuario.perfis], docenteId: usuario.docente?.id ?? null };
  }

  limpar() {
    this.form = this.novo();
    this.erro = '';
  }

  alterarStatus(usuario: any) {
    const acao = usuario.ativo ? 'inativar' : 'reativar';
    this.api.patch('/admin/usuarios/' + usuario.id + '/' + acao).subscribe(() => this.carregar());
  }
}
