package br.ucsal.espacos.service;

import java.time.LocalDateTime;
import java.util.List;

import br.ucsal.espacos.exception.NaoEncontradoException;
import br.ucsal.espacos.model.Curso;
import br.ucsal.espacos.model.Status;
import br.ucsal.espacos.repository.CursoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CursoService {

    @Autowired
    private CursoRepository repository;

    @Autowired
    private AuditoriaService auditoria;

    public List<Curso> listar() {
        return repository.findAll();
    }

    public Curso buscar(Long id) {
        return repository.findById(id).orElseThrow(() -> new NaoEncontradoException("Curso não encontrado"));
    }

    public Curso salvar(Curso curso) {
        curso.setId(null);
        curso.setDataCadastro(LocalDateTime.now());
        curso.setStatus(Status.ATIVO);
        Curso salvo = repository.save(curso);
        auditoria.registrar("Cadastrou o curso " + salvo.getSigla());
        return salvo;
    }

    public Curso atualizar(Long id, Curso dados) {
        Curso curso = buscar(id);
        dados.setId(id);
        dados.setDataCadastro(curso.getDataCadastro());
        dados.setStatus(curso.getStatus());
        Curso salvo = repository.save(dados);
        auditoria.registrar("Editou o curso " + salvo.getSigla());
        return salvo;
    }

    public Curso alterarStatus(Long id, Status status) {
        Curso curso = buscar(id);
        curso.setStatus(status);
        auditoria.registrar("Alterou o status do curso " + curso.getSigla() + " para " + status);
        return repository.save(curso);
    }

    public void excluir(Long id) {
        Curso curso = buscar(id);
        repository.delete(curso);
        auditoria.registrar("Excluiu o curso " + curso.getSigla());
    }
}
