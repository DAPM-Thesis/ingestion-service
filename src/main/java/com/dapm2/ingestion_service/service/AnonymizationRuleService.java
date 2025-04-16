package com.dapm2.ingestion_service.service;

import com.dapm2.ingestion_service.dto.AnonymizationRuleDTO;
import com.dapm2.ingestion_service.entity.AnonymizationRule;
import com.dapm2.ingestion_service.repository.AnonymizationRuleRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AnonymizationRuleService {

    @Autowired
    private AnonymizationRuleRepository repository;

    @Autowired
    private ObjectMapper objectMapper;

    public AnonymizationRule saveRule(AnonymizationRuleDTO request) {
        try {
            String jsonRules = objectMapper.writeValueAsString(request.getRules());
            AnonymizationRule rule = new AnonymizationRule();
            rule.setDataSourceId(request.getDataSourceId());
            rule.setRules(jsonRules);
            return repository.save(rule);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error serializing rules", e);
        }
    }

    public AnonymizationRule getRuleById(Long id) {
        return repository.findById(id).orElse(null);
    }

    public AnonymizationRule getRuleByDataSourceId(String dataSourceId) {
        return repository.findFirstByDataSourceId(dataSourceId).orElse(null);
    }

    public List<AnonymizationRule> getAllRules() {
        return repository.findAll();
    }

    public AnonymizationRule updateRule(Long id, AnonymizationRuleDTO request) {
        Optional<AnonymizationRule> optional = repository.findById(id);
        if (optional.isEmpty()) {
            throw new RuntimeException("Anonymization rule not found with id: " + id);
        }

        try {
            AnonymizationRule rule = optional.get();
            rule.setDataSourceId(request.getDataSourceId());
            rule.setRules(objectMapper.writeValueAsString(request.getRules()));
            return repository.save(rule);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to update rules", e);
        }
    }

    public boolean deleteRule(Long id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
            return true;
        }
        return false;
    }
}
