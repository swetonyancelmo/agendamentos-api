package com.swetonyancelmo.agendamentos.mapper;

import com.swetonyancelmo.agendamentos.dtos.request.CreateBusinessDto;
import com.swetonyancelmo.agendamentos.dtos.response.BusinessDto;
import com.swetonyancelmo.agendamentos.models.Business;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BusinessMapper {

    Business toEntity(CreateBusinessDto createBusinessDto);

    Business toEntity(BusinessDto businessDto);

    BusinessDto toDto(Business business);

}
