package br.ucsal.espacos.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import br.ucsal.espacos.exception.NaoEncontradoException;
import br.ucsal.espacos.exception.RegraException;
import br.ucsal.espacos.model.Espaco;
import br.ucsal.espacos.model.SolicitacaoEspaco;
import br.ucsal.espacos.model.StatusEspaco;
import br.ucsal.espacos.model.StatusSolicitacao;
import br.ucsal.espacos.repository.EspacoRepository;
import br.ucsal.espacos.repository.SolicitacaoEspacoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SolicitacaoEspacoService {

    @Autowired
    private SolicitacaoEspacoRepository repository;

    @Autowired
    private EspacoRepository espacoRepository;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private AuditoriaService auditoria;

    public List<SolicitacaoEspaco> minhas() {
        return repository.findBySolicitanteIdOrderByDataSolicitacaoDesc(usuarioService.usuarioLogado().getId());
    }

    public List<SolicitacaoEspaco> doMeuCentro() {
        return repository.findByEspacoCentroAcademicoIdOrderByDataSolicitacaoDesc(
                usuarioService.centroDoUsuarioLogado().getId());
    }

    public SolicitacaoEspaco salvar(SolicitacaoEspaco solicitacao) {
        usuarioService.centroDoUsuarioLogado();
        validar(solicitacao);
        solicitacao.setId(null);
        solicitacao.setSolicitante(usuarioService.usuarioLogado());
        solicitacao.setDataSolicitacao(LocalDateTime.now());
        solicitacao.setStatus(StatusSolicitacao.PENDENTE);
        SolicitacaoEspaco salva = repository.save(solicitacao);
        auditoria.registrar("Solicitou o espaço " + salva.getEspaco().getSigla() + " para " + salva.getData());
        return salva;
    }

    public SolicitacaoEspaco atualizar(Long id, SolicitacaoEspaco dados) {
        SolicitacaoEspaco solicitacao = buscarMinha(id);
        exigirPendente(solicitacao);
        dados.setId(id);
        validar(dados);
        solicitacao.setEspaco(dados.getEspaco());
        solicitacao.setData(dados.getData());
        solicitacao.setHoraInicio(dados.getHoraInicio());
        solicitacao.setHoraFim(dados.getHoraFim());
        auditoria.registrar("Editou a solicitação de espaço " + id);
        return repository.save(solicitacao);
    }

    public SolicitacaoEspaco inativar(Long id) {
        SolicitacaoEspaco solicitacao = buscarMinha(id);
        exigirPendente(solicitacao);
        solicitacao.setStatus(StatusSolicitacao.INATIVA);
        auditoria.registrar("Inativou a solicitação de espaço " + id);
        return repository.save(solicitacao);
    }

    public SolicitacaoEspaco liberar(Long id) {
        SolicitacaoEspaco solicitacao = buscarMinha(id);
        if (solicitacao.getStatus() != StatusSolicitacao.APROVADA) {
            throw new RegraException("Só reservas aprovadas podem ser liberadas");
        }
        solicitacao.setStatus(StatusSolicitacao.FINALIZADA);
        repository.save(solicitacao);

        Espaco espaco = solicitacao.getEspaco();
        if (!repository.existsByEspacoIdAndStatus(espaco.getId(), StatusSolicitacao.APROVADA)) {
            espaco.setStatus(StatusEspaco.DISPONIVEL);
            espacoRepository.save(espaco);
        }
        auditoria.registrar("Liberou o espaço " + espaco.getSigla());
        return solicitacao;
    }

    public SolicitacaoEspaco aprovar(Long id) {
        SolicitacaoEspaco solicitacao = buscarDoMeuCentro(id);
        exigirPendente(solicitacao);
        Espaco espaco = solicitacao.getEspaco();
        if (espaco.getStatus() == StatusEspaco.INATIVO) {
            throw new RegraException("O espaço está inativo");
        }
        if (solicitacao.getData().isBefore(LocalDate.now())) {
            throw new RegraException("A data desta solicitação já passou");
        }
        verificarConflito(espaco.getId(), solicitacao.getData(), solicitacao.getHoraInicio(),
                solicitacao.getHoraFim(), id);

        solicitacao.setStatus(StatusSolicitacao.APROVADA);
        repository.save(solicitacao);
        espaco.setStatus(StatusEspaco.INDISPONIVEL);
        espacoRepository.save(espaco);
        auditoria.registrar("Aprovou a solicitação de espaço " + id);
        return solicitacao;
    }

    public SolicitacaoEspaco reprovar(Long id, String justificativa) {
        if (justificativa == null || justificativa.isBlank()) {
            throw new RegraException("Informe a justificativa da reprovação");
        }
        SolicitacaoEspaco solicitacao = buscarDoMeuCentro(id);
        exigirPendente(solicitacao);
        solicitacao.setStatus(StatusSolicitacao.REPROVADA);
        solicitacao.setJustificativa(justificativa);
        auditoria.registrar("Reprovou a solicitação de espaço " + id + ": " + justificativa);
        return repository.save(solicitacao);
    }

    private void validar(SolicitacaoEspaco solicitacao) {
        Espaco espaco = espacoRepository.findById(solicitacao.getEspaco().getId())
                .orElseThrow(() -> new NaoEncontradoException("Espaço não encontrado"));
        if (espaco.getStatus() == StatusEspaco.INATIVO) {
            throw new RegraException("Este espaço está inativo");
        }
        if (!solicitacao.getHoraFim().isAfter(solicitacao.getHoraInicio())) {
            throw new RegraException("A hora final deve ser depois da hora inicial");
        }
        if (solicitacao.getData().isBefore(LocalDate.now())) {
            throw new RegraException("Não é possível solicitar uma data que já passou");
        }
        verificarConflito(espaco.getId(), solicitacao.getData(), solicitacao.getHoraInicio(),
                solicitacao.getHoraFim(), solicitacao.getId());
        solicitacao.setEspaco(espaco);
    }

    private void verificarConflito(Long espacoId, LocalDate data, LocalTime inicio, LocalTime fim, Long idIgnorado) {
        List<SolicitacaoEspaco> aprovadas = repository.findByEspacoIdAndDataAndStatus(espacoId, data,
                StatusSolicitacao.APROVADA);
        for (SolicitacaoEspaco outra : aprovadas) {
            boolean sobrepoe = inicio.isBefore(outra.getHoraFim()) && fim.isAfter(outra.getHoraInicio());
            if (sobrepoe && !outra.getId().equals(idIgnorado)) {
                throw new RegraException("Já existe uma reserva aprovada para este espaço nesse horário");
            }
        }
    }

    private SolicitacaoEspaco buscar(Long id) {
        return repository.findById(id).orElseThrow(() -> new NaoEncontradoException("Solicitação não encontrada"));
    }

    private SolicitacaoEspaco buscarMinha(Long id) {
        SolicitacaoEspaco solicitacao = buscar(id);
        if (!solicitacao.getSolicitante().getId().equals(usuarioService.usuarioLogado().getId())) {
            throw new RegraException("Esta solicitação é de outro usuário");
        }
        return solicitacao;
    }

    private SolicitacaoEspaco buscarDoMeuCentro(Long id) {
        SolicitacaoEspaco solicitacao = buscar(id);
        Long centroDoEspaco = solicitacao.getEspaco().getCentroAcademico().getId();
        if (!centroDoEspaco.equals(usuarioService.centroDoUsuarioLogado().getId())) {
            throw new RegraException("Esta solicitação é de um espaço de outro centro acadêmico");
        }
        return solicitacao;
    }

    private void exigirPendente(SolicitacaoEspaco solicitacao) {
        if (solicitacao.getStatus() != StatusSolicitacao.PENDENTE) {
            throw new RegraException("Só solicitações pendentes podem ser alteradas");
        }
    }
}
