import { Component } from '@angular/core';
import { DatePipe } from '@angular/common';
import { ApiService } from '../../../services/api.service';

@Component({
  selector: 'app-relatorios',
  imports: [DatePipe],
  templateUrl: './relatorios.component.html',
})
export class RelatoriosComponent {
  tipo = '';
  relatorio: any = null;

  constructor(private api: ApiService) {}

  emitir(tipo: string) {
    this.tipo = tipo;
    this.api.get('/admin/relatorios/' + tipo).subscribe((dados) => (this.relatorio = dados));
  }

  centrosDa(ies: any) {
    return this.relatorio.centros.filter((centro: any) => centro.ies.id === ies.id);
  }

  cursosDo(centro: any) {
    return this.relatorio.cursos.filter((curso: any) => curso.centroAcademico.id === centro.id);
  }

  imprimir() {
    window.print();
  }
}
