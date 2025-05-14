package com.toystore.customer.application.usecases.cliente;

import com.toystore.customer.application.dto.ClienteAtualizacaoDTO;
import com.toystore.customer.application.dto.ClienteDTO;

import java.util.List;

public interface ClienteService {

    ClienteDTO buscarPorId(String cpf);
    List<ClienteDTO> buscarTodos();
    ClienteDTO salvar (ClienteDTO clienteDTO);
    ClienteDTO atualizar (String cpf, ClienteAtualizacaoDTO clienteDTO);
    void deletarPorId(String cpf);
}
