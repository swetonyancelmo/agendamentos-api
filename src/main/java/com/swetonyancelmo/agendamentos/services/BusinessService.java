package com.swetonyancelmo.agendamentos.services;

import com.swetonyancelmo.agendamentos.dtos.request.CreateBusinessDto;
import com.swetonyancelmo.agendamentos.dtos.response.BusinessDto;
import com.swetonyancelmo.agendamentos.exceptions.BusinessRuleException;
import com.swetonyancelmo.agendamentos.mapper.BusinessMapper;
import com.swetonyancelmo.agendamentos.models.Business;
import com.swetonyancelmo.agendamentos.repositories.BusinessRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BusinessService {

    private final BusinessRepository businessRepository;
    private final BusinessMapper businessMapper;
    private final PasswordEncoder passwordEncoder;

    public BusinessService(BusinessRepository businessRepository, BusinessMapper businessMapper, PasswordEncoder passwordEncoder) {
        this.businessRepository = businessRepository;
        this.businessMapper = businessMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public BusinessDto createBusiness(CreateBusinessDto dto) {
        if(businessRepository.existsByEmail(dto.email())) {
            throw new BusinessRuleException("Email já cadastrado");
        }

        Business business = new Business();
        business.setName(dto.name());
        business.setPhone(dto.phone());
        business.setEmail(dto.email());
        business.setPassword(passwordEncoder.encode(dto.password()));

        Business savedBusiness = businessRepository.save(business);
        return businessMapper.toDto(savedBusiness);
    }

}
