package com.dapm2.ingestion_service.service;

import com.dapm2.ingestion_service.dto.*;
import com.dapm2.ingestion_service.entity.*;
import com.dapm2.ingestion_service.repository.AttributeSettingRepository;
import com.dapm2.ingestion_service.repository.FilterConfigRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class StreamConfigurationService {

    @Autowired
    private FilterConfigRepository filterRepository;

    @Autowired
    private AttributeSettingRepository repository;

    @Autowired
    private ObjectMapper mapper;

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
    public FilterConfig saveFilter(FilterConfigDTO dto) {
        try {
            FilterConfig config = new FilterConfig();
            config.setFilters(mapper.writeValueAsString(dto.getFilters()));
            return filterRepository.save(config);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to save filter config", e);
        }
    }
    public FilterConfig getFilterById(Long id) {
        return filterRepository.findById(id)
                .orElse(null);
    }
    public FilterConfig updateFilter(Long id, FilterConfigDTO dto) {
        Optional<FilterConfig> optional = filterRepository.findById(id);
        if (optional.isEmpty()) {
            throw new RuntimeException("Filter config not found with id: " + id);
        }

        try {
            FilterConfig config = optional.get();
            config.setFilters(mapper.writeValueAsString(dto.getFilters()));
            return filterRepository.save(config);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to update filter config", e);
        }
    }
    public boolean deleteFilter(Long id) {
        if (filterRepository.existsById(id)) {
            filterRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
