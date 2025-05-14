package com.toystore.customer.application.usecases.endereco;

import com.toystore.customer.application.dto.EnderecoDTO;
import com.toystore.customer.domain.exceptions.RecursoNaoEncontradoException;
import com.toystore.customer.domain.model.Endereco;
import com.toystore.customer.domain.repository.EnderecoRepository;
import com.toystore.customer.infrastructure.utils.EnderecoHelper;
import com.toystore.customer.interfaces.mapper.EnderecoMapper;
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

import static com.toystore.customer.infrastructure.utils.EnderecoHelper.gerarEndereco;
import static com.toystore.customer.infrastructure.utils.EnderecoHelper.gerarEnderecoDTO;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EnderecoServiceTest {

    @Mock
    private EnderecoRepository enderecoRepository;

    @Mock
    private EnderecoMapper enderecoMapper;

    @InjectMocks
    private EnderecoServiceImpl enderecoService;


    private Endereco endereco;
    private EnderecoDTO enderecoDTO;

    @BeforeEach
    void setup(){
        this.endereco = gerarEndereco();
        this.enderecoDTO = gerarEnderecoDTO(endereco);
    }

    @DisplayName("Buscar Endereço")
    @Nested
    class BuscarEndereco{

        @DisplayName("Deve buscar Endereço pelo id")
        @Test
        void deveBuscarEndereco(){
            //Arrange
            when(enderecoRepository.findById(endereco.getId())).thenReturn(Optional.of(endereco));
            when(enderecoMapper.toDto(endereco)).thenReturn(enderecoDTO);

            //Act
            var enderecoRecebido = enderecoService.buscarPorId(endereco.getId());

            //Assert
            assertThat(enderecoRecebido)
                    .usingRecursiveComparison()
                    .isEqualTo(endereco);

            verify(enderecoRepository).findById(endereco.getId());
        }

        @DisplayName("Deve lançar exceção ao buscar Endereço com id inexistente")
        @Test
        void deveGerarExcecao_QuandoBuscarEndereco_PorIdInexistente(){
            //Arrange
            when(enderecoRepository.findById(endereco.getId())).thenReturn(Optional.empty());

            //Act & Assert
            assertThatThrownBy(() -> enderecoService.buscarPorId(endereco.getId()))
                    .isInstanceOf(RecursoNaoEncontradoException.class)
                    .hasMessage("Endereço não encontrado com id: " + endereco.getId());

            verify(enderecoRepository).findById(endereco.getId());
        }

        @DisplayName("Deve retornar um lista de Endereços cadastrados")
        @Test
        void deveBuscarTodosEnderecos(){
            //Arrange
            var enderecos = List.of(gerarEndereco(),
                    gerarEndereco(),
                    gerarEndereco());
            var enderecosDTO = enderecos.stream()
                    .map(EnderecoHelper::gerarEnderecoDTO)
                    .toList();

            when(enderecoRepository.findAll()).thenReturn(enderecos);
            when(enderecoMapper.toDto(any(Endereco.class)))
                    .thenAnswer(invocation -> {
                        endereco = invocation.getArgument(0);
                        return gerarEnderecoDTO(endereco);
                    });

            //Act
            List<EnderecoDTO> enderecosRecebidos = enderecoService.buscarTodos();

            //Assert
            assertThat(enderecosRecebidos).containsExactlyElementsOf(enderecosDTO);
            verify(enderecoRepository).findAll();
            verify(enderecoMapper, times(3)).toDto(any(Endereco.class));
        }

    }

    @DisplayName("Salvar Endereço")
    @Nested
    class SalvarEndereco {

        @DisplayName("Deve salvar Endereço")
        @Test
        void deveSalvarEndereco() {
            // Arrange
            when(enderecoMapper.toEntity(enderecoDTO)).thenReturn(endereco);
            when(enderecoMapper.toDto(endereco)).thenReturn(enderecoDTO);
            when(enderecoRepository.save(endereco)).thenReturn(endereco);

            // Act
            var enderecoSalvo = enderecoService.salvar(enderecoDTO);

            // Assert
            assertThat(enderecoSalvo)
                    .isNotNull()
                    .isInstanceOf(EnderecoDTO.class)
                    .isEqualTo(enderecoDTO);
            verify(enderecoRepository).save(endereco);
            verify(enderecoMapper).toDto(endereco);
            verify(enderecoMapper).toEntity(enderecoDTO);
        }
    }

    @DisplayName("Alterar Endereço")
    @Nested
    class AlterarEndereco{

        @DisplayName("Deve alterar Endereço cadastrado")
        @Test
        void deveAlterarEnderecoPorId() {
            // Arrange
            when(enderecoMapper.toEntity(enderecoDTO)).thenReturn(endereco);
            when(enderecoMapper.toDto(endereco)).thenReturn(enderecoDTO);
            doNothing().when(enderecoMapper).updateFromDto(enderecoDTO, endereco);
            when(enderecoRepository.save(endereco)).thenReturn(endereco);
            when(enderecoRepository.findById(endereco.getId())).thenReturn(Optional.of(endereco));

            // Act
            var enderecoSalvo = enderecoService.atualizar(enderecoDTO.id(), enderecoDTO);

            // Assert
            assertThat(enderecoSalvo)
                    .isNotNull()
                    .isInstanceOf(EnderecoDTO.class)
                    .isEqualTo(enderecoDTO);
            verify(enderecoRepository).findById(enderecoDTO.id());
            verify(enderecoRepository).save(endereco);
            verify(enderecoMapper).updateFromDto(enderecoDTO, endereco);
            verify(enderecoMapper).toEntity(enderecoDTO);
            verify(enderecoMapper, times(2)).toDto(endereco);
        }

        @DisplayName("Deve lançar exceção ao tentar alterar Endereço com id inexistente")
        @Test
        void deveGerarExcecao_QuandoAlterarEndereco_PorIdInexistente() {
            // Arrange
            when(enderecoRepository.findById(endereco.getId())).thenReturn(Optional.empty());
            // Act & Assert
            assertThatThrownBy(() -> enderecoService.atualizar(enderecoDTO.id(), enderecoDTO))
                    .isInstanceOf(RecursoNaoEncontradoException.class)
                    .hasMessage("Endereço não encontrado com id: " + endereco.getId());

            verify(enderecoRepository).findById(enderecoDTO.id());
        }
    }

    @DisplayName("Deletar Endereço")
    @Nested
    class DeletarEndereco{

        @DisplayName("Deve deletar Endereço")
        @Test
        void deveDeletarEnderecoPorId(){
            // Arrange
            when(enderecoRepository.findById(endereco.getId()))
                    .thenReturn(Optional.of(endereco));
            doNothing().when(enderecoRepository).deleteById(endereco.getId());

            // Act
            enderecoService.deletarPorId(endereco.getId());

            // Assert
            verify(enderecoRepository).findById(endereco.getId());
            verify(enderecoRepository).deleteById(endereco.getId());
        }

        @DisplayName("Deve lançar exceção ao tentar deletar Endereço por id inexistente")
        @Test
        void deveGerarExcecao_QuandoDeletarEndereco_PorIdInexistente(){
            // Arrange
            when(enderecoRepository.findById(endereco.getId())).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> enderecoService.deletarPorId(endereco.getId()))
                    .isInstanceOf(RecursoNaoEncontradoException.class)
                    .hasMessage("Endereço não encontrado com id: " + endereco.getId());

            verify(enderecoRepository).findById(endereco.getId());
        }
    }

}
