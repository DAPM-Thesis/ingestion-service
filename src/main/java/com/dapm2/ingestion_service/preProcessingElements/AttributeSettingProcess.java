package com.dapm2.ingestion_service.preProcessingElements;

import com.dapm2.ingestion_service.config.SpringContext;
import com.dapm2.ingestion_service.entity.AttributeSetting;
import com.dapm2.ingestion_service.service.StreamConfigurationService;
import com.dapm2.ingestion_service.utils.AppConstants;
import com.dapm2.ingestion_service.utils.TimestampConverterISO;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import communication.message.impl.event.Attribute;
import communication.message.impl.event.Event;

import java.util.*;

public class AttributeSettingProcess {

    private final String caseIdField;
    private final String activityField;
    private final String timestampField;
    private final List<String> attributes;

    private AttributeSettingProcess(String caseIdField, String activityField, String timestampField, List<String> attributes) {
        this.caseIdField = caseIdField;
        this.activityField = activityField;
        this.timestampField = timestampField;
        this.attributes = attributes;
    }

    // Load from DB using real Spring service
    public static AttributeSettingProcess fromSettingId(Long id) {
        StreamConfigurationService service = SpringContext.getBean(StreamConfigurationService.class);
        ObjectMapper mapper = SpringContext.getBean(ObjectMapper.class);

        AttributeSetting setting = service.getAttributeSettingById(id);
        if (setting == null) {
            throw new RuntimeException("No AttributeSetting found for id: " + id);
        }

        List<String> attributesList = new ArrayList<>();
        try {
            attributesList = mapper.readValue(setting.getAttributes(), List.class);
        } catch (Exception e) {
            System.err.println("Failed to parse attribute list: " + e.getMessage());
        }

        return new AttributeSettingProcess(
                setting.getCaseId(),
                setting.getActivity(),
                setting.getTimeStamp(),
                attributesList
        );
    }

    public Event extractEvent(JsonNode json) {
        String caseId = extractField(json, caseIdField, "unknown_case");
        String activity = extractField(json, activityField, "unknown_type");

        Object rawTimestamp = extractRawValue(json, timestampField);
        String timestamp = TimestampConverterISO.toISO(rawTimestamp);
        // 1) the attributes from your DB-configured list
        Set<Attribute<?>> eventAttributes = new HashSet<>();
        for (String attr : attributes) {
            String value = extractField(json, attr, "");
            eventAttributes.add(new Attribute<>(attr, value));
        }
        // 2) now add mappingTableRef if present in the JSON
        JsonNode mappingNode = json.get(AppConstants.MAPPING_Table_REFERENCE);
        if (mappingNode != null && !mappingNode.isNull()) {
            String mappingRef = mappingNode.asText();
            eventAttributes.add(new Attribute<>(AppConstants.MAPPING_Table_REFERENCE, mappingRef));
        }
        return new Event(caseId, activity, timestamp, eventAttributes);
    }

    private String extractField(JsonNode json, String fieldPath, String defaultVal) {
        JsonNode node = json;
        for (String part : fieldPath.split("\\.")) {
            node = node.path(part);
        }
        return node.isMissingNode() ? defaultVal : node.asText();
    }

    private Object extractRawValue(JsonNode json, String fieldPath) {
        JsonNode node = json;
        for (String part : fieldPath.split("\\.")) {
            node = node.path(part);
        }
        if (node.isMissingNode()) return null;
        return node.isNumber() ? node.longValue() : node.asText();
    }
}
