package com.swetonyancelmo.agendamentos.services;

import com.swetonyancelmo.agendamentos.dtos.request.CreateServiceDto;
import com.swetonyancelmo.agendamentos.dtos.response.ServiceDto;
import com.swetonyancelmo.agendamentos.mapper.ServiceMapper;
import com.swetonyancelmo.agendamentos.repositories.BusinessRepository;
import com.swetonyancelmo.agendamentos.repositories.ServiceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ServiceService {

    private final ServiceRepository serviceRepository;
    private final BusinessRepository businessRepository;
    private final ServiceMapper serviceMapper;

    public ServiceService(ServiceRepository serviceRepository, BusinessRepository businessRepository, ServiceMapper serviceMapper) {
        this.serviceRepository = serviceRepository;
        this.businessRepository = businessRepository;
        this.serviceMapper = serviceMapper;
    }

    @Transactional(readOnly = true)
    public List<ServiceDto> listAllServices() {
        return serviceRepository.findAll().stream()
                .map(serviceMapper::toDto)
                .toList();
    }

}
