package com.toystore.customer.infrastructure.utils;

import com.toystore.customer.application.dto.EnderecoDTO;
import com.toystore.customer.domain.model.Endereco;

import java.util.UUID;

public class EnderecoHelper {

    public static Endereco gerarEndereco(){
        return Endereco.builder()
                .id(Math.abs(UUID.randomUUID().getMostSignificantBits()))
                .estado("Bahia")
                .cidade("Salvador")
                .cep("1111111")
                .bairro("Centro")
                .rua("Rua Principal")
                .numero("111")
                .build();
    }
    public  static EnderecoDTO gerarEnderecoDTO(Endereco endereco){
        return new EnderecoDTO(endereco.getId(),
                endereco.getCep(),
                endereco.getEstado(),
                endereco.getCidade(),
                endereco.getBairro(),
                endereco.getRua(),
                endereco.getNumero());
    }

    public  static EnderecoDTO gerarEnderecoDTOSemId(Endereco endereco){
        return new EnderecoDTO(null,
                endereco.getCep(),
                endereco.getEstado(),
                endereco.getCidade(),
                endereco.getBairro(),
                endereco.getRua(),
                endereco.getNumero());
    }
}
