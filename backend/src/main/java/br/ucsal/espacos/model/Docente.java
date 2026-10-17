package br.ucsal.espacos.model;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Docente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Informe a matrícula")
    @Column(unique = true)
    private String matricula;

    @NotBlank(message = "Informe o nome")
    private String nome;

    @NotBlank(message = "Informe o e-mail")
    @Email(message = "E-mail inválido")
    private String email;

    @NotBlank(message = "Informe o telefone")
    private String telefone;

    @NotNull(message = "Informe o centro acadêmico")
    @ManyToOne
    private CentroAcademico centroAcademico;

    private LocalDateTime dataCadastro;

    @Enumerated(EnumType.STRING)
    private Status status;
}
