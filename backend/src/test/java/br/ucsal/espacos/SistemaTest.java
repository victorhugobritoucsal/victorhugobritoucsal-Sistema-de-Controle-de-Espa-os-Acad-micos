package br.ucsal.espacos;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;

import br.ucsal.espacos.model.StatusEspaco;
import br.ucsal.espacos.repository.EspacoRepository;
import br.ucsal.espacos.repository.IesRepository;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class SistemaTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private EspacoRepository espacoRepository;

    @Autowired
    private IesRepository iesRepository;

    private MockHttpServletRequestBuilder comChave(MockHttpServletRequestBuilder requisicao) {
        return requisicao.header("X-Gateway-Key", "chave-de-teste").contentType(MediaType.APPLICATION_JSON);
    }

    private Long idDoEspaco(String sigla) {
        return espacoRepository.findAll().stream()
                .filter(espaco -> espaco.getSigla().equals(sigla))
                .findFirst().get().getId();
    }

    private String reserva(Long espacoId, String inicio, String fim) {
        return """
                {"espaco": {"id": %d}, "data": "%s", "horaInicio": "%s", "horaFim": "%s"}
                """.formatted(espacoId, LocalDate.now().plusDays(1), inicio, fim);
    }

    private Integer criarReserva(Long espacoId, String inicio, String fim) throws Exception {
        String resposta = mvc.perform(comChave(post("/api/usuario/solicitacoes-espaco"))
                        .with(user("D0002").roles("USUARIO"))
                        .content(reserva(espacoId, inicio, fim)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        return JsonPath.read(resposta, "$.id");
    }

    private void aprovar(Integer id) throws Exception {
        mvc.perform(comChave(patch("/api/colaborador/solicitacoes-espaco/" + id + "/aprovar"))
                        .with(user("D0001").roles("COLABORADOR")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("APROVADA"));
    }

    @Test
    void requisicaoSemChaveDoFrontEndEBloqueada() throws Exception {
        mvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"login\": \"admin\", \"senha\": \"admin123\"}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void loginComSenhaCorretaFunciona() throws Exception {
        mvc.perform(comChave(post("/api/auth/login")).content("{\"login\": \"admin\", \"senha\": \"admin123\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.login").value("admin"));
    }

    @Test
    void loginComSenhaErradaRetorna401() throws Exception {
        mvc.perform(comChave(post("/api/auth/login")).content("{\"login\": \"admin\", \"senha\": \"errada\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void usuarioSemPerfilDeAdministradorNaoAcessaAreaDoAdmin() throws Exception {
        mvc.perform(comChave(get("/api/admin/ies")).with(user("D0002").roles("USUARIO")))
                .andExpect(status().isForbidden());
    }

    @Test
    void reservaAprovadaBloqueiaHorarioSobreposto() throws Exception {
        Long sala = idDoEspaco("S101");
        aprovar(criarReserva(sala, "08:00", "10:00"));

        mvc.perform(comChave(post("/api/usuario/solicitacoes-espaco"))
                        .with(user("D0002").roles("USUARIO"))
                        .content(reserva(sala, "09:00", "11:00")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensagem").value("Já existe uma reserva aprovada para este espaço nesse horário"));

        criarReserva(sala, "10:00", "11:00");
    }

    @Test
    void liberarReservaDeixaOEspacoDisponivel() throws Exception {
        Long sala = idDoEspaco("S101");
        Integer id = criarReserva(sala, "14:00", "16:00");
        aprovar(id);
        assertThat(espacoRepository.findById(sala).get().getStatus()).isEqualTo(StatusEspaco.INDISPONIVEL);

        mvc.perform(comChave(patch("/api/usuario/solicitacoes-espaco/" + id + "/liberar"))
                        .with(user("D0002").roles("USUARIO")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("FINALIZADA"));
        assertThat(espacoRepository.findById(sala).get().getStatus()).isEqualTo(StatusEspaco.DISPONIVEL);
    }

    @Test
    void reprovarSemJustificativaNaoEPermitido() throws Exception {
        Integer id = criarReserva(idDoEspaco("S101"), "18:00", "19:00");
        mvc.perform(comChave(patch("/api/colaborador/solicitacoes-espaco/" + id + "/reprovar"))
                        .with(user("D0001").roles("COLABORADOR"))
                        .content("{\"justificativa\": \"\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void iesComCentroAcademicoNaoPodeSerExcluida() throws Exception {
        Long ucsal = iesRepository.findAll().get(0).getId();
        mvc.perform(comChave(delete("/api/admin/ies/" + ucsal)).with(user("admin").roles("ADMINISTRADOR")))
                .andExpect(status().isBadRequest());
    }
}
