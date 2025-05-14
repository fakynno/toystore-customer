package com.toystore.customer.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record EnderecoDTO(

        @JsonProperty(access = JsonProperty.Access.READ_ONLY)
        @Schema(example = "1")
        Long id,

        @NotBlank(message = "O CEP é obrigatório")
        @Schema(example = "12345-678")
        String cep,

        @NotBlank(message = "O estado é obrigatório")
        @Schema(example = "Bahia")
        String estado,

        @NotBlank(message = "A cidade é obrigatório")
        @Schema(example = "Salvador")
        String cidade,

        @NotBlank(message = "O bairro é obrigatório")
        @Schema(example = "Centro")
        String bairro,

        @NotBlank(message = "A rua é obrigatório")
        @Schema(example = "Rua das Flores")
        String rua,

        @Schema(example = "123")
        String numero

) {
}
