package com.dapm2.ingestion_service.preProcessingElements;

import com.dapm2.ingestion_service.config.SpringContext;
import com.dapm2.ingestion_service.entity.AnonymizationRule;
import com.dapm2.ingestion_service.service.AnonymizationRuleService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.List;
import java.util.UUID;

public class AnonymizationProcess {

    // Retrieve the service once
    private static final AnonymizationRuleService ruleService =
            SpringContext.getBean(AnonymizationRuleService.class);

    private AnonymizationProcess() {
        // prevent instantiation
    }

    /**
     * Apply anonymization (pseudonymization & suppression) to a JSON event.
     * @param json the incoming event JSON node
     * @param dataSourceId the key to lookup rules in the DB
     * @return the modified JSON node
     */
    public static JsonNode apply(JsonNode json, String dataSourceId) {
        AnonymizationRule rule = ruleService.getRuleByDataSourceId(dataSourceId);
        if (rule == null) {
            return json;
        }

        List<String> pseudoFields = rule.getPseudonymization();
        List<String> supFields    = rule.getSuppression();
        ObjectNode node = (ObjectNode) json;

        // Pseudonymization: replace values with random UUIDs
        if (pseudoFields != null && !pseudoFields.isEmpty()) {
            for (String field : pseudoFields) {
                if (node.has(field)) {
                    node.put(field, UUID.randomUUID().toString());
                }
            }
        }

        // Suppression: remove fields entirely
        if (supFields != null && !supFields.isEmpty()) {
            for (String field : supFields) {
                node.remove(field);
            }
        }

        return node;
    }
}