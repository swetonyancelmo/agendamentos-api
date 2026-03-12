package com.swetonyancelmo.agendamentos.mapper;

import com.swetonyancelmo.agendamentos.dtos.request.CreateServiceDto;
import com.swetonyancelmo.agendamentos.dtos.response.ServiceDto;
import com.swetonyancelmo.agendamentos.models.Service;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ServiceMapper {

    Service toEntity(CreateServiceDto createServiceDto);

    @Mapping(source = "business.id", target = "businessId")
    ServiceDto toDto(Service service);

}
