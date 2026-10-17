package br.ucsal.espacos.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class SolicitacaoEspaco {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Informe o espaço")
    @ManyToOne
    private Espaco espaco;

    @ManyToOne
    private Usuario solicitante;

    @NotNull(message = "Informe a data")
    private LocalDate data;

    @NotNull(message = "Informe a hora inicial")
    private LocalTime horaInicio;

    @NotNull(message = "Informe a hora final")
    private LocalTime horaFim;

    private LocalDateTime dataSolicitacao;

    @Enumerated(EnumType.STRING)
    private StatusSolicitacao status;

    private String justificativa;
}
