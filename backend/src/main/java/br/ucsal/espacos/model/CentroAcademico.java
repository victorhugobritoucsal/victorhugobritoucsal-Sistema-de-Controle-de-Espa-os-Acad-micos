package br.ucsal.espacos.model;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class CentroAcademico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Informe a sigla")
    private String sigla;

    @NotBlank(message = "Informe o nome")
    private String nome;

    @NotBlank(message = "Informe o e-mail")
    @Email(message = "E-mail inválido")
    private String email;

    @NotBlank(message = "Informe o telefone")
    private String telefone;

    @ManyToOne
    @JsonIgnoreProperties("centroAcademico")
    private Docente coordenador;

    @NotNull(message = "Informe a IES")
    @ManyToOne
    private Ies ies;

    private LocalDateTime dataCadastro;

    @Enumerated(EnumType.STRING)
    private Status status;
}
