package br.ucsal.espacos.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Software {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Informe o nome do software")
    private String nome;

    @NotBlank(message = "Informe a URL de download")
    private String urlDownload;

    @NotNull(message = "Informe o tipo do software")
    @Enumerated(EnumType.STRING)
    private TipoSoftware tipo;

    @NotBlank(message = "Informe o objetivo do software")
    private String objetivo;

    private String disciplinas;
}
