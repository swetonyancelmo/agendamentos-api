package com.swetonyancelmo.agendamentos.mapper;

import com.swetonyancelmo.agendamentos.dtos.request.AvailabilityRequestDto;
import com.swetonyancelmo.agendamentos.dtos.response.AvailabilityResponseDto;
import com.swetonyancelmo.agendamentos.models.Availability;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AvailabilityMapper {
    Availability toEntity(AvailabilityRequestDto dto);
    AvailabilityResponseDto toDto(Availability availability);
}
