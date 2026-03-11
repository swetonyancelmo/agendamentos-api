package com.swetonyancelmo.agendamentos.mapper;

import com.swetonyancelmo.agendamentos.dtos.request.CreateServiceDto;
import com.swetonyancelmo.agendamentos.dtos.response.ServiceDto;
import com.swetonyancelmo.agendamentos.models.Service;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ServiceMapper {

    Service toEntity(CreateServiceDto createServiceDto);

    Service toEntity(ServiceDto serviceDto);

    ServiceDto toDto(Service service);

}
