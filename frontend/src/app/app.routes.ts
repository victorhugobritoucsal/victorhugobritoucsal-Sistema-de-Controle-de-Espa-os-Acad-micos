import { Routes } from '@angular/router';
import { authGuard } from './guards/auth.guard';
import { LoginComponent } from './pages/login/login.component';
import { InicioComponent } from './pages/inicio/inicio.component';
import { IesComponent } from './pages/admin/ies/ies.component';
import { CentrosComponent } from './pages/admin/centros/centros.component';
import { CursosComponent } from './pages/admin/cursos/cursos.component';
import { DocentesComponent } from './pages/admin/docentes/docentes.component';
import { UsuariosComponent } from './pages/admin/usuarios/usuarios.component';
import { AuditoriaComponent } from './pages/admin/auditoria/auditoria.component';
import { RelatoriosComponent } from './pages/admin/relatorios/relatorios.component';
import { EspacosComponent } from './pages/colaborador/espacos/espacos.component';
import { AvaliacoesComponent } from './pages/colaborador/avaliacoes/avaliacoes.component';
import { RelatorioColaboradorComponent } from './pages/colaborador/relatorio/relatorio-colaborador.component';
import { ReservasComponent } from './pages/usuario/reservas/reservas.component';
import { SoftwaresComponent } from './pages/usuario/softwares/softwares.component';
import { RelatorioUsuarioComponent } from './pages/usuario/relatorio/relatorio-usuario.component';

const admin = { perfil: 'ADMINISTRADOR' };
const colaborador = { perfil: 'COLABORADOR' };
const usuario = { perfil: 'USUARIO' };

export const routes: Routes = [
  { path: 'login', component: LoginComponent },
  { path: 'inicio', component: InicioComponent, canActivate: [authGuard] },
  { path: 'admin/ies', component: IesComponent, canActivate: [authGuard], data: admin },
  { path: 'admin/centros', component: CentrosComponent, canActivate: [authGuard], data: admin },
  { path: 'admin/cursos', component: CursosComponent, canActivate: [authGuard], data: admin },
  { path: 'admin/docentes', component: DocentesComponent, canActivate: [authGuard], data: admin },
  { path: 'admin/usuarios', component: UsuariosComponent, canActivate: [authGuard], data: admin },
  { path: 'admin/auditoria', component: AuditoriaComponent, canActivate: [authGuard], data: admin },
  { path: 'admin/relatorios', component: RelatoriosComponent, canActivate: [authGuard], data: admin },
  { path: 'colaborador/espacos', component: EspacosComponent, canActivate: [authGuard], data: colaborador },
  { path: 'colaborador/avaliacoes', component: AvaliacoesComponent, canActivate: [authGuard], data: colaborador },
  { path: 'colaborador/relatorio', component: RelatorioColaboradorComponent, canActivate: [authGuard], data: colaborador },
  { path: 'usuario/reservas', component: ReservasComponent, canActivate: [authGuard], data: usuario },
  { path: 'usuario/softwares', component: SoftwaresComponent, canActivate: [authGuard], data: usuario },
  { path: 'usuario/relatorio', component: RelatorioUsuarioComponent, canActivate: [authGuard], data: usuario },
  { path: '**', redirectTo: 'inicio' },
];
