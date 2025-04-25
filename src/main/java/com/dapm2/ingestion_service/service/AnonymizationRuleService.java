package com.dapm2.ingestion_service.service;

import com.dapm2.ingestion_service.dto.AnonymizationRuleDTO;
import com.dapm2.ingestion_service.entity.AnonymizationRule;
import com.dapm2.ingestion_service.mongo.AnonymizationMappingDoc;
import com.dapm2.ingestion_service.repository.AnonymizationRuleRepository;
import com.dapm2.ingestion_service.utils.AppConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class AnonymizationRuleService {

    @Autowired
    private AnonymizationRuleRepository repository;
    @Autowired
    private MongoTemplate mongoTemplate;
    public AnonymizationRule saveRule(AnonymizationRuleDTO request) {
        AnonymizationRule rule = new AnonymizationRule();
        rule.setDataSourceId(request.getDataSourceId());
        rule.setPseudonymization(request.getPseudonymization());
        rule.setSuppression(request.getSuppression());
        rule.setStatus(
                request.getStatus() != null
                        ? request.getStatus()
                        : AppConstants.STATUS_ACTIVE
        );
        return repository.save(rule);
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
        AnonymizationRule rule = optional.get();
        rule.setDataSourceId(request.getDataSourceId());
        rule.setPseudonymization(request.getPseudonymization());
        rule.setSuppression(request.getSuppression());
        if (request.getStatus() != null) {
            rule.setStatus(request.getStatus());
        }
        return repository.save(rule);
    }

    public boolean updateRuleStatus(Long id, String status) {
        return repository.findById(id)
                .map(r -> {
                    r.setStatus(status);
                    repository.save(r);
                    return true;
                })
                .orElse(false);
    }

    public boolean deleteRule(Long id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
            return true;
        }
        return false;
    }

    /**
     * Build a per‐source collection name.
     */
    private String collectionFor(String dataSourceId) {
        return "anon_map_" + dataSourceId;
    }

    /**
     * Return an anonymized token for the given original value,
     * creating a new mapping in Mongo if none exists.
     */
    public String anonymizeValue(String dataSourceId, String originalValue) {
        String collection = collectionFor(dataSourceId);

        Query q = Query.query(Criteria.where("originalValue").is(originalValue));
        AnonymizationMappingDoc existing =
                mongoTemplate.findOne(q, AnonymizationMappingDoc.class, collection);

        if (existing != null) {
            return existing.getAnonymizedValue();
        }

        String token = UUID.randomUUID().toString();
        AnonymizationMappingDoc doc = AnonymizationMappingDoc.builder()
                .dataSourceId(dataSourceId)
                .originalValue(originalValue)
                .anonymizedValue(token)
                .createdAt(Instant.now())
                .build();
        mongoTemplate.save(doc, collection);
        return token;
    }

    /**
     * Reverse‐lookup the original value from a pseudonym.
     */
    public Optional<String> deanonymizeValue(String dataSourceId, String token) {
        String collection = collectionFor(dataSourceId);

        Query q = Query.query(Criteria.where("anonymizedValue").is(token));
        AnonymizationMappingDoc doc =
                mongoTemplate.findOne(q, AnonymizationMappingDoc.class, collection);

        return Optional.ofNullable(doc)
                .map(AnonymizationMappingDoc::getOriginalValue);
    }
}