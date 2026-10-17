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
public class Ies {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Informe a sigla")
    @Column(unique = true)
    private String sigla;

    @NotBlank(message = "Informe o nome")
    private String nome;

    @NotBlank(message = "Informe o e-mail")
    @Email(message = "E-mail inválido")
    private String email;

    @NotBlank(message = "Informe o telefone")
    private String telefone;

    @NotBlank(message = "Informe o reitor")
    private String reitor;

    @Valid
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "ies_id")
    private Set<ProReitoria> proReitorias = new HashSet<>();

    private LocalDateTime dataCadastro;

    @Enumerated(EnumType.STRING)
    private Status status;
}
