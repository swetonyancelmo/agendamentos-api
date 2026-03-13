package com.swetonyancelmo.agendamentos.services;

import com.swetonyancelmo.agendamentos.dtos.request.AvailabilityRequestDto;
import com.swetonyancelmo.agendamentos.dtos.response.AvailabilityResponseDto;
import com.swetonyancelmo.agendamentos.exceptions.BusinessRuleException;
import com.swetonyancelmo.agendamentos.mapper.AvailabilityMapper;
import com.swetonyancelmo.agendamentos.models.Availability;
import com.swetonyancelmo.agendamentos.models.Business;
import com.swetonyancelmo.agendamentos.repositories.AvailabilityRepository;
import com.swetonyancelmo.agendamentos.repositories.BusinessRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AvailabilityService {

    private final AvailabilityRepository repository;
    private final BusinessRepository businessRepository;
    private final AvailabilityMapper mapper;

    @Transactional
    public AvailabilityResponseDto create(UUID businessId, AvailabilityRequestDto dto) {
        Business business = businessRepository.findById(businessId)
                .orElseThrow(() -> new BusinessRuleException("Empresa não encontrada."));

        if (dto.startTime().isAfter(dto.endTime())) {
            throw new BusinessRuleException("Horário de início não pode ser após o fim.");
        }

        Availability availability = mapper.toEntity(dto);
        availability.setBusiness(business);
        return mapper.toDto(repository.save(availability));
    }

    public List<AvailabilityResponseDto> findAllByBusiness(UUID businessId) {
        return repository.findAllByBusinessId(businessId).stream()
                .map(mapper::toDto).toList();
    }

    @Transactional
    public void delete(UUID availabilityId) {
        if (!repository.existsById(availabilityId)) {
            throw new BusinessRuleException("Disponibilidade não encontrada.");
        }
        repository.deleteById(availabilityId);
    }
}
