package br.ucsal.espacos.service;

import java.time.LocalDateTime;
import java.util.List;

import br.ucsal.espacos.exception.NaoEncontradoException;
import br.ucsal.espacos.exception.RegraException;
import br.ucsal.espacos.model.Espaco;
import br.ucsal.espacos.model.Software;
import br.ucsal.espacos.model.SolicitacaoSoftware;
import br.ucsal.espacos.model.StatusEspaco;
import br.ucsal.espacos.model.StatusSolicitacao;
import br.ucsal.espacos.repository.EspacoRepository;
import br.ucsal.espacos.repository.SolicitacaoSoftwareRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SolicitacaoSoftwareService {

    @Autowired
    private SolicitacaoSoftwareRepository repository;

    @Autowired
    private EspacoRepository espacoRepository;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private AuditoriaService auditoria;

    public List<SolicitacaoSoftware> minhas() {
        return repository.findBySolicitanteIdOrderByDataSolicitacaoDesc(usuarioService.usuarioLogado().getId());
    }

    public List<SolicitacaoSoftware> doMeuCentro() {
        return repository.findByEspacoCentroAcademicoIdOrderByDataSolicitacaoDesc(
                usuarioService.centroDoUsuarioLogado().getId());
    }

    public SolicitacaoSoftware salvar(SolicitacaoSoftware solicitacao) {
        usuarioService.centroDoUsuarioLogado();
        solicitacao.setEspaco(buscarLaboratorio(solicitacao.getEspaco().getId()));
        solicitacao.setId(null);
        solicitacao.getSoftwares().forEach(software -> software.setId(null));
        solicitacao.setSolicitante(usuarioService.usuarioLogado());
        solicitacao.setDataSolicitacao(LocalDateTime.now());
        solicitacao.setStatus(StatusSolicitacao.PENDENTE);
        SolicitacaoSoftware salva = repository.save(solicitacao);
        auditoria.registrar("Solicitou instalação de software no " + salva.getEspaco().getSigla());
        return salva;
    }

    public SolicitacaoSoftware atualizar(Long id, SolicitacaoSoftware dados) {
        SolicitacaoSoftware solicitacao = buscarMinha(id);
        exigirPendente(solicitacao);
        solicitacao.setEspaco(buscarLaboratorio(dados.getEspaco().getId()));
        solicitacao.getSoftwares().clear();
        for (Software software : dados.getSoftwares()) {
            software.setId(null);
            solicitacao.getSoftwares().add(software);
        }
        auditoria.registrar("Editou a solicitação de software " + id);
        return repository.save(solicitacao);
    }

    public SolicitacaoSoftware inativar(Long id) {
        SolicitacaoSoftware solicitacao = buscarMinha(id);
        exigirPendente(solicitacao);
        solicitacao.setStatus(StatusSolicitacao.INATIVA);
        auditoria.registrar("Inativou a solicitação de software " + id);
        return repository.save(solicitacao);
    }

    public SolicitacaoSoftware aprovar(Long id) {
        SolicitacaoSoftware solicitacao = buscarDoMeuCentro(id);
        exigirPendente(solicitacao);
        Espaco laboratorio = solicitacao.getEspaco();
        for (Software pedido : solicitacao.getSoftwares()) {
            Software instalado = new Software();
            instalado.setNome(pedido.getNome());
            instalado.setUrlDownload(pedido.getUrlDownload());
            instalado.setTipo(pedido.getTipo());
            instalado.setObjetivo(pedido.getObjetivo());
            laboratorio.getSoftwares().add(instalado);
        }
        espacoRepository.save(laboratorio);
        solicitacao.setStatus(StatusSolicitacao.APROVADA);
        auditoria.registrar("Aprovou a solicitação de software " + id);
        return repository.save(solicitacao);
    }

    public SolicitacaoSoftware reprovar(Long id, String justificativa) {
        if (justificativa == null || justificativa.isBlank()) {
            throw new RegraException("Informe a justificativa da reprovação");
        }
        SolicitacaoSoftware solicitacao = buscarDoMeuCentro(id);
        exigirPendente(solicitacao);
        solicitacao.setStatus(StatusSolicitacao.REPROVADA);
        solicitacao.setJustificativa(justificativa);
        auditoria.registrar("Reprovou a solicitação de software " + id + ": " + justificativa);
        return repository.save(solicitacao);
    }

    private Espaco buscarLaboratorio(Long espacoId) {
        Espaco espaco = espacoRepository.findById(espacoId)
                .orElseThrow(() -> new NaoEncontradoException("Espaço não encontrado"));
        if (!espaco.isLaboratorioInformatica() || espaco.getStatus() == StatusEspaco.INATIVO) {
            throw new RegraException("Escolha um laboratório de informática ativo");
        }
        return espaco;
    }

    private SolicitacaoSoftware buscar(Long id) {
        return repository.findById(id).orElseThrow(() -> new NaoEncontradoException("Solicitação não encontrada"));
    }

    private SolicitacaoSoftware buscarMinha(Long id) {
        SolicitacaoSoftware solicitacao = buscar(id);
        if (!solicitacao.getSolicitante().getId().equals(usuarioService.usuarioLogado().getId())) {
            throw new RegraException("Esta solicitação é de outro usuário");
        }
        return solicitacao;
    }

    private SolicitacaoSoftware buscarDoMeuCentro(Long id) {
        SolicitacaoSoftware solicitacao = buscar(id);
        Long centroDoEspaco = solicitacao.getEspaco().getCentroAcademico().getId();
        if (!centroDoEspaco.equals(usuarioService.centroDoUsuarioLogado().getId())) {
            throw new RegraException("Esta solicitação é de um espaço de outro centro acadêmico");
        }
        return solicitacao;
    }

    private void exigirPendente(SolicitacaoSoftware solicitacao) {
        if (solicitacao.getStatus() != StatusSolicitacao.PENDENTE) {
            throw new RegraException("Só solicitações pendentes podem ser alteradas");
        }
    }
}
