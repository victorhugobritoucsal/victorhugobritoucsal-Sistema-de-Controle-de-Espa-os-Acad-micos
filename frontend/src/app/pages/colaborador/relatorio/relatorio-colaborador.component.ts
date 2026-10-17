import { Component, OnInit } from '@angular/core';
import { DatePipe } from '@angular/common';
import { ApiService } from '../../../services/api.service';

@Component({
  selector: 'app-relatorio-colaborador',
  imports: [DatePipe],
  templateUrl: './relatorio-colaborador.component.html',
})
export class RelatorioColaboradorComponent implements OnInit {
  relatorio: any = null;

  constructor(private api: ApiService) {}

  ngOnInit() {
    this.api.get('/colaborador/relatorio').subscribe((dados) => (this.relatorio = dados));
  }

  imprimir() {
    window.print();
  }
}
