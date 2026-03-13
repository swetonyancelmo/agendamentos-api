package com.swetonyancelmo.agendamentos.mapper;

import com.swetonyancelmo.agendamentos.dtos.request.AvailabilityRequestDto;
import com.swetonyancelmo.agendamentos.dtos.response.AvailabilityResponseDto;
import com.swetonyancelmo.agendamentos.models.Availability;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AvailabilityMapper {

    Availability toEntity(AvailabilityRequestDto dto);

    @Mapping(source = "business.id", target = "businessId")
    AvailabilityResponseDto toDto(Availability availability);
}
