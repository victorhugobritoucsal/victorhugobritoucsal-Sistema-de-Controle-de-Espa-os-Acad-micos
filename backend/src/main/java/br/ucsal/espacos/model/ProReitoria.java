package br.ucsal.espacos.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class ProReitoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Informe o nome da pró-reitoria")
    private String nome;

    @NotBlank(message = "Informe o pró-reitor")
    private String titular;

    private String nomeacao;
}
