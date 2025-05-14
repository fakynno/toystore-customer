package com.toystore.customer.application.usecases.cliente;

import com.toystore.customer.application.dto.ClienteAtualizacaoDTO;
import com.toystore.customer.application.dto.ClienteDTO;
import com.toystore.customer.application.dto.EnderecoDTO;
import com.toystore.customer.application.usecases.endereco.EnderecoService;
import com.toystore.customer.domain.exceptions.CPFJaCadastradoException;
import com.toystore.customer.domain.exceptions.RecursoNaoEncontradoException;
import com.toystore.customer.domain.model.Cliente;
import com.toystore.customer.domain.repository.ClienteRepository;
import com.toystore.customer.infrastructure.utils.ClienteHelper;
import com.toystore.customer.interfaces.mapper.ClienteMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static com.toystore.customer.infrastructure.utils.ClienteHelper.*;
import static com.toystore.customer.infrastructure.utils.EnderecoHelper.gerarEndereco;
import static com.toystore.customer.infrastructure.utils.EnderecoHelper.gerarEnderecoDTO;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ClienteServiceTest {

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private ClienteMapper clienteMapper;

    @Mock
    private EnderecoService enderecoService;

    @InjectMocks
    private ClienteServiceImpl clienteService;

    private Cliente cliente;
    private ClienteDTO clienteDTO;
    private ClienteAtualizacaoDTO clienteAtualizacaoDTO;
    private EnderecoDTO enderecoDTO;
    @BeforeEach
    void setUp(){
        this.cliente = gerarCliente();
        this.clienteDTO = gerarClienteDTO(cliente);
        this.clienteAtualizacaoDTO = gerarAtualizacaoClienteDTO(clienteDTO);
        this.enderecoDTO = gerarEnderecoDTO(gerarEndereco());
    }

    @DisplayName("Buscar Cliente")
    @Nested
    class BuscarCliente {

        @DisplayName("Deve buscar um Cliente pelo ID fornecido")
        @Test
        void deveBuscarClientePorId() {
            // Arrange
            when(clienteRepository.findById(cliente.getCpf())).thenReturn(Optional.of(cliente));
            when(clienteMapper.toDto(cliente)).thenReturn(gerarClienteDTO(cliente));

            // Act
            var clienteRecebido = clienteService.buscarPorId(cliente.getCpf());

            // Assert
            assertThat(clienteRecebido)
                    .usingRecursiveComparison()
                    .ignoringFields("enderecoId")
                    .isEqualTo(cliente);

            assertThat(clienteRecebido.enderecoId()).isEqualTo(cliente.getEndereco().getId());

            verify(clienteRepository).findById(cliente.getCpf());
        }

        @DisplayName("Deve lançar exceção ao buscar cliente com ID inexistente")
        @Test
        void deveGerarExcecao_QuandoBuscarCliente_PorIdInexistente() {
            // Arrange
            when(clienteRepository.findById(cliente.getCpf())).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> clienteService.buscarPorId(cliente.getCpf()))
                    .isInstanceOf(RecursoNaoEncontradoException.class)
                    .hasMessage("Cliente não encontrado com o cpf: " + cliente.getCpf());
            verify(clienteRepository).findById(cliente.getCpf());
        }

        @DisplayName("Deve retornar uma lista de avaliações salvas")
        @Test
        void deveBuscarTodasAsCliente() {
            // Arrange
            var clientes = List.of(gerarCliente(), gerarCliente(), gerarCliente());
            var clientesDto = clientes.stream()
                    .map(ClienteHelper::gerarClienteDTO)
                    .toList();

            when(clienteRepository.findAll()).thenReturn(clientes);
            when(clienteMapper.toDto(any(Cliente.class)))
                    .thenAnswer(invocation -> {
                        cliente = invocation.getArgument(0);
                        return gerarClienteDTO(cliente);
                    });

            // Act
            List<ClienteDTO> clientesRecebidos = clienteService.buscarTodos();

            // Assert
            assertThat(clientesRecebidos)
                    .isNotNull()
                    .isNotEmpty()
                    .hasSize(3)
                    .containsExactlyElementsOf(clientesDto);
            verify(clienteRepository).findAll();
            verify(clienteMapper, times(3)).toDto(any(Cliente.class));
        }
    }

    @DisplayName("Salvar Cliente")
    @Nested
    class SalvarCliente {

        @DisplayName("Deve salvar Cliente")
        @Test
        void deveSalvarCliente() {
            // Arrange
            when(clienteMapper.toEntity(clienteDTO)).thenReturn(cliente);
            when(clienteMapper.toDto(cliente)).thenReturn(clienteDTO);
            when(enderecoService.buscarPorId(clienteDTO.enderecoId())).thenReturn(enderecoDTO);
            when(clienteRepository.findById(cliente.getCpf())).thenReturn(Optional.empty());
            when(clienteRepository.save(cliente)).thenReturn(cliente);

            // Act
            var clienteSalvo = clienteService.salvar(clienteDTO);

            // Assert
            assertThat(clienteSalvo)
                    .isNotNull()
                    .isInstanceOf(ClienteDTO.class)
                    .isEqualTo(clienteDTO);
            verify(enderecoService).buscarPorId(clienteDTO.enderecoId());
            verify(clienteRepository).findById(cliente.getCpf());
            verify(clienteRepository).save(cliente);
            verify(clienteMapper).toDto(cliente);
            verify(clienteMapper).toEntity(clienteDTO);
        }

        @DisplayName("Deve lançar exceção ao tentar salvar Cliente com endereço inexistente")
        @Test
        void deveGerarExcecao_QuandoSalvarCliente_ComEnderecoInexistente() {
            // Arrange
            when(enderecoService.buscarPorId(clienteDTO.enderecoId())).thenThrow(new
                    RecursoNaoEncontradoException("Endereço não encontrado com id: " + clienteDTO.enderecoId()));

            // Act & Assert
            assertThatThrownBy(() -> clienteService.salvar(clienteDTO))
                    .isInstanceOf(RecursoNaoEncontradoException.class)
                    .hasMessage("Endereço não encontrado com id: " + clienteDTO.enderecoId());
            verify(enderecoService).buscarPorId(clienteDTO.enderecoId());
        }

        @DisplayName("Deve lançar exceção ao tentar salvar Cliente com cpf existente")
        @Test
        void deveGerarExcecao_QuandoSalvarCliente_ComCpfExistente(){
            //Arrenge
            when(clienteRepository.findById(cliente.getCpf())).thenReturn(Optional.of(cliente));

            //Act & Assert
            assertThatThrownBy(() -> clienteService.salvar(clienteDTO))
                    .isInstanceOf(CPFJaCadastradoException.class)
                    .hasMessage("CPF " + cliente.getCpf() + " já cadastrado no sistema");
        }
    }

    @DisplayName("Alterar Cliente")
    @Nested
    class AlterarCliente{

        @DisplayName("Deve alterar Cliente cadastrado")
        @Test
        void deveAlterarClientePorId() {
            // Arrange
            when(clienteMapper.toEntity(clienteDTO)).thenReturn(cliente);
            when(clienteMapper.toDto(cliente)).thenReturn(clienteDTO);
            doNothing().when(clienteMapper).updateFromDto(clienteAtualizacaoDTO, cliente);
            when(enderecoService.buscarPorId(clienteDTO.enderecoId())).thenReturn(enderecoDTO);
            when(clienteRepository.save(cliente)).thenReturn(cliente);
            when(clienteRepository.findById(cliente.getCpf())).thenReturn(Optional.of(cliente));

            // Act
            var clienteSalvo = clienteService.atualizar(clienteDTO.cpf(), clienteAtualizacaoDTO);

            // Assert
            assertThat(clienteSalvo)
                    .isNotNull()
                    .isInstanceOf(ClienteDTO.class)
                    .isEqualTo(clienteDTO);
            verify(enderecoService).buscarPorId(clienteDTO.enderecoId());
            verify(clienteRepository).findById(clienteDTO.cpf());
            verify(clienteRepository).save(cliente);
            verify(clienteMapper).updateFromDto(clienteAtualizacaoDTO, cliente);
            verify(clienteMapper).toEntity(clienteDTO);
            verify(clienteMapper, times(2)).toDto(cliente);
        }

        @DisplayName("Deve lançar exceção ao tentar alterar Cliente com cpf inexistente")
        @Test
        void deveGerarExcecao_QuandoAlterarCliente_PorIdInexistente() {
            // Arrange
            when(clienteRepository.findById(cliente.getCpf())).thenReturn(Optional.empty());
            // Act & Assert
            assertThatThrownBy(() -> clienteService.atualizar(clienteDTO.cpf(), clienteAtualizacaoDTO))
                    .isInstanceOf(RecursoNaoEncontradoException.class)
                    .hasMessage("Cliente não encontrado com o cpf: " + cliente.getCpf());

            verify(clienteRepository).findById(clienteDTO.cpf());
        }

        @DisplayName("Deve lançar exceção ao tentar alterar Cliente por endereço inexistente")
        @Test
        void deveGerarExcecao_QuandoAlterarCliente_PorEnderecoInexistente() {
            // Arrange
            when(clienteMapper.toEntity(clienteDTO)).thenReturn(cliente);
            when(clienteMapper.toDto(cliente)).thenReturn(clienteDTO);
            when(clienteRepository.findById(cliente.getCpf())).thenReturn(Optional.of(cliente));
            when(enderecoService.buscarPorId(clienteDTO.enderecoId())).thenThrow(new
                    RecursoNaoEncontradoException("Endereço não encontrado com id: " + clienteDTO.enderecoId()));

            // Act & Assert
            assertThatThrownBy(() -> clienteService.atualizar(clienteDTO.cpf(), clienteAtualizacaoDTO))
                    .isInstanceOf(RecursoNaoEncontradoException.class)
                    .hasMessage("Endereço não encontrado com id: " + clienteDTO.enderecoId());

            verify(enderecoService).buscarPorId(clienteDTO.enderecoId());
            verify(clienteRepository).findById(clienteDTO.cpf());
            verifyNoMoreInteractions(clienteRepository);
        }
    }

    @DisplayName("Deletar Cliente")
    @Nested
    class DeletarCliente{

        @DisplayName("Deve deletar Cliente")
        @Test
        void deveDeletarClientePorId(){
            // Arrange
            when(clienteRepository.findById(cliente.getCpf()))
                    .thenReturn(Optional.of(cliente));
            doNothing().when(clienteRepository).deleteById(cliente.getCpf());

            // Act
            clienteService.deletarPorId(cliente.getCpf());

            // Assert
            verify(clienteRepository).findById(cliente.getCpf());
            verify(clienteRepository).deleteById(cliente.getCpf());
        }

        @DisplayName("Deve lançar exceção ao tentar deletar Cliente por id inexistente")
        @Test
        void deveGerarExcecao_QuandoDeletarCliente_PorIdInexistente(){
            // Arrange
            when(clienteRepository.findById(cliente.getCpf())).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> clienteService.deletarPorId(cliente.getCpf()))
                    .isInstanceOf(RecursoNaoEncontradoException.class)
                    .hasMessage("Cliente não encontrado com o cpf: " + cliente.getCpf());

            verify(clienteRepository).findById(cliente.getCpf());
        }
    }
}


