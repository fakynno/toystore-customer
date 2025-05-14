package com.toystore.customer.infrastructure.utils;

import com.toystore.customer.application.dto.ClienteAtualizacaoDTO;
import com.toystore.customer.application.dto.ClienteDTO;
import com.toystore.customer.domain.model.Cliente;

import java.util.concurrent.ThreadLocalRandom;

import static com.toystore.customer.infrastructure.utils.EnderecoHelper.gerarEndereco;

public class ClienteHelper {

    public static Cliente gerarCliente(){
        return Cliente.builder()
                .cpf(String.valueOf(ThreadLocalRandom.current().nextLong(10000000000L, 100000000000L)))
                .email("ninguemdasilva@mail.com")
                .nome("Ninguem da Silva")
                .endereco(gerarEndereco())
                .build();
    }

    public static ClienteDTO gerarClienteDTO(Cliente cliente){
        return new ClienteDTO(cliente.getCpf(),
                cliente.getNome(),
                cliente.getEmail(),
                cliente.getEndereco().getId());
    }

    public static ClienteDTO gerarClienteDTOSemCpf(ClienteDTO clienteDTO){
        return new ClienteDTO(null,
                clienteDTO.nome(),
                clienteDTO.email(),
                clienteDTO.enderecoId());
    }

    public static ClienteAtualizacaoDTO gerarAtualizacaoClienteDTO(ClienteDTO clienteDTO){
        return new ClienteAtualizacaoDTO(clienteDTO.nome(),
                clienteDTO.email(),
                clienteDTO.enderecoId());
    }
}
