import { Component, OnInit } from '@angular/core';
import { DatePipe } from '@angular/common';
import { ApiService } from '../../../services/api.service';

@Component({
  selector: 'app-relatorio-usuario',
  imports: [DatePipe],
  templateUrl: './relatorio-usuario.component.html',
})
export class RelatorioUsuarioComponent implements OnInit {
  relatorio: any = null;

  constructor(private api: ApiService) {}

  ngOnInit() {
    this.api.get('/usuario/relatorio').subscribe((dados) => (this.relatorio = dados));
  }

  nomesDosSoftwares(pedido: any) {
    return pedido.softwares.map((software: any) => software.nome).join(', ');
  }

  imprimir() {
    window.print();
  }
}
