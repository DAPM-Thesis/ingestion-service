package com.dapm2.ingestion_service.preProcessingElements;

import com.dapm2.ingestion_service.config.SpringContext;
import com.dapm2.ingestion_service.entity.AnonymizationRule;
import com.dapm2.ingestion_service.service.AnonymizationRuleService;
import com.dapm2.ingestion_service.mongo.AnonymizationMappingMongoService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.List;

/**
 * Encapsulates pseudonymization & suppression logic, with
 * rules loaded once at construction.
 */
public class AnonymizationProcess {

    private final List<String> pseudoFields;
    private final List<String> supFields;
    private final String dataSourceId;
    private final AnonymizationMappingMongoService mappingService;

    private AnonymizationProcess(String dataSourceId,
                                 List<String> pseudoFields,
                                 List<String> supFields,
                                 AnonymizationMappingMongoService mappingService) {
        this.dataSourceId   = dataSourceId;
        this.pseudoFields   = pseudoFields;
        this.supFields      = supFields;
        this.mappingService = mappingService;
    }

    /**
     * Factory: loads the rule and mapping‐service once.
     */
    public static AnonymizationProcess fromDataSourceId(String dataSourceId) {
        // load JPA rule service
        AnonymizationRuleService ruleService =
                SpringContext.getBean(AnonymizationRuleService.class);
        AnonymizationRule rule = ruleService.getRuleByDataSourceId(dataSourceId);

        List<String> pseu = rule != null
                ? rule.getPseudonymization()
                : List.of();
        List<String> sup = rule != null
                ? rule.getSuppression()
                : List.of();

        // load Mongo mapping service
        AnonymizationMappingMongoService mappingSvc =
                SpringContext.getBean(AnonymizationMappingMongoService.class);

        return new AnonymizationProcess(dataSourceId, pseu, sup, mappingSvc);
    }

    /**
     * Apply pseudonymization + suppression to the JSON node.
     */
    public JsonNode apply(JsonNode json) {
        ObjectNode node = (ObjectNode) json;

        // pseudonymize each field (reusing existing token if present)
        for (String field : pseudoFields) {
            if (node.has(field)) {
                String original = node.get(field).asText();
                String token = mappingService.anonymize(dataSourceId, field, original);
                node.put(field, token);
            }
        }

        // suppress unwanted fields
        for (String field : supFields) {
            node.remove(field);
        }

        return node;
    }
}
