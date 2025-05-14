package com.toystore.customer.application.usecases.endereco;

import com.toystore.customer.application.dto.EnderecoDTO;
import com.toystore.customer.domain.exceptions.RecursoNaoEncontradoException;
import com.toystore.customer.domain.model.Endereco;
import com.toystore.customer.domain.repository.EnderecoRepository;
import com.toystore.customer.interfaces.mapper.EnderecoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class EnderecoServiceImpl implements EnderecoService {

    private final EnderecoRepository enderecoRepository;
    private final EnderecoMapper enderecoMapper;

    @Override
    public EnderecoDTO buscarPorId(Long id) {
        Endereco endereco = enderecoRepository.findById(id).orElseThrow(() -> new RecursoNaoEncontradoException(
                "Endereço não encontrado com id: " + id
        ));
        return enderecoMapper.toDto(endereco);
    }

    @Override
    public List<EnderecoDTO> buscarTodos() {
        return enderecoRepository.findAll()
                .stream()
                .map(enderecoMapper::toDto)
                .toList();
    }

    @Override
    public EnderecoDTO salvar(EnderecoDTO enderecoDTO) {
        Endereco endereco = enderecoMapper.toEntity(enderecoDTO);
        endereco = enderecoRepository.save(endereco);
        return enderecoMapper.toDto(endereco);
    }

    @Override
    public EnderecoDTO atualizar(Long id, EnderecoDTO enderecoDTO) {
        Endereco endereco = enderecoMapper.toEntity(this.buscarPorId(id));
        enderecoMapper.updateFromDto(enderecoDTO, endereco);
        endereco = enderecoRepository.save(endereco);
        return enderecoMapper.toDto(endereco);
    }

    @Override
    public void deletarPorId(Long id) {
        this.buscarPorId(id);
        enderecoRepository.deleteById(id);
    }
}
