package com.toystore.customer.application.usecases.cliente;

import com.toystore.customer.application.dto.ClienteAtualizacaoDTO;
import com.toystore.customer.application.dto.ClienteDTO;
import com.toystore.customer.application.usecases.endereco.EnderecoService;
import com.toystore.customer.domain.exceptions.CPFJaCadastradoException;
import com.toystore.customer.domain.exceptions.RecursoNaoEncontradoException;
import com.toystore.customer.domain.model.Cliente;
import com.toystore.customer.domain.repository.ClienteRepository;
import com.toystore.customer.interfaces.mapper.ClienteMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ClienteServiceImpl implements ClienteService{

    private final ClienteRepository clienteRepository;
    private final ClienteMapper clienteMapper;
    private final EnderecoService enderecoService;

    @Override
    public ClienteDTO buscarPorId(String cpf) {
        Cliente cliente = clienteRepository.findById(cpf).orElseThrow(() -> new RecursoNaoEncontradoException(
                "Cliente não encontrado com o cpf: " + cpf
        ));
        return clienteMapper.toDto(cliente);
    }

    @Override
    public List<ClienteDTO> buscarTodos() {
        return clienteRepository.findAll()
                .stream()
                .map(clienteMapper::toDto)
                .toList();
    }

    @Override
    public ClienteDTO salvar(ClienteDTO clienteDTO) {
        verificarCPFUnico(clienteDTO.cpf());
        enderecoService.buscarPorId(clienteDTO.enderecoId());
        Cliente cliente = clienteMapper.toEntity(clienteDTO);
        cliente = clienteRepository.save(cliente);
        return clienteMapper.toDto(cliente);
    }

    @Override
    public ClienteDTO atualizar(String cpf, ClienteAtualizacaoDTO clienteAtualizacaoDTO) {
        Cliente cliente = clienteMapper.toEntity(this.buscarPorId(cpf));
        enderecoService.buscarPorId(clienteAtualizacaoDTO.enderecoId());
        clienteMapper.updateFromDto(clienteAtualizacaoDTO, cliente);
        cliente = clienteRepository.save(cliente);
        return clienteMapper.toDto(cliente);
    }

    @Override
    public void deletarPorId(String cpf) {
        this.buscarPorId(cpf);
        clienteRepository.deleteById(cpf);
    }

    private void verificarCPFUnico(String cpf){
        clienteRepository.findById(cpf).ifPresent(cliente -> {
            throw new CPFJaCadastradoException(
                    "CPF " + cpf + " já cadastrado no sistema");
        });
    }
}
