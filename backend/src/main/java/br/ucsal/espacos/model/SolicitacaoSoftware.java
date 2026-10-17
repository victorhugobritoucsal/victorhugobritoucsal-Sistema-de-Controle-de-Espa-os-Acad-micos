package br.ucsal.espacos.model;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class SolicitacaoSoftware {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Informe o laboratório")
    @ManyToOne
    private Espaco espaco;

    @ManyToOne
    private Usuario solicitante;

    @Valid
    @NotEmpty(message = "Informe pelo menos um software")
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "solicitacao_id")
    private Set<Software> softwares = new HashSet<>();

    private LocalDateTime dataSolicitacao;

    @Enumerated(EnumType.STRING)
    private StatusSolicitacao status;

    private String justificativa;
}
