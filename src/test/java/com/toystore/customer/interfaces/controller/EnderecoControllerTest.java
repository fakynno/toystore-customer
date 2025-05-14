package com.toystore.customer.interfaces.controller;

import com.toystore.customer.application.dto.EnderecoDTO;
import com.toystore.customer.application.usecases.endereco.EnderecoService;
import com.toystore.customer.domain.exceptions.GlobalExceptionHandler;
import com.toystore.customer.domain.exceptions.RecursoNaoEncontradoException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static com.toystore.customer.infrastructure.utils.EnderecoHelper.*;
import static com.toystore.customer.infrastructure.utils.GeneralHelper.asJsonString;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@ExtendWith(MockitoExtension.class)
class EnderecoControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Mock
    private EnderecoService enderecoService;

    private EnderecoDTO enderecoDTO;
    private EnderecoDTO enderecoDTOSemId;

    @BeforeEach
    void setUp() {
        enderecoDTO = gerarEnderecoDTO(gerarEndereco());
        enderecoDTOSemId = gerarEnderecoDTOSemId(gerarEndereco());
        EnderecoController enderecoController = new EnderecoController(enderecoService);

        mockMvc = MockMvcBuilders.standaloneSetup(enderecoController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .addFilter((request, response, chain) -> {
                    response.setCharacterEncoding("UTF-8");
                    chain.doFilter(request, response);
                }, "/*")
                .build();
    }

    @DisplayName("Buscar Endereço")
    @Nested
    class buscarEndereco{

        @DisplayName("Deve buscar um Endereço pelo ID fornecido")
        @Test
        void deveBuscarEnderecoPorId() throws Exception {
            when(enderecoService.buscarPorId(enderecoDTO.id())).thenReturn(enderecoDTO);

            mockMvc.perform(get("/v1/endereco/{idEndereco}", enderecoDTO.id()))
                    .andExpect(status().isOk())
                    .andExpect(content().json(asJsonString(enderecoDTO)));
        }

        @DisplayName("Deve lançar exceção ao buscar Endereço com ID inexistente")
        @Test
        void deveGerarExcecao_QuandoBuscarEndereco_PorIdInexistente() throws Exception {
            doThrow(new RecursoNaoEncontradoException("Endereço não encontrado com id: " + enderecoDTO.id()))
                    .when(enderecoService).buscarPorId(enderecoDTO.id());

            mockMvc.perform(get("/v1/endereco/{idEndereco}", enderecoDTO.id()))
                    .andExpect(status().isNotFound())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.message")
                            .value("Endereço não encontrado com id: " + enderecoDTO.id()));

        }

        @DisplayName("Deve retornar uma lista de avaliações salvas")
        @Test
        void deveBuscarTodosOsEnderecos() throws Exception {
            var enderecos = List.of(enderecoDTO,
                    new EnderecoDTO(2L, "2222222", "Bahia", "Camaçari", "Centro", "Unica", "2"));

            when(enderecoService.buscarTodos()).thenReturn(enderecos);

            mockMvc.perform(get("/v1/endereco"))
                    .andExpect(status().isOk())
                    .andExpect(content().json(asJsonString(enderecos)));
        }
    }

    @DisplayName("Salvar Endereço")
    @Nested
    class SalvarEndereco {

        @DisplayName("Deve salvar Endereço")
        @Test
        void deveSalvarEndereco() throws Exception {
            when(enderecoService.salvar(enderecoDTOSemId))
                    .thenReturn(enderecoDTO);

            mockMvc.perform(post("/v1/endereco")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(asJsonString(enderecoDTOSemId)))
                    .andExpect(status().isCreated())
                    .andExpect(content().json(asJsonString(enderecoDTO)));

            verify(enderecoService).salvar(enderecoDTOSemId);
        }
    }

    @DisplayName("Alterar Endereço")
    @Nested
    class AlterarEndereco {

        @DisplayName("Deve alterar Endereço cadastrado")
        @Test
        void deveAtualizarEndereco() throws Exception {
            when(enderecoService.atualizar(enderecoDTO.id(), enderecoDTOSemId)).thenReturn(enderecoDTO);

            mockMvc.perform(put("/v1/endereco/{idEndereco}", enderecoDTO.id())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(asJsonString(enderecoDTOSemId)))
                    .andExpect(status().isOk())
                    .andExpect(content().json(asJsonString(enderecoDTO)));
        }

        @DisplayName("Deve lançar exceção ao tentar alterar Endereço com id inexistente")
        @Test
        void deveGerarExcecao_QuandoAlterarEndereco_PorIdInexistente() throws Exception {
            doThrow(new RecursoNaoEncontradoException("Endereço não encontrado com id: " + enderecoDTO.id()))
                    .when(enderecoService).atualizar(enderecoDTO.id(), enderecoDTOSemId);

            mockMvc.perform(put("/v1/endereco/{idEndereco}", enderecoDTO.id())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(asJsonString(enderecoDTOSemId)))
                    .andExpect(status().isNotFound())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.message")
                            .value("Endereço não encontrado com id: " + enderecoDTO.id()));
        }
    }

    @DisplayName("Deletar Endereço")
    @Nested
    class DeletarEndereco {

        @DisplayName("Deve deletar Endereço")
        @Test
        void deveDeletarEndereco() throws Exception {
            doNothing().when(enderecoService).deletarPorId(1L);

            mockMvc.perform(delete("/v1/endereco/{idEndereco}", 1L))
                    .andExpect(status().isNoContent());
        }

        @DisplayName("Deve lançar exceção ao tentar deletar Endereço por id inexistente")
        @Test
        void deveGerarExcecao_QuandoDeletarEndereco_PorIdInexistente() throws Exception {
            doThrow(new RecursoNaoEncontradoException("Endereço não encontrado com id: " + enderecoDTO.id()))
                    .when(enderecoService).deletarPorId(enderecoDTO.id());

            mockMvc.perform(delete("/v1/endereco/{idEndereco}", enderecoDTO.id()))
                    .andExpect(status().isNotFound())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.message")
                            .value("Endereço não encontrado com id: " + enderecoDTO.id()));
        }
    }
}
