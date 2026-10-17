package br.ucsal.espacos.service;

import java.time.LocalDateTime;
import java.util.List;

import br.ucsal.espacos.exception.NaoEncontradoException;
import br.ucsal.espacos.exception.RegraException;
import br.ucsal.espacos.model.Espaco;
import br.ucsal.espacos.model.StatusEspaco;
import br.ucsal.espacos.model.StatusSolicitacao;
import br.ucsal.espacos.model.TipoEspaco;
import br.ucsal.espacos.repository.EspacoRepository;
import br.ucsal.espacos.repository.SolicitacaoEspacoRepository;
import br.ucsal.espacos.repository.SolicitacaoSoftwareRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EspacoService {

    @Autowired
    private EspacoRepository repository;

    @Autowired
    private SolicitacaoEspacoRepository solicitacaoEspacoRepository;

    @Autowired
    private SolicitacaoSoftwareRepository solicitacaoSoftwareRepository;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private AuditoriaService auditoria;

    public List<Espaco> listarDoMeuCentro() {
        return repository.findByCentroAcademicoId(usuarioService.centroDoUsuarioLogado().getId());
    }

    public List<Espaco> listarAtivos() {
        return repository.findByStatusNot(StatusEspaco.INATIVO);
    }

    public Espaco salvar(Espaco espaco) {
        validar(espaco);
        espaco.setId(null);
        espaco.setCentroAcademico(usuarioService.centroDoUsuarioLogado());
        espaco.setDataCadastro(LocalDateTime.now());
        espaco.setStatus(StatusEspaco.DISPONIVEL);
        Espaco salvo = repository.save(espaco);
        auditoria.registrar("Cadastrou o espaço " + salvo.getSigla());
        return salvo;
    }

    public Espaco atualizar(Long id, Espaco dados) {
        Espaco espaco = buscarDoMeuCentro(id);
        validar(dados);
        dados.setId(id);
        dados.setCentroAcademico(espaco.getCentroAcademico());
        dados.setDataCadastro(espaco.getDataCadastro());
        dados.setStatus(espaco.getStatus());
        dados.setMotivoInativacao(espaco.getMotivoInativacao());
        Espaco salvo = repository.save(dados);
        auditoria.registrar("Editou o espaço " + salvo.getSigla());
        return salvo;
    }

    public Espaco inativar(Long id, String motivo) {
        if (motivo == null || motivo.isBlank()) {
            throw new RegraException("Informe o motivo da inativação");
        }
        Espaco espaco = buscarDoMeuCentro(id);
        espaco.setStatus(StatusEspaco.INATIVO);
        espaco.setMotivoInativacao(motivo);
        auditoria.registrar("Inativou o espaço " + espaco.getSigla() + ": " + motivo);
        return repository.save(espaco);
    }

    public Espaco reativar(Long id) {
        Espaco espaco = buscarDoMeuCentro(id);
        boolean temReserva = solicitacaoEspacoRepository.existsByEspacoIdAndStatus(id, StatusSolicitacao.APROVADA);
        espaco.setStatus(temReserva ? StatusEspaco.INDISPONIVEL : StatusEspaco.DISPONIVEL);
        espaco.setMotivoInativacao(null);
        auditoria.registrar("Reativou o espaço " + espaco.getSigla());
        return repository.save(espaco);
    }

    public void excluir(Long id) {
        Espaco espaco = buscarDoMeuCentro(id);
        if (solicitacaoEspacoRepository.existsByEspacoId(id) || solicitacaoSoftwareRepository.existsByEspacoId(id)) {
            throw new RegraException("O espaço possui solicitações e não pode ser excluído. Inative-o.");
        }
        repository.delete(espaco);
        auditoria.registrar("Excluiu o espaço " + espaco.getSigla());
    }

    public Espaco buscarDoMeuCentro(Long id) {
        Espaco espaco = repository.findById(id).orElseThrow(() -> new NaoEncontradoException("Espaço não encontrado"));
        if (!espaco.getCentroAcademico().getId().equals(usuarioService.centroDoUsuarioLogado().getId())) {
            throw new RegraException("Este espaço pertence a outro centro acadêmico");
        }
        return espaco;
    }

    private void validar(Espaco espaco) {
        if (espaco.isLaboratorioInformatica() && espaco.getTipo() != TipoEspaco.LABORATORIO) {
            throw new RegraException("Só espaços do tipo laboratório podem ser laboratório de informática");
        }
        if (!espaco.isLaboratorioInformatica()) {
            espaco.setTipoComputadores(null);
            espaco.getSoftwares().clear();
        }
    }
}
