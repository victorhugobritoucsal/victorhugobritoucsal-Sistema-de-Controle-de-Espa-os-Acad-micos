package br.ucsal.espacos.model;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Curso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Informe a sigla")
    private String sigla;

    @NotBlank(message = "Informe a descrição")
    private String descricao;

    @NotNull(message = "Informe o centro acadêmico")
    @ManyToOne
    private CentroAcademico centroAcademico;

    @NotEmpty(message = "Informe pelo menos um turno")
    @ElementCollection(fetch = FetchType.EAGER)
    private Set<String> turnos = new HashSet<>();

    @ManyToOne
    private Docente coordenador;

    private LocalDateTime dataCadastro;

    @Enumerated(EnumType.STRING)
    private Status status;
}
