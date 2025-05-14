package com.toystore.customer.interfaces.controller;

import com.toystore.customer.application.dto.ClienteAtualizacaoDTO;
import com.toystore.customer.application.dto.ClienteDTO;
import com.toystore.customer.application.usecases.cliente.ClienteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/cliente")
@RequiredArgsConstructor
public class ClienteController {
    
    private final ClienteService clienteService;

    @GetMapping
    @Operation(summary = "Listar todos os clientes", description = "Retorna uma lista de todos os clientes cadastrados")
    @ApiResponse(responseCode = "200", description = "Lista de clientes retornado com sucesso",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ClienteDTO.class)))
    @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    public ResponseEntity<List<ClienteDTO>> listarTodos(){
        return ResponseEntity.ok(clienteService.buscarTodos());
    }

    @GetMapping("/{cpf}")
    @Operation(summary = "Buscar endereço por CPF", description = "Busca uma endereço pelo seu CPF")
    @ApiResponse(responseCode = "200", description = "Cliente encontrado com sucesso",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ClienteDTO.class)))
    @ApiResponse(responseCode = "404", description = "Cliente nao encontrado")
    @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    public ResponseEntity<ClienteDTO> buscar(@PathVariable String cpf){
        return  ResponseEntity.ok(clienteService.buscarPorId(cpf));
    }

    @PostMapping
    @Operation(summary = "Salvar novo endereço", description = "Cadastra um novo endereço")
    @ApiResponse(responseCode = "201", description = "Cliente cadastrado com sucesso",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ClienteDTO.class)))
    @ApiResponse(responseCode = "400", description = "Requisição inválida")
    @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    public ResponseEntity<ClienteDTO> salvar(@Valid @RequestBody ClienteDTO clienteDto){
        return  ResponseEntity.status(HttpStatus.CREATED).body(clienteService.salvar(clienteDto));
    }

    @PutMapping("/{cpf}")
    @Operation(summary = "Atualizar endereço", description = "Atualiza um endereço existente")
    @ApiResponse(responseCode = "201", description = "Cliente atualizado com sucesso",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ClienteDTO.class)))
    @ApiResponse(responseCode = "400", description = "Requisição inválida")
    @ApiResponse(responseCode = "404", description = "Cliente não encontrado")
    @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    public ResponseEntity<ClienteDTO> atualizar(@PathVariable String cpf, @Valid @RequestBody ClienteAtualizacaoDTO clienteAtualizacaoDTO){
        return ResponseEntity.ok(clienteService.atualizar(cpf, clienteAtualizacaoDTO));
    }

    @DeleteMapping("/{cpf}")
    @Operation(summary = "Deletar cliente por ID", description = "Exclui um cliente pelo seu ID")
    @ApiResponse(responseCode = "204", description = "Cliente deletado com sucesso")
    @ApiResponse(responseCode = "404", description = "Cliente não encontrado")
    @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    public ResponseEntity<Void> deletar(@PathVariable String cpf){
        clienteService.deletarPorId(cpf);
        return ResponseEntity.noContent().build();

    }
}
