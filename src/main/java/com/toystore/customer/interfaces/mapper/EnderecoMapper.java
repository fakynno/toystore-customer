package com.toystore.customer.interfaces.mapper;

import com.toystore.customer.application.dto.EnderecoDTO;
import com.toystore.customer.domain.model.Endereco;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "Spring")
public interface EnderecoMapper {

    Endereco toEntity (EnderecoDTO dto);

    EnderecoDTO toDto (Endereco entity);

    @Mapping(target = "id", ignore = true)
    void updateFromDto (EnderecoDTO dto, @MappingTarget Endereco entity);
}
