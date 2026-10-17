package br.ucsal.espacos.service;

import java.time.LocalDateTime;
import java.util.List;

import br.ucsal.espacos.exception.NaoEncontradoException;
import br.ucsal.espacos.exception.RegraException;
import br.ucsal.espacos.model.Ies;
import br.ucsal.espacos.model.Status;
import br.ucsal.espacos.repository.CentroAcademicoRepository;
import br.ucsal.espacos.repository.IesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class IesService {

    @Autowired
    private IesRepository repository;

    @Autowired
    private CentroAcademicoRepository centroRepository;

    @Autowired
    private AuditoriaService auditoria;

    public List<Ies> listar() {
        return repository.findAll();
    }

    public Ies buscar(Long id) {
        return repository.findById(id).orElseThrow(() -> new NaoEncontradoException("IES não encontrada"));
    }

    public Ies salvar(Ies ies) {
        ies.setId(null);
        ies.setDataCadastro(LocalDateTime.now());
        ies.setStatus(Status.ATIVO);
        Ies salva = repository.save(ies);
        auditoria.registrar("Cadastrou a IES " + salva.getSigla());
        return salva;
    }

    public Ies atualizar(Long id, Ies dados) {
        Ies ies = buscar(id);
        dados.setId(id);
        dados.setDataCadastro(ies.getDataCadastro());
        dados.setStatus(ies.getStatus());
        Ies salva = repository.save(dados);
        auditoria.registrar("Editou a IES " + salva.getSigla());
        return salva;
    }

    public Ies alterarStatus(Long id, Status status) {
        Ies ies = buscar(id);
        ies.setStatus(status);
        auditoria.registrar("Alterou o status da IES " + ies.getSigla() + " para " + status);
        return repository.save(ies);
    }

    public void excluir(Long id) {
        Ies ies = buscar(id);
        if (centroRepository.existsByIesId(id)) {
            throw new RegraException("A IES possui centros acadêmicos e não pode ser excluída. Inative-a.");
        }
        repository.delete(ies);
        auditoria.registrar("Excluiu a IES " + ies.getSigla());
    }
}
