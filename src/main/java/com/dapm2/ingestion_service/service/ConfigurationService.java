package com.dapm2.ingestion_service.service;

import com.dapm2.ingestion_service.dto.AttributeSettingDTO;
import com.dapm2.ingestion_service.entity.AttributeSetting;
import com.dapm2.ingestion_service.repository.AttributeSettingRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ConfigurationService {

    private final AttributeSettingRepository repository;
    private final ObjectMapper mapper;

    public ConfigurationService(AttributeSettingRepository repository, ObjectMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public AttributeSetting saveAttributes(AttributeSettingDTO dto) {
        AttributeSetting setting = new AttributeSetting();
        setting.setCaseId(dto.getCaseId());
        setting.setActivity(dto.getActivity());
        setting.setTimeStamp(dto.getTimeStamp());

        if (dto.getAttributes() != null) {
            try {
                setting.setAttributes(mapper.writeValueAsString(dto.getAttributes()));
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Error serializing attributes list", e);
            }
        }

        return repository.save(setting);
    }

    public AttributeSetting getAttributeSettingById(Long id) {
        return repository.findById(id)
                .orElse(null);
    }
    public AttributeSetting updateAttributeSetting(Long id, AttributeSettingDTO dto) {
        Optional<AttributeSetting> optional = repository.findById(id);

        if (optional.isPresent()) {
            AttributeSetting existing = optional.get();
            existing.setCaseId(dto.getCaseId());
            existing.setActivity(dto.getActivity());
            existing.setTimeStamp(dto.getTimeStamp());
            existing.setAttributes(String.join(",", dto.getAttributes())); // Convert list to comma-separated string
            return repository.save(existing);
        } else {
            throw new RuntimeException("Attribute setting not found with id: " + id);
        }
    }
    public boolean deleteAttributeSetting(Long id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
            return true;
        } else {
            return false;
        }
    }

}
