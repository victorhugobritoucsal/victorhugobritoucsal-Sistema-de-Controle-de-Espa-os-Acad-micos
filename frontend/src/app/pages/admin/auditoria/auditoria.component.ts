import { Component, OnInit } from '@angular/core';
import { DatePipe } from '@angular/common';
import { ApiService } from '../../../services/api.service';

@Component({
  selector: 'app-auditoria',
  imports: [DatePipe],
  templateUrl: './auditoria.component.html',
})
export class AuditoriaComponent implements OnInit {
  logs: any[] = [];

  constructor(private api: ApiService) {}

  ngOnInit() {
    this.api.get('/admin/auditoria').subscribe((dados) => (this.logs = dados));
  }
}
