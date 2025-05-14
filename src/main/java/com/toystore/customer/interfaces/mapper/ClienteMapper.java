package com.toystore.customer.interfaces.mapper;

import com.toystore.customer.application.dto.ClienteAtualizacaoDTO;
import com.toystore.customer.application.dto.ClienteDTO;
import com.toystore.customer.domain.model.Cliente;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "Spring")
public interface ClienteMapper {

    @Mapping(target = "endereco.id", source = "enderecoId")
    Cliente toEntity (ClienteDTO dto);

    @Mapping(target = "endereco.id", source = "enderecoId")
    Cliente toEntity (ClienteAtualizacaoDTO dto);

    @Mapping(target = "enderecoId", source = "endereco.id")
    ClienteDTO toDto (Cliente entity);

    @Mapping(target = "endereco.id", source = "enderecoId")
    @Mapping(target = "cpf", ignore = true)
    void updateFromDto(ClienteDTO dto, @MappingTarget Cliente entity);

    @Mapping(target = "endereco.id", source = "enderecoId")
    @Mapping(target = "cpf", ignore = true)
    void updateFromDto(ClienteAtualizacaoDTO dto, @MappingTarget Cliente entity);
}
