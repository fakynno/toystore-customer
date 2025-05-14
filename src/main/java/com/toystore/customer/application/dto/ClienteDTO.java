package com.toystore.customer.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ClienteDTO(

        @NotBlank(message = "O CPF é obrigatório")
        @Schema(example = "12345678900")
        String cpf,

        @NotBlank(message = "O nome é obrigatório")
        @Schema(example = "Pablo Marçal")
        String nome,

        @NotBlank(message = "O email é obrigatório")
        @Schema(example = "pablomarcal@dinheiro.com")
        String email,

        @NotNull(message = "O id do endereço é obrigatório")
        @Schema(example = "1")
        Long enderecoId
) {
}
