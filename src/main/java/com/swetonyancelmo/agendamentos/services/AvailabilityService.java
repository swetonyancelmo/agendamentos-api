package com.swetonyancelmo.agendamentos.services;

import com.swetonyancelmo.agendamentos.dtos.request.AvailabilityRequestDto;
import com.swetonyancelmo.agendamentos.dtos.response.AvailabilityResponseDto;
import com.swetonyancelmo.agendamentos.exceptions.BusinessRuleException;
import com.swetonyancelmo.agendamentos.mapper.AvailabilityMapper;
import com.swetonyancelmo.agendamentos.models.Availability;
import com.swetonyancelmo.agendamentos.models.Business;
import com.swetonyancelmo.agendamentos.repositories.AvailabilityRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class AvailabilityService {

    private final AvailabilityRepository repository;
    private final AvailabilityMapper mapper;

    public AvailabilityService(AvailabilityRepository repository, AvailabilityMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Transactional
    public AvailabilityResponseDto create(Business business, AvailabilityRequestDto dto) {
        if (dto.startTime().isAfter(dto.endTime())) {
            throw new BusinessRuleException("Horário de início não pode ser após o fim.");
        }

        Availability availability = mapper.toEntity(dto);
        availability.setBusiness(business);
        return mapper.toDto(repository.save(availability));
    }

    @Transactional(readOnly = true)
    public List<AvailabilityResponseDto> findAllByBusiness(UUID businessId) {
        return repository.findAllByBusinessId(businessId).stream()
                .map(mapper::toDto)
                .toList();
    }

    @Transactional
    public void delete(UUID availabilityId, Business businessLogged) {
        Availability availability = repository.findById(availabilityId)
                .orElseThrow(() -> new BusinessRuleException("Disponibilidade não encontrada."));

        if (!availability.getBusiness().getId().equals(businessLogged.getId())) {
            throw new BusinessRuleException("Você não tem permissão para deletar esta disponibilidade.");
        }

        repository.deleteById(availabilityId);
    }
}
