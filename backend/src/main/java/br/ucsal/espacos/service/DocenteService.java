package br.ucsal.espacos.service;

import java.time.LocalDateTime;
import java.util.List;

import br.ucsal.espacos.exception.NaoEncontradoException;
import br.ucsal.espacos.exception.RegraException;
import br.ucsal.espacos.model.Docente;
import br.ucsal.espacos.model.HistoricoDocente;
import br.ucsal.espacos.model.Status;
import br.ucsal.espacos.repository.DocenteRepository;
import br.ucsal.espacos.repository.HistoricoDocenteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DocenteService {

    @Autowired
    private DocenteRepository repository;

    @Autowired
    private HistoricoDocenteRepository historicoRepository;

    @Autowired
    private AuditoriaService auditoria;

    public List<Docente> listar() {
        return repository.findAll();
    }

    public Docente buscar(Long id) {
        return repository.findById(id).orElseThrow(() -> new NaoEncontradoException("Docente não encontrado"));
    }

    public List<HistoricoDocente> historico(Long id) {
        return historicoRepository.findByDocenteIdOrderByDataDesc(id);
    }

    public Docente salvar(Docente docente) {
        docente.setId(null);
        docente.setDataCadastro(LocalDateTime.now());
        docente.setStatus(Status.ATIVO);
        Docente salvo = repository.save(docente);
        registrarHistorico(salvo, "Cadastro", null);
        auditoria.registrar("Cadastrou o docente " + salvo.getMatricula());
        return salvo;
    }

    public Docente atualizar(Long id, Docente dados) {
        Docente docente = buscar(id);
        dados.setId(id);
        dados.setDataCadastro(docente.getDataCadastro());
        dados.setStatus(docente.getStatus());
        Docente salvo = repository.save(dados);
        registrarHistorico(salvo, "Alteração", null);
        auditoria.registrar("Editou o docente " + salvo.getMatricula());
        return salvo;
    }

    public Docente inativar(Long id, String motivo) {
        if (motivo == null || motivo.isBlank()) {
            throw new RegraException("Informe o motivo da inativação");
        }
        Docente docente = buscar(id);
        docente.setStatus(Status.INATIVO);
        registrarHistorico(docente, "Inativação", motivo);
        auditoria.registrar("Inativou o docente " + docente.getMatricula() + ": " + motivo);
        return repository.save(docente);
    }

    public Docente reativar(Long id) {
        Docente docente = buscar(id);
        docente.setStatus(Status.ATIVO);
        registrarHistorico(docente, "Reativação", null);
        auditoria.registrar("Reativou o docente " + docente.getMatricula());
        return repository.save(docente);
    }

    private void registrarHistorico(Docente docente, String acao, String motivo) {
        HistoricoDocente historico = new HistoricoDocente();
        historico.setDocente(docente);
        historico.setAcao(acao);
        historico.setMotivo(motivo);
        historico.setData(LocalDateTime.now());
        historicoRepository.save(historico);
    }
}
