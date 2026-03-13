package com.swetonyancelmo.agendamentos.services;

import com.swetonyancelmo.agendamentos.dtos.request.CreateServiceDto;
import com.swetonyancelmo.agendamentos.dtos.response.ServiceDto;
import com.swetonyancelmo.agendamentos.mapper.ServiceMapper;
import com.swetonyancelmo.agendamentos.models.Business;
import com.swetonyancelmo.agendamentos.repositories.ServiceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class ServiceService {

    private final ServiceRepository serviceRepository;
    private final ServiceMapper serviceMapper;

    public ServiceService(ServiceRepository serviceRepository, ServiceMapper serviceMapper) {
        this.serviceRepository = serviceRepository;
        this.serviceMapper = serviceMapper;
    }

    @Transactional(readOnly = true)
    public List<ServiceDto> listAllServices() {
        return serviceRepository.findAll().stream()
                .map(serviceMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ServiceDto> findServiceByBusiness(UUID businessId){
        return serviceRepository.findByBusinessId(businessId).stream()
                .map(serviceMapper::toDto)
                .toList();
    }

    @Transactional
    public ServiceDto createService(CreateServiceDto createServiceDto, Business business) {
        var service = serviceMapper.toEntity(createServiceDto);
        service.setBusiness(business);
        var savedService = serviceRepository.save(service);
        return serviceMapper.toDto(savedService);
    }

}
