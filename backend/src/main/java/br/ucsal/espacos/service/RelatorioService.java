package br.ucsal.espacos.service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import br.ucsal.espacos.model.CentroAcademico;
import br.ucsal.espacos.model.Usuario;
import br.ucsal.espacos.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RelatorioService {

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
    private SolicitacaoEspacoService solicitacaoEspacoService;

    @Autowired
    private SolicitacaoSoftwareService solicitacaoSoftwareService;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private AuditoriaService auditoria;

    public Map<String, Object> institucional() {
        Map<String, Object> relatorio = cabecalho("Relatório institucional");
        relatorio.put("ies", iesRepository.findAll());
        relatorio.put("centros", centroRepository.findAll());
        relatorio.put("cursos", cursoRepository.findAll());
        return relatorio;
    }

    public Map<String, Object> docentes() {
        Map<String, Object> relatorio = cabecalho("Relatório de docentes");
        relatorio.put("docentes", docenteRepository.findAll());
        return relatorio;
    }

    public Map<String, Object> colaborador() {
        CentroAcademico centro = usuarioService.centroDoUsuarioLogado();
        Map<String, Object> relatorio = cabecalho("Relatório do centro acadêmico " + centro.getSigla());
        relatorio.put("espacos", espacoRepository.findByCentroAcademicoId(centro.getId()));
        relatorio.put("solicitacoesEspaco", solicitacaoEspacoService.doMeuCentro());
        relatorio.put("solicitacoesSoftware", solicitacaoSoftwareService.doMeuCentro());
        return relatorio;
    }

    public Map<String, Object> usuario() {
        Map<String, Object> relatorio = cabecalho("Relatório dos meus pedidos");
        relatorio.put("solicitacoesEspaco", solicitacaoEspacoService.minhas());
        relatorio.put("solicitacoesSoftware", solicitacaoSoftwareService.minhas());
        return relatorio;
    }

    private Map<String, Object> cabecalho(String titulo) {
        Usuario usuario = usuarioService.usuarioLogado();
        Map<String, Object> relatorio = new HashMap<>();
        relatorio.put("titulo", titulo);
        relatorio.put("emitidoPor", usuario.getNome() + " (" + usuario.getLogin() + ")");
        relatorio.put("emitidoEm", LocalDateTime.now());
        auditoria.registrar("Emitiu o " + titulo.toLowerCase());
        return relatorio;
    }
}
