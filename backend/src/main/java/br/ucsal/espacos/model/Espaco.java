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
public class Espaco {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Informe a sigla")
    private String sigla;

    @NotBlank(message = "Informe o nome")
    private String nome;

    @NotNull(message = "Informe o tipo")
    @Enumerated(EnumType.STRING)
    private TipoEspaco tipo;

    private String descricao;

    @NotNull(message = "Informe a capacidade")
    @Positive(message = "A capacidade deve ser maior que zero")
    private Integer capacidade;

    @NotBlank(message = "Informe a localização")
    private String localizacao;

    @ManyToOne
    private CentroAcademico centroAcademico;

    private boolean laboratorioInformatica;

    private String tipoComputadores;

    @Valid
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "espaco_id")
    private Set<Software> softwares = new HashSet<>();

    private LocalDateTime dataCadastro;

    @Enumerated(EnumType.STRING)
    private StatusEspaco status;

    private String motivoInativacao;
}
