package com.toystore.customer.interfaces.controller;

import com.toystore.customer.application.dto.ClienteAtualizacaoDTO;
import com.toystore.customer.application.dto.ClienteDTO;
import com.toystore.customer.application.dto.EnderecoDTO;
import com.toystore.customer.application.usecases.cliente.ClienteService;
import com.toystore.customer.domain.exceptions.CPFJaCadastradoException;
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

import static com.toystore.customer.infrastructure.utils.ClienteHelper.*;
import static com.toystore.customer.infrastructure.utils.EnderecoHelper.gerarEndereco;
import static com.toystore.customer.infrastructure.utils.EnderecoHelper.gerarEnderecoDTO;
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
class ClienteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private ClienteService clienteService;

    private EnderecoDTO enderecoDTO;
    private ClienteDTO clienteDTO;
    private ClienteAtualizacaoDTO clienteAtualizacaoDTO;
    private ClienteDTO clienteDTOSemId;

    @BeforeEach
    void setUp() {
        this.enderecoDTO = gerarEnderecoDTO(gerarEndereco());
        this.clienteDTO = gerarClienteDTO(gerarCliente());
        this.clienteDTOSemId = gerarClienteDTOSemCpf(clienteDTO);
        this.clienteAtualizacaoDTO = gerarAtualizacaoClienteDTO(clienteDTO);
        ClienteController clienteController = new ClienteController(clienteService);

        mockMvc = MockMvcBuilders.standaloneSetup(clienteController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .addFilter((request, response, chain) -> {
                    response.setCharacterEncoding("UTF-8");
                    chain.doFilter(request, response);
                }, "/*")
                .build();
    }

    @DisplayName("Buscar Cliente")
    @Nested
    class buscarCliente{

        @DisplayName("Deve buscar um Cliente pelo ID fornecido")
        @Test
        void deveBuscarClientePorId() throws Exception {
            when(clienteService.buscarPorId(clienteDTO.cpf())).thenReturn(clienteDTO);

            mockMvc.perform(get("/v1/cliente/{cpfCliente}", clienteDTO.cpf()))
                    .andExpect(status().isOk())
                    .andExpect(content().json(asJsonString(clienteDTO)));
        }

        @DisplayName("Deve lançar exceção ao buscar Cliente com cpf inexistente")
        @Test
        void deveGerarExcecao_QuandoBuscarCliente_PorIdInexistente() throws Exception {
            doThrow(new RecursoNaoEncontradoException("Cliente não encontrado com o cpf: " + clienteDTO.cpf()))
                    .when(clienteService).buscarPorId(clienteDTO.cpf());

            mockMvc.perform(get("/v1/cliente/{cpfCliente}", clienteDTO.cpf()))
                    .andExpect(status().isNotFound())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.message")
                            .value("Cliente não encontrado com o cpf: " + clienteDTO.cpf()));

        }

        @DisplayName("Deve retornar uma lista de clientes salvas")
        @Test
        void deveBuscarTodosOsClientes() throws Exception {
            var clientes = List.of(clienteDTO,
                    new ClienteDTO("22233344455", "Ele", "ele@mail.com", null));

            when(clienteService.buscarTodos()).thenReturn(clientes);

            mockMvc.perform(get("/v1/cliente"))
                    .andExpect(status().isOk())
                    .andExpect(content().json(asJsonString(clientes)));
        }
    }

    @DisplayName("Salvar Cliente")
    @Nested
    class SalvarCliente {

        @DisplayName("Deve salvar Cliente")
        @Test
        void deveSalvarCliente() throws Exception {
            when(clienteService.salvar(clienteDTO))
                    .thenReturn(clienteDTO);

            mockMvc.perform(post("/v1/cliente")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(asJsonString(clienteDTO)))
                    .andExpect(status().isCreated())
                    .andExpect(content().json(asJsonString(clienteDTO)));

            verify(clienteService).salvar(clienteDTO);
        }

        @DisplayName("Deve lançar exceção ao tentar salvar Cliente com endereço inexistente")
        @Test
        void deveGerarExcecao_QuandoSalvarCliente_ComEnderecoInexistente() throws Exception {
            doThrow(new RecursoNaoEncontradoException("Endereço não encontrado com id: " +
                    clienteDTO.enderecoId()))
                    .when(clienteService).salvar(clienteDTO);

            mockMvc.perform(post("/v1/cliente")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(asJsonString(clienteDTO)))
                    .andExpect(status().isNotFound())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.message")
                            .value("Endereço não encontrado com id: " +
                                    clienteDTO.enderecoId()));
        }

        @DisplayName("Deve lançar exceção ao tentar salvar Cliente com cpf existente")
        @Test
        void deveGerarExcecao_QuandoSalvarCliente_ComCPFExistente() throws Exception {
            doThrow(new CPFJaCadastradoException("CPF " + clienteDTO.cpf() + " já cadastrado no sistema"))
                    .when(clienteService).salvar(clienteDTO);

            mockMvc.perform(post("/v1/cliente")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(asJsonString(clienteDTO)))
                    .andExpect(status().isConflict())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.message")
                            .value("CPF " + clienteDTO.cpf() + " já cadastrado no sistema"));
        }
    }

    @DisplayName("Alterar Cliente")
    @Nested
    class AlterarCliente {

        @DisplayName("Deve alterar Cliente cadastrada")
        @Test
        void deveAtualizarCliente() throws Exception {
            when(clienteService.atualizar(clienteDTO.cpf(), clienteAtualizacaoDTO)).thenReturn(clienteDTO);

            mockMvc.perform(put("/v1/cliente/{cpfCliente}", clienteDTO.cpf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(asJsonString(clienteAtualizacaoDTO)))
                    .andExpect(status().isOk())
                    .andExpect(content().json(asJsonString(clienteDTO)));
        }

        @DisplayName("Deve lançar exceção ao tentar alterar Cliente com cpf inexistente")
        @Test
        void deveGerarExcecao_QuandoAlterarCliente_PorIdInexistente() throws Exception {
            doThrow(new RecursoNaoEncontradoException("Cliente não encontrado com o cpf: " + clienteDTO.cpf()))
                    .when(clienteService).atualizar(clienteDTO.cpf(), clienteAtualizacaoDTO);

            mockMvc.perform(put("/v1/cliente/{cpfCliente}", clienteDTO.cpf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(asJsonString(clienteAtualizacaoDTO)))
                    .andExpect(status().isNotFound())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.message")
                            .value("Cliente não encontrado com o cpf: " + clienteDTO.cpf()));
        }

        @DisplayName("Deve lançar exceção ao tentar salvar Cliente com endereço inexistente")
        @Test
        void deveGerarExcecao_QuandoAlterarCliente_ComEstadoInexistente() throws Exception {
            doThrow(new RecursoNaoEncontradoException("Endereço não encontrado com id: " +
                    clienteDTO.enderecoId()))
                    .when(clienteService).atualizar(clienteDTO.cpf(), clienteAtualizacaoDTO);

            mockMvc.perform(put("/v1/cliente/{cpfCliente}", clienteDTO.cpf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(asJsonString(clienteAtualizacaoDTO)))
                    .andExpect(status().isNotFound())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.message")
                            .value("Endereço não encontrado com id: " +
                                    clienteDTO.enderecoId()));
        }
    }

    @DisplayName("Deletar Cliente")
    @Nested
    class DeletarCliente {

        @DisplayName("Deve deletar Cliente")
        @Test
        void deveDeletarCliente() throws Exception {
            doNothing().when(clienteService).deletarPorId("11122233344");

            mockMvc.perform(delete("/v1/cliente/{cpfCliente}", "11122233344"))
                    .andExpect(status().isNoContent());
        }

        @DisplayName("Deve lançar exceção ao tentar deletar Cliente por cpf inexistente")
        @Test
        void deveGerarExcecao_QuandoDeletarCliente_PorIdInexistente() throws Exception {
            doThrow(new RecursoNaoEncontradoException("Cliente não encontrado com o cpf: " + clienteDTO.cpf()))
                    .when(clienteService).deletarPorId(clienteDTO.cpf());

            mockMvc.perform(delete("/v1/cliente/{cpfCliente}", clienteDTO.cpf()))
                    .andExpect(status().isNotFound())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.message")
                            .value("Cliente não encontrado com o cpf: " + clienteDTO.cpf()));
        }
    }
    
    
    
}
