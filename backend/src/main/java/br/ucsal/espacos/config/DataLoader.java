package br.ucsal.espacos.config;

import java.time.LocalDateTime;

import br.ucsal.espacos.model.*;
import br.ucsal.espacos.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements CommandLineRunner {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private IesRepository iesRepository;

    @Autowired
    private CentroAcademicoRepository centroRepository;

    @Autowired
    private CursoRepository cursoRepository;

    @Autowired
    private DocenteRepository docenteRepository;

    @Autowired
    private EspacoRepository espacoRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${app.admin-senha:}")
    private String senhaAdmin;

    @Override
    public void run(String... args) {
        if (usuarioRepository.count() > 0) {
            return;
        }

        criarUsuario("admin", "Administrador", senhaAdmin.isBlank() ? "admin123" : senhaAdmin, null, Perfil.ADMINISTRADOR);

        Ies ucsal = new Ies();
        ucsal.setSigla("UCSAL");
        ucsal.setNome("Universidade Católica do Salvador");
        ucsal.setEmail("contato@ucsal.br");
        ucsal.setTelefone("(71) 3203-8800");
        ucsal.setReitor("Reitor da UCSAL");
        ucsal.setDataCadastro(LocalDateTime.now());
        ucsal.setStatus(Status.ATIVO);
        iesRepository.save(ucsal);

        CentroAcademico ccet = new CentroAcademico();
        ccet.setSigla("CCET");
        ccet.setNome("Escola de Engenharias e Ciências Tecnológicas");
        ccet.setEmail("ccet@ucsal.br");
        ccet.setTelefone("(71) 3203-8801");
        ccet.setIes(ucsal);
        ccet.setDataCadastro(LocalDateTime.now());
        ccet.setStatus(Status.ATIVO);
        centroRepository.save(ccet);

        Docente coordenadora = criarDocente("D0001", "Maria Coordenadora", ccet);
        Docente professor = criarDocente("D0002", "João Professor", ccet);
        ccet.setCoordenador(coordenadora);
        centroRepository.save(ccet);

        Curso bes = new Curso();
        bes.setSigla("BES");
        bes.setDescricao("Bacharelado em Engenharia de Software");
        bes.setCentroAcademico(ccet);
        bes.getTurnos().add("Matutino");
        bes.getTurnos().add("Noturno");
        bes.setCoordenador(coordenadora);
        bes.setDataCadastro(LocalDateTime.now());
        bes.setStatus(Status.ATIVO);
        cursoRepository.save(bes);

        criarUsuario("D0001", coordenadora.getNome(), "123456", coordenadora, Perfil.COLABORADOR, Perfil.USUARIO);
        criarUsuario("D0002", professor.getNome(), "123456", professor, Perfil.USUARIO);

        Espaco laboratorio = criarEspaco("LAB01", "Laboratório de Informática 01", TipoEspaco.LABORATORIO, 40, ccet);
        laboratorio.setLaboratorioInformatica(true);
        laboratorio.setTipoComputadores("Desktop Intel Core i5, 16 GB RAM");
        Software eclipse = new Software();
        eclipse.setNome("Eclipse IDE");
        eclipse.setUrlDownload("https://www.eclipse.org/downloads/");
        eclipse.setTipo(TipoSoftware.LIVRE);
        eclipse.setObjetivo("Desenvolvimento em Java");
        eclipse.setDisciplinas("Programação Orientada a Objetos");
        laboratorio.getSoftwares().add(eclipse);
        espacoRepository.save(laboratorio);

        espacoRepository.save(criarEspaco("S101", "Sala 101", TipoEspaco.SALA, 50, ccet));
    }

    private void criarUsuario(String login, String nome, String senha, Docente docente, Perfil... perfis) {
        Usuario usuario = new Usuario();
        usuario.setLogin(login);
        usuario.setNome(nome);
        usuario.setSenha(passwordEncoder.encode(senha));
        usuario.setDocente(docente);
        for (Perfil perfil : perfis) {
            usuario.getPerfis().add(perfil);
        }
        usuarioRepository.save(usuario);
    }

    private Docente criarDocente(String matricula, String nome, CentroAcademico centro) {
        Docente docente = new Docente();
        docente.setMatricula(matricula);
        docente.setNome(nome);
        docente.setEmail(matricula.toLowerCase() + "@ucsal.br");
        docente.setTelefone("(71) 99999-0000");
        docente.setCentroAcademico(centro);
        docente.setDataCadastro(LocalDateTime.now());
        docente.setStatus(Status.ATIVO);
        return docenteRepository.save(docente);
    }

    private Espaco criarEspaco(String sigla, String nome, TipoEspaco tipo, int capacidade, CentroAcademico centro) {
        Espaco espaco = new Espaco();
        espaco.setSigla(sigla);
        espaco.setNome(nome);
        espaco.setTipo(tipo);
        espaco.setCapacidade(capacidade);
        espaco.setLocalizacao("Campus Pituaçu");
        espaco.setCentroAcademico(centro);
        espaco.setDataCadastro(LocalDateTime.now());
        espaco.setStatus(StatusEspaco.DISPONIVEL);
        return espaco;
    }
}
