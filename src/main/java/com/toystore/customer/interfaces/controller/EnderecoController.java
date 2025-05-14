package com.toystore.customer.interfaces.controller;

import com.toystore.customer.application.dto.EnderecoDTO;
import com.toystore.customer.application.usecases.endereco.EnderecoService;
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
@RequestMapping("/v1/endereco")
@RequiredArgsConstructor
public class EnderecoController {
    
    private final EnderecoService enderecoService;

    @GetMapping
    @Operation(summary = "Listar todos os endereços", description = "Retorna uma lista de todos os endereços cadastrados")
    @ApiResponse(responseCode = "200", description = "Lista de endereços retornado com sucesso",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = EnderecoDTO.class)))
    @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    public ResponseEntity<List<EnderecoDTO>> listarTodos(){
        return ResponseEntity.ok(enderecoService.buscarTodos());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar endereço por ID", description = "Busca uma endereço pelo seu ID")
    @ApiResponse(responseCode = "200", description = "Endereço encontrado com sucesso",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = EnderecoDTO.class)))
    @ApiResponse(responseCode = "404", description = "Endereço nao encontrado")
    @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    public ResponseEntity<EnderecoDTO> buscar(@PathVariable Long id){
        return  ResponseEntity.ok(enderecoService.buscarPorId(id));
    }

    @PostMapping
    @Operation(summary = "Salvar novo endereço", description = "Cadastra um novo endereço")
    @ApiResponse(responseCode = "201", description = "Endereço cadastrado com sucesso",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = EnderecoDTO.class)))
    @ApiResponse(responseCode = "400", description = "Requisição inválida")
    @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    public ResponseEntity<EnderecoDTO> salvar(@Valid @RequestBody EnderecoDTO enderecoDto){
        return  ResponseEntity.status(HttpStatus.CREATED).body(enderecoService.salvar(enderecoDto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar endereço", description = "Atualiza um endereço existente")
    @ApiResponse(responseCode = "201", description = "Endereço atualizado com sucesso",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = EnderecoDTO.class)))
    @ApiResponse(responseCode = "400", description = "Requisição inválida")
    @ApiResponse(responseCode = "404", description = "Endereço não encontrado")
    @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    public ResponseEntity<EnderecoDTO> atualizar(@PathVariable Long id, @Valid @RequestBody EnderecoDTO enderecoDto){
        return ResponseEntity.ok(enderecoService.atualizar(id, enderecoDto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar endereço por ID", description = "Exclui um endereço pelo seu ID")
    @ApiResponse(responseCode = "204", description = "Endereço deletado com sucesso")
    @ApiResponse(responseCode = "404", description = "Endereço não encontrado")
    @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    public ResponseEntity<Void> deletar(@PathVariable Long id){
        enderecoService.deletarPorId(id);
        return ResponseEntity.noContent().build();

    }
}
