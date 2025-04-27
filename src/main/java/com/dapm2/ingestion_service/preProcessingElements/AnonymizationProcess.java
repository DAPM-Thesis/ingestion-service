package com.dapm2.ingestion_service.preProcessingElements;

import com.dapm2.ingestion_service.config.SpringContext;
import com.dapm2.ingestion_service.entity.AnonymizationRule;
import com.dapm2.ingestion_service.service.AnonymizationRuleService;
import com.dapm2.ingestion_service.mongo.AnonymizationMappingService;
import com.dapm2.ingestion_service.utils.AppConstants;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class AnonymizationProcess {

    private final String dataSourceId;
    private final List<String> pseudoFields;
    private final List<String> supFields;
    private final String uniqueField;
    private final AnonymizationMappingService mappingService;

    private AnonymizationProcess(String dataSourceId,
                                 List<String> pseudoFields,
                                 List<String> supFields,
                                 String uniqueField,
                                 AnonymizationMappingService mappingService) {
        this.dataSourceId   = dataSourceId;
        this.pseudoFields   = pseudoFields;
        this.supFields      = supFields;
        this.uniqueField    = uniqueField;
        this.mappingService = mappingService;
    }

    public static AnonymizationProcess fromDataSourceId(String dataSourceId) {
        var ruleService = SpringContext.getBean(AnonymizationRuleService.class);
        AnonymizationRule rule = ruleService.getRuleByDataSourceId(dataSourceId);

        List<String> pseu = rule != null ? rule.getPseudonymization() : List.of();
        List<String> sup  = rule != null ? rule.getSuppression()      : List.of();
        String uniq      = rule != null ? rule.getUniqueField()      : null;

        var mappingSvc = SpringContext.getBean(AnonymizationMappingService.class);
        return new AnonymizationProcess(dataSourceId, pseu, sup, uniq, mappingSvc);
    }

    public JsonNode apply(JsonNode json) {
        // 1) quick exit if no fields to touch
        if (pseudoFields.isEmpty() && supFields.isEmpty()) return json;

        // 2) keep raw copy
        JsonNode raw = json.deepCopy();
        ObjectNode node = (ObjectNode) json;

        // 3) try to find a prior wrapper by uniqueField
        if (uniqueField != null && raw.has(uniqueField)) {
            String mappingId = UUID.randomUUID().toString();

            //Pseudonymous
            for (String field : pseudoFields) {
                String originalValue = node.get(field).asText();
                String pseudoValue = mappingService.pseudonym(dataSourceId,uniqueField, field, raw);
                if (!Objects.equals(originalValue, pseudoValue))
                    node.put(field, pseudoValue);
            }
            // suppress unwanted fields
            for (String field : supFields) {
                node.remove(field);
            }
            // build wrapper doc
            ObjectNode wrapper = JsonNodeFactory.instance.objectNode();
            wrapper.put(AppConstants.MAPPING_Table_ID, mappingId);
            wrapper.set(AppConstants.Raw_Data, raw);
            wrapper.set(AppConstants.Anonymized_Data, node);
            // save wrapper
            mappingService.saveRawDataAnonymization(dataSourceId, wrapper);
            // tag and return
            node.put(AppConstants.MAPPING_Table_ID, mappingId);
        }
        return node;
    }
}
