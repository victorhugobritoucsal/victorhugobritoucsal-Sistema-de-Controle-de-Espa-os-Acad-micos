package br.ucsal.espacos.service;

import java.time.LocalDateTime;
import java.util.List;

import br.ucsal.espacos.exception.NaoEncontradoException;
import br.ucsal.espacos.exception.RegraException;
import br.ucsal.espacos.model.CentroAcademico;
import br.ucsal.espacos.model.Docente;
import br.ucsal.espacos.model.Status;
import br.ucsal.espacos.repository.CentroAcademicoRepository;
import br.ucsal.espacos.repository.CursoRepository;
import br.ucsal.espacos.repository.DocenteRepository;
import br.ucsal.espacos.repository.EspacoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CentroAcademicoService {

    @Autowired
    private CentroAcademicoRepository repository;

    @Autowired
    private DocenteRepository docenteRepository;

    @Autowired
    private CursoRepository cursoRepository;

    @Autowired
    private EspacoRepository espacoRepository;

    @Autowired
    private IesService iesService;

    @Autowired
    private AuditoriaService auditoria;

    public List<CentroAcademico> listar() {
        return repository.findAll();
    }

    public CentroAcademico buscar(Long id) {
        return repository.findById(id).orElseThrow(() -> new NaoEncontradoException("Centro acadêmico não encontrado"));
    }

    public CentroAcademico salvar(CentroAcademico centro) {
        validarIes(centro);
        centro.setId(null);
        centro.setCoordenador(null);
        centro.setDataCadastro(LocalDateTime.now());
        centro.setStatus(Status.ATIVO);
        CentroAcademico salvo = repository.save(centro);
        auditoria.registrar("Cadastrou o centro acadêmico " + salvo.getSigla());
        return salvo;
    }

    public CentroAcademico atualizar(Long id, CentroAcademico dados) {
        CentroAcademico centro = buscar(id);
        validarIes(dados);
        if (dados.getCoordenador() != null) {
            Docente coordenador = docenteRepository.findById(dados.getCoordenador().getId())
                    .orElseThrow(() -> new NaoEncontradoException("Docente não encontrado"));
            if (!coordenador.getCentroAcademico().getId().equals(id)) {
                throw new RegraException("O coordenador precisa ser um docente deste centro acadêmico");
            }
            if (repository.existsByCoordenadorIdAndIdNot(coordenador.getId(), id)) {
                throw new RegraException("Este docente já coordena outro centro acadêmico");
            }
        }
        dados.setId(id);
        dados.setDataCadastro(centro.getDataCadastro());
        dados.setStatus(centro.getStatus());
        CentroAcademico salvo = repository.save(dados);
        auditoria.registrar("Editou o centro acadêmico " + salvo.getSigla());
        return salvo;
    }

    public CentroAcademico alterarStatus(Long id, Status status) {
        CentroAcademico centro = buscar(id);
        centro.setStatus(status);
        auditoria.registrar("Alterou o status do centro acadêmico " + centro.getSigla() + " para " + status);
        return repository.save(centro);
    }

    public void excluir(Long id) {
        CentroAcademico centro = buscar(id);
        if (cursoRepository.existsByCentroAcademicoId(id) || docenteRepository.existsByCentroAcademicoId(id)
                || espacoRepository.existsByCentroAcademicoId(id)) {
            throw new RegraException("O centro acadêmico possui cursos, docentes ou espaços e não pode ser excluído. Inative-o.");
        }
        repository.delete(centro);
        auditoria.registrar("Excluiu o centro acadêmico " + centro.getSigla());
    }

    private void validarIes(CentroAcademico centro) {
        if (iesService.buscar(centro.getIes().getId()).getStatus() == Status.INATIVO) {
            throw new RegraException("A IES está inativa");
        }
    }
}
