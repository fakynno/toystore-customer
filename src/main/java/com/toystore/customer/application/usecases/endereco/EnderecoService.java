package com.toystore.customer.application.usecases.endereco;

import com.toystore.customer.application.dto.EnderecoDTO;

import java.util.List;

public interface EnderecoService {

    EnderecoDTO buscarPorId(Long id);
    List<EnderecoDTO> buscarTodos();
    EnderecoDTO salvar (EnderecoDTO enderecoDTO);
    EnderecoDTO atualizar (Long id, EnderecoDTO enderecoDTO);
    void deletarPorId(Long id);
}
